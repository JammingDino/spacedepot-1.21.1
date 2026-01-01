package com.jammingdino.jd_spacedepot.block.menu;

import com.jammingdino.jd_spacedepot.block.entity.SpaceLauncherBlockEntity;
import com.jammingdino.jd_spacedepot.registry.ModBlocks;
import com.jammingdino.jd_spacedepot.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SpaceLauncherMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerLevelAccess access;
    public final BlockPos pos;

    private final DataSlot energyStored = DataSlot.standalone();
    private final DataSlot energyCapacity = DataSlot.standalone();

    public SpaceLauncherMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public SpaceLauncherMenu(int containerId, Inventory playerInv, BlockEntity entity) {
        super(ModMenuTypes.SPACE_LAUNCHER_MENU.get(), containerId);
        this.access = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());
        this.pos = entity.getBlockPos();

        if (entity instanceof SpaceLauncherBlockEntity launcher) {
            this.container = launcher;

            // --- UPDATED SLOT POSITION ---
            // Moved to X=100, Y=20 to sit nicely on the right side
            this.addSlot(new Slot(launcher, 0, 100, 20));

            addDataSlot(energyStored);
            addDataSlot(energyCapacity);
        } else {
            throw new IllegalStateException("Menu opened on invalid block entity");
        }

        // Player Inventory (Standard Position Y=84)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 1) {
                if (!this.moveItemStackTo(itemstack1, 1, 37, true)) return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) return ItemStack.EMPTY;

            if (itemstack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.SPACE_LAUNCHER_BLOCK.get());
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (this.container instanceof SpaceLauncherBlockEntity launcher) {
            this.energyStored.set(launcher.energy.getEnergyStored());
            this.energyCapacity.set(launcher.energy.getMaxEnergyStored());
        }
    }

    public int getEnergy() { return energyStored.get(); }
    public int getMaxEnergy() { return energyCapacity.get(); }
}