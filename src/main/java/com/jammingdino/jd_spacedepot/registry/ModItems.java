package com.jammingdino.jd_spacedepot.registry;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import com.jammingdino.jd_spacedepot.item.FuelBlockItem;
import com.jammingdino.jd_spacedepot.item.FuelItem;
import com.jammingdino.jd_spacedepot.item.SpaceCrateItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpaceDepot.MODID);

    // Examples
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", ModBlocks.EXAMPLE_BLOCK);
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // Crates
    public static final DeferredItem<BlockItem> WOODEN_SPACE_CRATE_ITEM = ITEMS.register("wooden_space_crate",
            () -> new SpaceCrateItem(ModBlocks.WOODEN_SPACE_CRATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> IRON_SPACE_CRATE_ITEM = ITEMS.register("iron_space_crate",
            () -> new SpaceCrateItem(ModBlocks.IRON_SPACE_CRATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> GOLD_SPACE_CRATE_ITEM = ITEMS.register("gold_space_crate",
            () -> new SpaceCrateItem(ModBlocks.GOLD_SPACE_CRATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> DIAMOND_SPACE_CRATE_ITEM = ITEMS.register("diamond_space_crate",
            () -> new SpaceCrateItem(ModBlocks.DIAMOND_SPACE_CRATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> EMERALD_SPACE_CRATE_ITEM = ITEMS.register("emerald_space_crate",
            () -> new SpaceCrateItem(ModBlocks.EMERALD_SPACE_CRATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> OBSIDIAN_SPACE_CRATE_ITEM = ITEMS.register("obsidian_space_crate",
            () -> new SpaceCrateItem(ModBlocks.OBSIDIAN_SPACE_CRATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> NETHERITE_SPACE_CRATE_ITEM = ITEMS.register("netherite_space_crate",
            () -> new SpaceCrateItem(ModBlocks.NETHERITE_SPACE_CRATE.get(), new Item.Properties()));

    // Solar
    public static final DeferredItem<Item> SOLAR_COIN = ITEMS.register("solar_coin",
            () -> new FuelItem(new Item.Properties(), 300));

    public static final DeferredItem<Item> SOLAR_CHUNK = ITEMS.register("solar_chunk",
            () -> new FuelItem(new Item.Properties(), 2700));

    public static final DeferredItem<BlockItem> SOLAR_BLOCK_ITEM = ITEMS.register("solar_block",
            () -> new FuelBlockItem(ModBlocks.SOLAR_BLOCK.get(), new Item.Properties(), 24300));

    // Space Launcher
    public static final DeferredItem<BlockItem> SPACE_LAUNCHER_ITEM = ITEMS.register("space_launcher",
            () -> new BlockItem(ModBlocks.SPACE_LAUNCHER_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> SPACE_RECEIVER_ITEM = ITEMS.register("space_receiver",
            () -> new BlockItem(ModBlocks.SPACE_RECEIVER_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> SPACE_AUTO_LAUNCHER_ITEM = ITEMS.register("space_auto_launcher",
            () -> new BlockItem(ModBlocks.SPACE_AUTO_LAUNCHER_BLOCK.get(), new Item.Properties()));
}