package com.jammingdino.jd_spacedepot.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

public record DepotQuest(
        UUID id,
        String title,
        List<QuestRequirement> requirements,
        ItemStack reward,
        boolean isSpecial
) {
    // Codec for parsing the JSON file (ID is generated later)
    public static final Codec<DepotQuest> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("title").forGetter(DepotQuest::title),
            QuestRequirement.CODEC.listOf().fieldOf("requirements").forGetter(DepotQuest::requirements),
            ItemStack.CODEC.fieldOf("reward").forGetter(DepotQuest::reward),
            Codec.BOOL.optionalFieldOf("is_special", false).forGetter(DepotQuest::isSpecial)
    ).apply(instance, (title, reqs, reward, special) -> new DepotQuest(UUID.randomUUID(), title, reqs, reward, special)));

    public Component getDisplayName() {
        return Component.literal(title);
    }

    // Helper to create a new instance with a specific ID
    public DepotQuest withId(UUID newId) {
        return new DepotQuest(newId, title, requirements, reward, isSpecial);
    }

    // Helper to get total required count of all items (for simple display)
    public int getTotalRequiredCount() {
        return requirements.stream().mapToInt(QuestRequirement::count).sum();
    }
}