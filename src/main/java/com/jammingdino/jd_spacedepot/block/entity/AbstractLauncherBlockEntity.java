package com.jammingdino.jd_spacedepot.block.entity;

import com.jammingdino.jd_spacedepot.block.crate.SpaceCrateBlock;
import com.jammingdino.jd_spacedepot.quest.DepotQuest;
import com.jammingdino.jd_spacedepot.quest.QuestRequirement;
import com.jammingdino.jd_spacedepot.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket; // Import Added
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.energy.EnergyStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractLauncherBlockEntity extends BaseContainerBlockEntity {
    protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    protected final List<PendingReward> pendingRewards = new ArrayList<>();

    public class LauncherEnergyStorage extends EnergyStorage {
        public LauncherEnergyStorage(int capacity, int maxReceive) { super(capacity, maxReceive, 0); }
        public void consumeEnergy(int amount) {
            this.energy = Math.max(0, this.energy - amount);
            if(amount > 0) setChanged();
        }
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int r = super.receiveEnergy(maxReceive, simulate);
            if(r > 0 && !simulate) setChanged();
            return r;
        }
    }
    public final LauncherEnergyStorage energy = new LauncherEnergyStorage(100000, 20000);

    public AbstractLauncherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractLauncherBlockEntity entity) {
        if (!entity.pendingRewards.isEmpty()) {
            boolean changed = false;
            Iterator<PendingReward> iterator = entity.pendingRewards.iterator();
            while (iterator.hasNext()) {
                PendingReward reward = iterator.next();
                reward.ticksLeft--;
                if (reward.ticksLeft <= 0) {
                    entity.sendRewardToReceiver(reward.stack);
                    iterator.remove();
                    changed = true;
                }
            }
            if (changed) entity.setChanged();
        }
    }

    public boolean tryLaunch(DepotQuest quest) {
        ItemStack inputCrate = items.get(0);
        if (inputCrate.isEmpty()) return false;
        if (energy.getEnergyStored() < 20000) return false;

        // Sky Check
        int highestY = this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, this.worldPosition.getX(), this.worldPosition.getZ());
        if (highestY > this.worldPosition.getY() + 1) {
            this.level.playSound(null, this.worldPosition, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 1.0f);
            return false;
        }

        Block block = Block.byItem(inputCrate.getItem());
        if (!(block instanceof SpaceCrateBlock)) return false;

        ItemContainerContents contents = inputCrate.get(DataComponents.CONTAINER);
        if (contents == null) return false;

        // Verify Requirements
        List<ItemStack> simulatedContents = new ArrayList<>();
        contents.stream().forEach(s -> simulatedContents.add(s.copy()));

        for (QuestRequirement req : quest.requirements()) {
            int needed = req.count();
            int found = 0;
            for (ItemStack stack : simulatedContents) {
                if (stack.isEmpty()) continue;
                if (req.ingredient().test(stack)) {
                    int take = Math.min(stack.getCount(), needed - found);
                    stack.shrink(take);
                    found += take;
                    if (found >= needed) break;
                }
            }
            if (found < needed) return false;
        }

        // Success
        energy.consumeEnergy(20000);
        triggerLaunchAnimation(inputCrate.copy());
        this.items.set(0, ItemStack.EMPTY);
        this.pendingRewards.add(new PendingReward(quest.reward().copy(), 100)); // 5 seconds delay
        this.setChanged();
        return true;
    }

    private void sendRewardToReceiver(ItemStack reward) {
        if (this.level instanceof ServerLevel serverLevel) {
            for (BlockPos p : BlockPos.betweenClosed(worldPosition.offset(-5, -5, -5), worldPosition.offset(5, 5, 5))) {
                if (serverLevel.getBlockState(p).getBlock() == ModBlocks.SPACE_RECEIVER_BLOCK.get()) {
                    var be = serverLevel.getBlockEntity(p);
                    if (be instanceof SpaceReceiverBlockEntity receiver) {
                        receiver.addReward(reward);
                        serverLevel.playSound(null, p, SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
                        break;
                    }
                }
            }
        }
    }

    private void triggerLaunchAnimation(ItemStack crateStack) {
        if (this.level instanceof ServerLevel serverLevel) {
            BlockPos pos = this.worldPosition;
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.2; // Start slightly higher to clear machine
            double z = pos.getZ() + 0.5;

            // 1. Sounds
            serverLevel.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
            serverLevel.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.BLOCKS, 5.0f, 0.7f);

            // 2. Particles
            for(int i = 0; i < 10; i++) {
                double vx = (level.random.nextDouble() - 0.5) * 0.2;
                double vy = 0.2 + level.random.nextDouble() * 0.4;
                double vz = (level.random.nextDouble() - 0.5) * 0.2;
                serverLevel.sendParticles(ParticleTypes.FLAME, x, y, z, 0, vx, vy, vz, 1.0);
            }

            for(int i = 0; i < 20; i++) {
                double vx = (level.random.nextDouble() - 0.5) * 0.5;
                double vy = 0.5 + level.random.nextDouble() * 1.0;
                double vz = (level.random.nextDouble() - 0.5) * 0.5;
                serverLevel.sendParticles(ParticleTypes.CLOUD, x, y, z, 0, vx, vy, vz, 1.0);
            }

            serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 3, 0.2, 0.2, 0.2, 0.0);

            // 3. Visual Crate Entity
            Block crateBlock = Block.byItem(crateStack.getItem());

            if (crateBlock instanceof SpaceCrateBlock) {
                // Use built-in fall method to create the entity properly
                FallingBlockEntity flyingBlock = FallingBlockEntity.fall(serverLevel, BlockPos.containing(x, y, z), crateBlock.defaultBlockState());

                // Configure it
                flyingBlock.setPos(x, y, z);
                flyingBlock.setDeltaMovement(0, 1.5, 0); // High upward velocity
                flyingBlock.setNoGravity(true);          // Fly straight
                flyingBlock.dropItem = false;            // No drops
                // Note: cancelDrop is private, but dropItem=false handles the main duplication issue.

                // IMPORTANT: Manually broadcast the velocity packet because FallingBlockEntity.fall()
                // spawns it with 0 velocity initially, and the client might miss the update.
                serverLevel.getChunkSource().broadcast(flyingBlock, new ClientboundSetEntityMotionPacket(flyingBlock));

            } else {
                // Fallback for non-blocks
                ItemEntity flyingCrate = new ItemEntity(serverLevel, x, y, z, crateStack);
                flyingCrate.setDeltaMovement(0, 1.5, 0);
                flyingCrate.setNoGravity(true);
                flyingCrate.setPickUpDelay(32767);
                flyingCrate.setUnlimitedLifetime();
                flyingCrate.setCustomName(Component.literal("Launched Shipment"));
                flyingCrate.setCustomNameVisible(false);
                serverLevel.addFreshEntity(flyingCrate);
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        if (tag.contains("Energy")) this.energy.deserializeNBT(registries, tag.get("Energy"));
        pendingRewards.clear();
        if (tag.contains("PendingRewards", Tag.TAG_LIST)) {
            ListTag list = tag.getList("PendingRewards", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag t = list.getCompound(i);
                pendingRewards.add(new PendingReward(ItemStack.parseOptional(registries, t.getCompound("Item")), t.getInt("Ticks")));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.put("Energy", energy.serializeNBT(registries));
        if (!pendingRewards.isEmpty()) {
            ListTag list = new ListTag();
            for (PendingReward pr : pendingRewards) {
                CompoundTag t = new CompoundTag();
                t.putInt("Ticks", pr.ticksLeft);
                t.put("Item", pr.stack.save(registries));
                list.add(t);
            }
            tag.put("PendingRewards", list);
        }
    }

    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return items.get(0).isEmpty(); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { return ContainerHelper.removeItem(items, slot, amount); }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); setChanged(); }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }

    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }

    @Override public void clearContent() { items.clear(); }

    protected static class PendingReward {
        ItemStack stack; int ticksLeft;
        public PendingReward(ItemStack s, int t) { stack = s; ticksLeft = t; }
    }
}