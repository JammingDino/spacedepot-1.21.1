package com.jammingdino.jd_spacedepot.block.entity;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import com.jammingdino.jd_spacedepot.block.menu.SpaceAutoLauncherMenu;
import com.jammingdino.jd_spacedepot.quest.DepotQuest;
import com.jammingdino.jd_spacedepot.quest.QuestManager;
import com.jammingdino.jd_spacedepot.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class SpaceAutoLauncherBlockEntity extends AbstractLauncherBlockEntity {
    private UUID lockedQuestId = null;

    public SpaceAutoLauncherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPACE_AUTO_LAUNCHER_BE.get(), pos, state);
    }

    public void setLockedQuestId(UUID id) {
        this.lockedQuestId = id;
        this.setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public UUID getLockedQuestId() {
        return lockedQuestId;
    }

    public void onRedstoneSignal() {
        if (this.level == null || this.level.isClientSide) return;
        if (lockedQuestId == null) return;

        long day = level.getDayTime() / 24000L;
        Optional<DepotQuest> questOpt = QuestManager.get().findQuest(day, lockedQuestId);
        if (questOpt.isPresent()) {
            tryLaunch(questOpt.get());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("LockedQuest")) this.lockedQuestId = tag.getUUID("LockedQuest");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.lockedQuestId != null) tag.putUUID("LockedQuest", this.lockedQuestId);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if(lockedQuestId != null) tag.putUUID("LockedQuest", lockedQuestId);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.spacedepot.auto_launcher");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new SpaceAutoLauncherMenu(id, inv, this);
    }
}