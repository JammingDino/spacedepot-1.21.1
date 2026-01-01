package com.jammingdino.jd_spacedepot.block.entity;

import com.jammingdino.jd_spacedepot.block.menu.SpaceReceiverMenu;
import com.jammingdino.jd_spacedepot.registry.ModBlockEntities;
import com.jammingdino.jd_spacedepot.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SpaceReceiverBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    public SpaceReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPACE_RECEIVER_BE.get(), pos, state);
    }

    public void addReward(ItemStack rewardItem) {
        ItemStack rewardCrate = new ItemStack(ModItems.WOODEN_SPACE_CRATE_ITEM.get());
        ItemContainerContents contents = ItemContainerContents.fromItems(List.of(rewardItem));
        rewardCrate.set(DataComponents.CONTAINER, contents);

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, rewardCrate);
                setChanged();
                return;
            }
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.spacedepot.receiver");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        // Return the new menu!
        return new SpaceReceiverMenu(containerId, playerInventory, this);
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) if (!stack.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack s = ContainerHelper.removeItem(items, slot, amount);
        if (!s.isEmpty()) setChanged();
        return s;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    // --- REQUIRED METHODS ---
    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
        setChanged();
    }
    // ------------------------

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }
}