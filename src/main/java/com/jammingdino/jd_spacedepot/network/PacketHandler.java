package com.jammingdino.jd_spacedepot.network;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
    public static void register(RegisterPayloadHandlersEvent event) {
        // The version string "1" must match on client and server.
        final PayloadRegistrar registrar = event.registrar("1");

        // 1. Launch Quest Packet
        registrar.playToServer(
                LaunchPacket.TYPE,
                LaunchPacket.STREAM_CODEC,
                LaunchPacket::handle
        );

        // 2. Lock Quest Packet (This was likely missing!)
        registrar.playToServer(
                LockQuestPacket.TYPE,
                LockQuestPacket.STREAM_CODEC,
                LockQuestPacket::handle
        );
    }
}