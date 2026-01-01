package com.jammingdino.jd_spacedepot.block.crate;

import com.jammingdino.jd_spacedepot.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SpaceCrateMenu extends AbstractContainerMenu {
    private final Container container;
    private final int slotCount;

    public SpaceCrateMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, getContainerAtPos(playerInventory, extraData));
    }

    private static Container getContainerAtPos(Inventory playerInventory, FriendlyByteBuf extraData) {
        if (extraData == null) return new SimpleContainer(1);
        BlockPos pos = extraData.readBlockPos();
        BlockEntity be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof SpaceCrateBlockEntity crate) {
            return new SimpleContainer(crate.getContainerSize());
        }
        return new SimpleContainer(1);
    }

    public SpaceCrateMenu(int containerId, Inventory playerInventory, Container container) {
        super(ModMenuTypes.SPACE_CRATE_MENU.get(), containerId);
        this.container = container;
        this.slotCount = container.getContainerSize();

        checkContainerSize(container, slotCount);
        container.startOpen(playerInventory.player);

        int yStart = 35;
        if (slotCount > 18) {
            yStart = 18;
        }

        for (int i = 0; i < slotCount; i++) {
            int row = i / 9;
            int col = i % 9;

            int itemsInCurrentRow = 9;
            if ((row + 1) * 9 > slotCount) {
                itemsInCurrentRow = slotCount - (row * 9);
            }

            int rowStartX = 89 - (itemsInCurrentRow * 18) / 2;

            this.addSlot(new Slot(container, i, rowStartX + (col * 18), yStart + (row * 18)));
        }

        int inventoryY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, inventoryY + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, inventoryY + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < this.slotCount) {
                if (!this.moveItemStackTo(itemstack1, this.slotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.slotCount, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}