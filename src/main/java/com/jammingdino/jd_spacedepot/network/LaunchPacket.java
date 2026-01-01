package com.jammingdino.jd_spacedepot.network;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import com.jammingdino.jd_spacedepot.block.entity.SpaceLauncherBlockEntity;
import com.jammingdino.jd_spacedepot.quest.DepotQuest;
import com.jammingdino.jd_spacedepot.quest.QuestManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public record LaunchPacket(BlockPos pos, UUID questId) implements CustomPacketPayload {
    public static final Type<LaunchPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SpaceDepot.MODID, "launch_quest"));

    public static final StreamCodec<ByteBuf, LaunchPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, LaunchPacket::pos,
            net.minecraft.core.UUIDUtil.STREAM_CODEC, LaunchPacket::questId,
            LaunchPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final LaunchPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                SpaceDepot.LOGGER.info("PACKET: Received Launch Request for Quest ID: {}", payload.questId);

                if (player.level().getBlockEntity(payload.pos) instanceof SpaceLauncherBlockEntity launcher) {
                    long day = player.level().getDayTime() / 24000L;
                    SpaceDepot.LOGGER.info("PACKET: Server Day: {}", day);

                    List<DepotQuest> quests = QuestManager.getDailyQuests(day);

                    for (DepotQuest q : quests) {
                        if (q.id().equals(payload.questId)) {
                            SpaceDepot.LOGGER.info("PACKET: Found matching quest: {}", q.title());
                            launcher.tryLaunch(q);
                            return;
                        }
                    }
                    SpaceDepot.LOGGER.info("PACKET FAIL: Quest ID not found in server list for today.");
                } else {
                    SpaceDepot.LOGGER.info("PACKET FAIL: No Launcher BlockEntity found at {}", payload.pos);
                }
            }
        });
    }
}