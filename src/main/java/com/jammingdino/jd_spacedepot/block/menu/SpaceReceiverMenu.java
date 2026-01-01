package com.jammingdino.jd_spacedepot.block.menu;

import com.jammingdino.jd_spacedepot.registry.ModBlocks;
import com.jammingdino.jd_spacedepot.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SpaceReceiverMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerLevelAccess access;

    // Client Constructor
    public SpaceReceiverMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, new SimpleContainer(9));
    }

    // Server Constructor
    public SpaceReceiverMenu(int containerId, Inventory playerInv, Container container) {
        super(ModMenuTypes.SPACE_RECEIVER_MENU.get(), containerId);
        this.container = container;
        // Access is needed for "stillValid" checks. We assume the player is interacting with the block if container is a BE.
        this.access = ContainerLevelAccess.NULL;

        checkContainerSize(container, 9);
        container.startOpen(playerInv.player);

        // --- 3x3 Receiver Grid ---
        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(container, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }

        // --- Player Inventory ---
        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // --- Hotbar ---
        for(int col = 0; col < 9; ++col) {
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

            if (index < 9) { // Receiver Slots
                if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) { // Player Slots
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