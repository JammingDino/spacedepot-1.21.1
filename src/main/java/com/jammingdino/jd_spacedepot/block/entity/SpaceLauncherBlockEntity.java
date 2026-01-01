package com.jammingdino.jd_spacedepot.block.entity;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import com.jammingdino.jd_spacedepot.block.crate.SpaceCrateBlock;
import com.jammingdino.jd_spacedepot.block.menu.SpaceLauncherMenu;
import com.jammingdino.jd_spacedepot.quest.DepotQuest;
import com.jammingdino.jd_spacedepot.registry.ModBlockEntities;
import com.jammingdino.jd_spacedepot.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;

public class SpaceLauncherBlockEntity extends BaseContainerBlockEntity {
    // We use a List of size 1 to satisfy BaseContainerBlockEntity requirements
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    // Custom Energy Storage to handle internal consumption and updates
    public class SpaceLauncherEnergyStorage extends EnergyStorage {
        public SpaceLauncherEnergyStorage(int capacity, int maxReceive) {
            super(capacity, maxReceive, 0); // Max extract is 0 so cables can't drain it
        }

        // Custom method to bypass maxExtract check for internal machine use
        public void consumeEnergy(int amount) {
            this.energy = Math.max(0, this.energy - amount);
            if (amount > 0) {
                SpaceLauncherBlockEntity.this.setChanged();
            }
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                SpaceLauncherBlockEntity.this.setChanged();
            }
            return received;
        }
    }

    // Power System
    public final SpaceLauncherEnergyStorage energy = new SpaceLauncherEnergyStorage(100000, 20000);

    public SpaceLauncherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPACE_LAUNCHER_BE.get(), pos, state);
    }

    public boolean tryLaunch(DepotQuest quest) {
        SpaceDepot.LOGGER.info("--- STARTING LAUNCH ATTEMPT ---");
        ItemStack inputCrate = items.get(0);

        if (inputCrate.isEmpty()) {
            SpaceDepot.LOGGER.info("FAIL: Input slot is empty.");
            return false;
        }
        if (energy.getEnergyStored() < 20000) {
            SpaceDepot.LOGGER.info("FAIL: Insufficient Energy. Has: {}, Needs: 20000", energy.getEnergyStored());
            return false;
        }

        Block block = Block.byItem(inputCrate.getItem());
        if (!(block instanceof SpaceCrateBlock)) {
            SpaceDepot.LOGGER.info("FAIL: Item is not a Space Crate. Found: {}", inputCrate.getItem());
            return false;
        }

        ItemContainerContents contents = inputCrate.get(DataComponents.CONTAINER);
        if (contents == null) {
            SpaceDepot.LOGGER.info("FAIL: Crate has no container data (Empty?).");
            return false;
        }

        // We use an atomic integer or a simple array to count inside the lambda/loop
        final int[] countFound = {0};

        SpaceDepot.LOGGER.info("QUEST REQUIREMENT: {} items matching ingredient", quest.requiredCount());

        // Iterate over every item inside the crate component
        contents.stream().forEach(stack -> {
            if (!stack.isEmpty()) {
                boolean matches = quest.requiredItem().test(stack);

                SpaceDepot.LOGGER.info("SCANNING ITEM: {} x {} | Matches? {}", stack.getCount(), stack.getItem(), matches);

                if (matches) {
                    countFound[0] += stack.getCount();
                }
            }
        });

        int totalFound = countFound[0];

        SpaceDepot.LOGGER.info("TOTAL FOUND: {} / {}", totalFound, quest.requiredCount());

        if (totalFound >= quest.requiredCount()) {
            SpaceDepot.LOGGER.info("SUCCESS: Conditions met. Launching...");

            // UPDATED: Use our custom consume method
            energy.consumeEnergy(20000);

            this.items.set(0, ItemStack.EMPTY);

            sendRewardToReceiver(quest.reward());
            triggerLaunchAnimation();

            this.setChanged();
            return true;
        } else {
            SpaceDepot.LOGGER.info("FAIL: Not enough matching items.");
            return false;
        }
    }

    private void sendRewardToReceiver(ItemStack reward) {
        if (this.level instanceof ServerLevel serverLevel) {
            for (BlockPos p : BlockPos.betweenClosed(worldPosition.offset(-5, -5, -5), worldPosition.offset(5, 5, 5))) {
                if (serverLevel.getBlockState(p).getBlock() == ModBlocks.SPACE_RECEIVER_BLOCK.get()) {
                    var be = serverLevel.getBlockEntity(p);
                    if (be instanceof SpaceReceiverBlockEntity receiver) {
                        receiver.addReward(reward);
                        break;
                    }
                }
            }
        }
    }

    private void triggerLaunchAnimation() {
        // TODO: Sound/Particles
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.spacedepot.launcher");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new SpaceLauncherMenu(containerId, playerInventory, this);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    // --- REQUIRED METHODS FOR BaseContainerBlockEntity ---

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    // ----------------------------------------------------

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
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
        if (tag.contains("Energy")) {
            this.energy.deserializeNBT(registries, tag.get("Energy"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.put("Energy", energy.serializeNBT(registries));
    }
}