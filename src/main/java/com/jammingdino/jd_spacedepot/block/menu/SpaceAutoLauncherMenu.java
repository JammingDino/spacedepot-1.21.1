package com.jammingdino.jd_spacedepot.block.menu;

import com.jammingdino.jd_spacedepot.registry.ModBlocks;
import com.jammingdino.jd_spacedepot.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SpaceAutoLauncherMenu extends AbstractLauncherMenu {
    public SpaceAutoLauncherMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    public SpaceAutoLauncherMenu(int containerId, Inventory playerInv, BlockEntity entity) {
        super(ModMenuTypes.SPACE_AUTO_LAUNCHER_MENU.get(), containerId, playerInv, entity);
    }
    @Override protected Block getValidBlock() { return ModBlocks.SPACE_AUTO_LAUNCHER_BLOCK.get(); }
}