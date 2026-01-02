package com.jammingdino.jd_spacedepot.registry;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class ModCapabilities {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // --- ENERGY CAPABILITY ---
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.SPACE_LAUNCHER_BE.get(),
                (blockEntity, direction) -> blockEntity.energy
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.SPACE_AUTO_LAUNCHER_BE.get(),
                (blockEntity, direction) -> blockEntity.energy
        );

        // --- ITEM CAPABILITIES ---

        // 1. Launcher Input
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SPACE_LAUNCHER_BE.get(),
                (blockEntity, direction) -> new InvWrapper(blockEntity)
        );

        // 2. Receiver Output
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SPACE_RECEIVER_BE.get(),
                (blockEntity, direction) -> new InvWrapper(blockEntity)
        );

        // 3. Space Crates (NEW) - Allows pipes to insert/extract from crates
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SPACE_CRATE_BE.get(),
                (blockEntity, direction) -> new InvWrapper(blockEntity)
        );

        // 4. Auto Launcher Input
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SPACE_AUTO_LAUNCHER_BE.get(),
                (blockEntity, direction) -> new InvWrapper(blockEntity)
        );
    }
}