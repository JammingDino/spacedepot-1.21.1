package com.jammingdino.jd_spacedepot.network;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

// Annotation removed to fix warning. Registered manually in SpaceDepot.java
public class PacketHandler {
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                LaunchPacket.TYPE,
                LaunchPacket.STREAM_CODEC,
                LaunchPacket::handle
        );
    }
}