package com.jammingdino.jd_spacedepot.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class SpaceCrateItem extends BlockItem {
    public SpaceCrateItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        // If the crate has items inside, it shouldn't stack
        if (stack.has(DataComponents.CONTAINER)) {
            return 1;
        }
        // Otherwise, use default max stack size (usually 64)
        return super.getMaxStackSize(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        // Retrieve container contents
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);

        if (contents != null) {
            int displayedCount = 0;
            int totalCount = 0;

            // contents.nonEmptyStream() gives us all slots that actually have items
            for (ItemStack s : contents.nonEmptyStream().toList()) {
                totalCount++;

                // Show up to 5 items
                if (displayedCount < 5) {
                    MutableComponent line = s.getHoverName().copy();
                    line.append(" x").append(String.valueOf(s.getCount()));
                    tooltipComponents.add(line.withStyle(ChatFormatting.GRAY));
                    displayedCount++;
                }
            }

            // If there are more items than we displayed, show "... and X more"
            if (totalCount > displayedCount) {
                tooltipComponents.add(Component.translatable("container.shulkerBox.more", totalCount - displayedCount)
                        .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            }
        }
    }
}