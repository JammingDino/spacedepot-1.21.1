package com.jammingdino.jd_spacedepot;

import com.jammingdino.jd_spacedepot.network.PacketHandler;
import com.jammingdino.jd_spacedepot.registry.*;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(SpaceDepot.MODID)
public class SpaceDepot {
    public static final String MODID = "spacedepot";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpaceDepot(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // Registries
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(ModCapabilities::registerCapabilities);
        modEventBus.addListener(PacketHandler::register);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Enqueue work to run safely on the main thread during setup
        event.enqueueWork(() -> {
            registerDispenserBehaviors();
        });
    }

    // Helper method to register the dispenser logic
    private void registerDispenserBehaviors() {
        OptionalDispenseItemBehavior placeCrateBehavior = new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                this.setSuccess(false);
                Item item = stack.getItem();
                if (item instanceof BlockItem blockItem) {
                    Direction direction = source.state().getValue(DispenserBlock.FACING);
                    BlockPos pos = source.pos().relative(direction);

                    // Create a placement context based on the dispenser's facing
                    try {
                        InteractionResult result = blockItem.place(new DirectionalPlaceContext(source.level(), pos, direction, stack, direction));
                        this.setSuccess(result.consumesAction());
                    } catch (Exception e) {
                        LOGGER.error("Failed to dispense space crate", e);
                    }
                }
                return stack;
            }
        };

        // Register for all Crate Items
        DispenserBlock.registerBehavior(ModItems.WOODEN_SPACE_CRATE_ITEM.get(), placeCrateBehavior);
        DispenserBlock.registerBehavior(ModItems.IRON_SPACE_CRATE_ITEM.get(), placeCrateBehavior);
        DispenserBlock.registerBehavior(ModItems.GOLD_SPACE_CRATE_ITEM.get(), placeCrateBehavior);
        DispenserBlock.registerBehavior(ModItems.DIAMOND_SPACE_CRATE_ITEM.get(), placeCrateBehavior);
        DispenserBlock.registerBehavior(ModItems.EMERALD_SPACE_CRATE_ITEM.get(), placeCrateBehavior);
        DispenserBlock.registerBehavior(ModItems.OBSIDIAN_SPACE_CRATE_ITEM.get(), placeCrateBehavior);
        DispenserBlock.registerBehavior(ModItems.NETHERITE_SPACE_CRATE_ITEM.get(), placeCrateBehavior);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.SPACE_LAUNCHER_ITEM);
            event.accept(ModItems.SPACE_RECEIVER_ITEM);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}