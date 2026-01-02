package com.jammingdino.jd_spacedepot.client;

import com.jammingdino.jd_spacedepot.block.menu.SpaceLauncherMenu;
import com.jammingdino.jd_spacedepot.network.LaunchPacket;
import com.jammingdino.jd_spacedepot.quest.DepotQuest;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class SpaceLauncherScreen extends AbstractLauncherScreen<SpaceLauncherMenu> {
    public SpaceLauncherScreen(SpaceLauncherMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected int getSelectionColor(DepotQuest q) {
        return 0xFFDDAA00; // Gold for standard selection
    }

    @Override
    protected String getButtonText(DepotQuest q) {
        return "LAUNCH";
    }

    @Override
    protected void onButtonClick() {
        if (selectedQuest != null) {
            PacketDistributor.sendToServer(new LaunchPacket(menu.pos, selectedQuest.id()));
        }
    }
}