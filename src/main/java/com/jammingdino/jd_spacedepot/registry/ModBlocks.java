package com.jammingdino.jd_spacedepot.registry;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import com.jammingdino.jd_spacedepot.block.SpaceAutoLauncherBlock;
import com.jammingdino.jd_spacedepot.block.SpaceLauncherBlock;
import com.jammingdino.jd_spacedepot.block.SpaceReceiverBlock;
import com.jammingdino.jd_spacedepot.block.crate.CrateTier;
import com.jammingdino.jd_spacedepot.block.crate.SpaceCrateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpaceDepot.MODID);

    // Example
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));

    // Crates
    public static final DeferredBlock<Block> WOODEN_SPACE_CRATE = BLOCKS.register("wooden_space_crate",
            () -> new SpaceCrateBlock(CrateTier.WOODEN, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0f)));

    public static final DeferredBlock<Block> IRON_SPACE_CRATE = BLOCKS.register("iron_space_crate",
            () -> new SpaceCrateBlock(CrateTier.IRON, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0f)));

    public static final DeferredBlock<Block> GOLD_SPACE_CRATE = BLOCKS.register("gold_space_crate",
            () -> new SpaceCrateBlock(CrateTier.GOLD, BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(4.0f)));

    public static final DeferredBlock<Block> DIAMOND_SPACE_CRATE = BLOCKS.register("diamond_space_crate",
            () -> new SpaceCrateBlock(CrateTier.DIAMOND, BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).strength(4.0f)));

    public static final DeferredBlock<Block> EMERALD_SPACE_CRATE = BLOCKS.register("emerald_space_crate",
            () -> new SpaceCrateBlock(CrateTier.EMERALD, BlockBehaviour.Properties.of().mapColor(MapColor.EMERALD).strength(4.0f)));

    public static final DeferredBlock<Block> OBSIDIAN_SPACE_CRATE = BLOCKS.register("obsidian_space_crate",
            () -> new SpaceCrateBlock(CrateTier.OBSIDIAN, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(20.0f)));

    public static final DeferredBlock<Block> NETHERITE_SPACE_CRATE = BLOCKS.register("netherite_space_crate",
            () -> new SpaceCrateBlock(CrateTier.NETHERITE, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(20.0f)));

    // Solar
    public static final DeferredBlock<Block> SOLAR_BLOCK = BLOCKS.register("solar_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .strength(3.0f, 6.0f)
                    .lightLevel(state -> 15)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> SPACE_LAUNCHER_BLOCK = BLOCKS.register("space_launcher",
            () -> new SpaceLauncherBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0f).noOcclusion()));

    public static final DeferredBlock<Block> SPACE_RECEIVER_BLOCK = BLOCKS.register("space_receiver",
            () -> new SpaceReceiverBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0f).noOcclusion()));

    public static final DeferredBlock<Block> SPACE_AUTO_LAUNCHER_BLOCK = BLOCKS.register("space_auto_launcher",
            () -> new SpaceAutoLauncherBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0f).noOcclusion()));
}