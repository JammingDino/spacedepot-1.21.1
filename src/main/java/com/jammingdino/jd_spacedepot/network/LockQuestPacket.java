package com.jammingdino.jd_spacedepot.network;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import com.jammingdino.jd_spacedepot.block.entity.SpaceAutoLauncherBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record LockQuestPacket(BlockPos pos, UUID questId, boolean isLocking) implements CustomPacketPayload {
    public static final Type<LockQuestPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SpaceDepot.MODID, "lock_quest"));

    public static final StreamCodec<ByteBuf, LockQuestPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, LockQuestPacket::pos,
            net.minecraft.core.UUIDUtil.STREAM_CODEC, LockQuestPacket::questId,
            ByteBufCodecs.BOOL, LockQuestPacket::isLocking,
            LockQuestPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final LockQuestPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (player.level().getBlockEntity(payload.pos) instanceof SpaceAutoLauncherBlockEntity launcher) {
                    if (payload.isLocking) {
                        launcher.setLockedQuestId(payload.questId);
                    } else {
                        // "Unlock" means set to null
                        launcher.setLockedQuestId(null);
                    }
                }
            }
        });
    }
}