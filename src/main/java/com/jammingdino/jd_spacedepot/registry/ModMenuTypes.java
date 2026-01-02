package com.jammingdino.jd_spacedepot.registry;

import com.jammingdino.jd_spacedepot.SpaceDepot;
import com.jammingdino.jd_spacedepot.block.crate.SpaceCrateMenu;
import com.jammingdino.jd_spacedepot.block.menu.SpaceAutoLauncherMenu;
import com.jammingdino.jd_spacedepot.block.menu.SpaceLauncherMenu;
import com.jammingdino.jd_spacedepot.block.menu.SpaceReceiverMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, SpaceDepot.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<SpaceCrateMenu>> SPACE_CRATE_MENU =
            MENU_TYPES.register("space_crate", () -> IMenuTypeExtension.create(SpaceCrateMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SpaceLauncherMenu>> SPACE_LAUNCHER_MENU =
            MENU_TYPES.register("space_launcher", () -> IMenuTypeExtension.create(SpaceLauncherMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SpaceReceiverMenu>> SPACE_RECEIVER_MENU =
            MENU_TYPES.register("space_receiver", () -> IMenuTypeExtension.create(SpaceReceiverMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SpaceAutoLauncherMenu>> SPACE_AUTO_LAUNCHER_MENU =
            MENU_TYPES.register("space_auto_launcher", () -> IMenuTypeExtension.create(SpaceAutoLauncherMenu::new));
}