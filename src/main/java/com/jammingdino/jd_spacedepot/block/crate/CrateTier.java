package com.jammingdino.jd_spacedepot.block.crate;

import net.minecraft.util.StringRepresentable;

public enum CrateTier implements StringRepresentable {
    WOODEN("wooden", 1),
    IRON("iron", 2),
    GOLD("gold", 3),
    DIAMOND("diamond", 4),
    EMERALD("emerald", 5),
    OBSIDIAN("obsidian", 6),
    NETHERITE("netherite", 7);

    private final String name;
    private final int tierNumber;
    private final int slots;

    CrateTier(String name, int tierNumber) {
        this.name = name;
        this.tierNumber = tierNumber;
        // Equation: 1 + (tier-1) * 3
        this.slots = 1 + (tierNumber - 1) * 3;
    }

    public int getSlots() {
        return slots;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}