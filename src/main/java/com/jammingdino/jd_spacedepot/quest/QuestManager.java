package com.jammingdino.jd_spacedepot.quest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jammingdino.jd_spacedepot.SpaceDepot;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

import java.util.*;

public class QuestManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static QuestManager INSTANCE;

    // Data
    private final List<DepotQuest> mainQuests = new ArrayList<>();
    private WeightedRandomList<WeightedEntry.Wrapper<DepotQuest>> dailyQuestPool = WeightedRandomList.create();

    public QuestManager() {
        super(GSON, "spacedepot_quests");
        INSTANCE = this;
    }

    public static QuestManager get() {
        return INSTANCE;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
        mainQuests.clear();
        List<WeightedEntry.Wrapper<DepotQuest>> poolBuilder = new ArrayList<>();

        SpaceDepot.LOGGER.info("Loading Space Depot Quests...");

        resources.forEach((location, json) -> {
            try {
                JsonObject obj = json.getAsJsonObject();
                String type = obj.has("type") ? obj.get("type").getAsString() : "main";

                if (type.equals("daily_pool")) {
                    parseDailyPool(obj, poolBuilder);
                } else {
                    // Parse Main Quest
                    DepotQuest quest = DepotQuest.DIRECT_CODEC.parse(JsonOps.INSTANCE, obj)
                            .getOrThrow(e -> new IllegalStateException("Failed to parse quest: " + e))
                            .withId(UUID.nameUUIDFromBytes(location.toString().getBytes()));

                    // Force isSpecial = false for Main Quests (Standard look, at bottom)
                    quest = new DepotQuest(quest.id(), quest.title(), quest.requirements(), quest.reward(), false);
                    mainQuests.add(quest);
                }

            } catch (Exception e) {
                SpaceDepot.LOGGER.error("Failed to load quest file: {}", location, e);
            }
        });

        mainQuests.sort(Comparator.comparing(DepotQuest::title));
        this.dailyQuestPool = WeightedRandomList.create(poolBuilder);

        SpaceDepot.LOGGER.info("Loaded {} Main Quests and {} Daily Pool Entries.", mainQuests.size(), poolBuilder.size());
    }

    private void parseDailyPool(JsonObject json, List<WeightedEntry.Wrapper<DepotQuest>> poolBuilder) {
        if (json.has("entries")) {
            json.getAsJsonArray("entries").forEach(entryElem -> {
                JsonObject entryObj = entryElem.getAsJsonObject();
                int weight = entryObj.get("weight").getAsInt();
                JsonObject questObj = entryObj.getAsJsonObject("quest");

                DepotQuest quest = DepotQuest.DIRECT_CODEC.parse(JsonOps.INSTANCE, questObj)
                        .getOrThrow(e -> new IllegalStateException("Failed to parse pool entry"))
                        .withId(UUID.randomUUID());

                poolBuilder.add(WeightedEntry.wrap(quest, weight));
            });
        }
    }

    public List<DepotQuest> getQuestsForDay(long day) {
        List<DepotQuest> combined = new ArrayList<>();

        // 1. Add Daily Quests FIRST (Top of list)
        if (!dailyQuestPool.isEmpty()) {
            RandomSource random = RandomSource.create(day * 987654321L);

            for (int i = 0; i < 5; i++) {
                dailyQuestPool.getRandom(random).ifPresent(wrapper -> {
                    DepotQuest template = wrapper.data();
                    UUID dailyId = new UUID(random.nextLong(), random.nextLong());

                    // Force isSpecial = true for Daily Quests (Premium look)
                    combined.add(new DepotQuest(dailyId, template.title(), template.requirements(), template.reward(), true));
                });
            }
        }

        // 2. Add Main Quests SECOND (Bottom of list)
        combined.addAll(mainQuests);

        return combined;
    }

    public Optional<DepotQuest> findQuest(long day, UUID id) {
        return getQuestsForDay(day).stream().filter(q -> q.id().equals(id)).findFirst();
    }
}