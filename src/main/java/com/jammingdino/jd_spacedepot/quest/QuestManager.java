package com.jammingdino.jd_spacedepot.quest;

import com.jammingdino.jd_spacedepot.registry.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestManager {
    public static List<DepotQuest> getDailyQuests(long daySeed) {
        List<DepotQuest> quests = new ArrayList<>();
        // Create a seeded random so Client and Server generate the exact same list
        RandomSource random = RandomSource.create(daySeed * 987654321L); // Multiply to shuffle bits

        // 3 Special Quests
        for (int i = 0; i < 3; i++) {
            quests.add(new DepotQuest(
                    generateDeterministicId(random), // FIX: Use seeded UUID
                    "Priority Request #" + (i + 1),
                    Ingredient.of(Items.DIAMOND, Items.EMERALD, Items.GOLD_INGOT),
                    5 + random.nextInt(5),
                    new ItemStack(ModItems.SOLAR_CHUNK.get(), 1 + random.nextInt(2)),
                    true
            ));
        }

        // 5 Common Quests
        quests.add(new DepotQuest(generateDeterministicId(random), "Lumber Shipment", Ingredient.of(Items.OAK_LOG), 16, new ItemStack(ModItems.SOLAR_COIN.get(), 2), false));
        quests.add(new DepotQuest(generateDeterministicId(random), "Iron Supply", Ingredient.of(Items.IRON_INGOT), 8, new ItemStack(ModItems.SOLAR_COIN.get(), 4), false));
        quests.add(new DepotQuest(generateDeterministicId(random), "Food Rations", Ingredient.of(Items.BREAD), 32, new ItemStack(ModItems.SOLAR_COIN.get(), 1), false));
        quests.add(new DepotQuest(generateDeterministicId(random), "Cobble Transport", Ingredient.of(Items.COBBLESTONE), 64, new ItemStack(ModItems.SOLAR_COIN.get(), 1), false));
        quests.add(new DepotQuest(generateDeterministicId(random), "Fuel Logistics", Ingredient.of(Items.COAL), 16, new ItemStack(ModItems.SOLAR_COIN.get(), 3), false));

        return quests;
    }

    // Helper to generate a UUID based on the RandomSource state
    private static UUID generateDeterministicId(RandomSource random) {
        return new UUID(random.nextLong(), random.nextLong());
    }
}