package com.jammingdino.jd_spacedepot.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record QuestRequirement(Ingredient ingredient, int count) {
    public static final Codec<QuestRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(QuestRequirement::ingredient),
            Codec.INT.fieldOf("count").forGetter(QuestRequirement::count)
    ).apply(instance, QuestRequirement::new));

    public ItemStack getIcon() {
        ItemStack[] items = ingredient.getItems();
        return items.length > 0 ? items[0] : ItemStack.EMPTY;
    }

    public Component getTooltip() {
        ItemStack icon = getIcon();
        return Component.translatable(icon.getDescriptionId()).append(" x" + count);
    }
}