package com.jammingdino.jd_spacedepot.quest;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.UUID;

public record DepotQuest(
        UUID id,
        String title,
        Ingredient requiredItem,
        int requiredCount,
        ItemStack reward,
        boolean isSpecial
) {
    public Component getDisplayName() {
        return Component.literal(title);
    }
}