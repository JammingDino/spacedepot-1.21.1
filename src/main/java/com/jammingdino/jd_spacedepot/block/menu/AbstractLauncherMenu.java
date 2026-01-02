package com.jammingdino.jd_spacedepot.block.menu;

import com.jammingdino.jd_spacedepot.block.entity.AbstractLauncherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractLauncherMenu extends AbstractContainerMenu {
    public final BlockPos pos;
    public final AbstractLauncherBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final DataSlot energyStored = DataSlot.standalone();
    private final DataSlot energyCapacity = DataSlot.standalone();

    protected AbstractLauncherMenu(MenuType<?> type, int containerId, Inventory playerInv, BlockEntity entity) {
        super(type, containerId);
        this.access = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());
        this.pos = entity.getBlockPos();

        if (entity instanceof AbstractLauncherBlockEntity launcher) {
            this.blockEntity = launcher;
            // Common Crate Slot
            this.addSlot(new Slot(launcher, 0, 100, 20));
            addDataSlot(energyStored);
            addDataSlot(energyCapacity);
        } else {
            throw new IllegalStateException("Menu opened on invalid block entity");
        }

        // Player Inventory
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
            if (itemstack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        }
        return itemstack;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        this.energyStored.set(blockEntity.energy.getEnergyStored());
        this.energyCapacity.set(blockEntity.energy.getMaxEnergyStored());
    }

    public int getEnergy() { return energyStored.get(); }
    public int getMaxEnergy() { return energyCapacity.get(); }

    protected abstract Block getValidBlock();

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, getValidBlock());
    }
}