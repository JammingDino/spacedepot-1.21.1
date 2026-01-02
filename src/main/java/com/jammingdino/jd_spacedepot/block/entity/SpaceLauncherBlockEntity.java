package com.jammingdino.jd_spacedepot.block.entity;

import com.jammingdino.jd_spacedepot.block.menu.SpaceLauncherMenu;
import com.jammingdino.jd_spacedepot.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class SpaceLauncherBlockEntity extends AbstractLauncherBlockEntity {
    public SpaceLauncherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPACE_LAUNCHER_BE.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.spacedepot.launcher");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new SpaceLauncherMenu(containerId, playerInventory, this);
    }
}