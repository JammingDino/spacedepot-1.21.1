package com.jammingdino.jd_spacedepot.registry;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpaceDepot.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spacedepot"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.WOODEN_SPACE_CRATE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                // Machines
                output.accept(ModItems.SPACE_LAUNCHER_ITEM.get());
                output.accept(ModItems.SPACE_RECEIVER_ITEM.get());
                output.accept(ModItems.SPACE_AUTO_LAUNCHER_ITEM.get());

                // Currency
                output.accept(ModItems.SOLAR_COIN.get());
                output.accept(ModItems.SOLAR_CHUNK.get());
                output.accept(ModItems.SOLAR_BLOCK_ITEM.get());

                // Crates
                output.accept(ModItems.WOODEN_SPACE_CRATE_ITEM.get());
                output.accept(ModItems.IRON_SPACE_CRATE_ITEM.get());
                output.accept(ModItems.GOLD_SPACE_CRATE_ITEM.get());
                output.accept(ModItems.DIAMOND_SPACE_CRATE_ITEM.get());
                output.accept(ModItems.EMERALD_SPACE_CRATE_ITEM.get());
                output.accept(ModItems.OBSIDIAN_SPACE_CRATE_ITEM.get());
                output.accept(ModItems.NETHERITE_SPACE_CRATE_ITEM.get());
            }).build());
}