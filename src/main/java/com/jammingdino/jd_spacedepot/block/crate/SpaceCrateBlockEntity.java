package com.jammingdino.jd_spacedepot.block.crate;

import com.jammingdino.jd_spacedepot.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpaceCrateBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items;
    private final CrateTier tier;

    public SpaceCrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPACE_CRATE_BE.get(), pos, state);

        if (state.getBlock() instanceof SpaceCrateBlock crateBlock) {
            this.tier = crateBlock.getTier();
        } else {
            this.tier = CrateTier.WOODEN;
        }

        this.items = NonNullList.withSize(tier.getSlots(), ItemStack.EMPTY);
    }

    // ... existing getters and container methods ...

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.spacedepot." + tier.getSerializedName() + "_space_crate");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new SpaceCrateMenu(containerId, playerInventory, this);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        stack.limitSize(getMaxStackSize(stack));
        setChanged();
    }

    @Override
    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    // --- UPDATED SET CHANGED ---
    @Override
    public void setChanged() {
        super.setChanged();
        // Notify neighbors (like Comparators) that the block state/content has changed
        if (this.level != null) {
            this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
        }
    }
    // ---------------------------

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

    public NonNullList<ItemStack> getItems() {
        return items;
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