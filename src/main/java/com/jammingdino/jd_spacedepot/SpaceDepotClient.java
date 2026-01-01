package com.jammingdino.jd_spacedepot;

import com.jammingdino.jd_spacedepot.client.SpaceLauncherScreen;
import com.jammingdino.jd_spacedepot.client.SpaceReceiverScreen;
import com.jammingdino.jd_spacedepot.registry.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import com.jammingdino.jd_spacedepot.block.crate.SpaceCrateScreen;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = SpaceDepot.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SpaceDepot.MODID, value = Dist.CLIENT)
public class SpaceDepotClient {
    public SpaceDepotClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // ...
    }

    // ADD THIS METHOD
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.SPACE_CRATE_MENU.get(), SpaceCrateScreen::new);
        event.register(ModMenuTypes.SPACE_LAUNCHER_MENU.get(), SpaceLauncherScreen::new);
        event.register(ModMenuTypes.SPACE_RECEIVER_MENU.get(), SpaceReceiverScreen::new);
    }
}
