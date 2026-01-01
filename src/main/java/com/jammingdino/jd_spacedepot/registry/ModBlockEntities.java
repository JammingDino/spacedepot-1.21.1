package com.jammingdino.jd_spacedepot.registry;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import com.jammingdino.jd_spacedepot.block.crate.SpaceCrateBlockEntity;
import com.jammingdino.jd_spacedepot.block.entity.SpaceLauncherBlockEntity;
import com.jammingdino.jd_spacedepot.block.entity.SpaceReceiverBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpaceDepot.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpaceCrateBlockEntity>> SPACE_CRATE_BE =
            BLOCK_ENTITIES.register("space_crate", () -> BlockEntityType.Builder.of(SpaceCrateBlockEntity::new,
                    ModBlocks.WOODEN_SPACE_CRATE.get(),
                    ModBlocks.IRON_SPACE_CRATE.get(),
                    ModBlocks.GOLD_SPACE_CRATE.get(),
                    ModBlocks.DIAMOND_SPACE_CRATE.get(),
                    ModBlocks.EMERALD_SPACE_CRATE.get(),
                    ModBlocks.OBSIDIAN_SPACE_CRATE.get(),
                    ModBlocks.NETHERITE_SPACE_CRATE.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpaceLauncherBlockEntity>> SPACE_LAUNCHER_BE =
            BLOCK_ENTITIES.register("space_launcher", () -> BlockEntityType.Builder.of(SpaceLauncherBlockEntity::new,
                    ModBlocks.SPACE_LAUNCHER_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpaceReceiverBlockEntity>> SPACE_RECEIVER_BE =
            BLOCK_ENTITIES.register("space_receiver", () -> BlockEntityType.Builder.of(SpaceReceiverBlockEntity::new,
                    ModBlocks.SPACE_RECEIVER_BLOCK.get()).build(null));
}