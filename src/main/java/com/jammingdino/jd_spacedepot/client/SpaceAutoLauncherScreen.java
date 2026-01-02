package com.jammingdino.jd_spacedepot.client;

import com.jammingdino.jd_spacedepot.block.entity.SpaceAutoLauncherBlockEntity;
import com.jammingdino.jd_spacedepot.block.menu.SpaceAutoLauncherMenu;
import com.jammingdino.jd_spacedepot.network.LockQuestPacket;
import com.jammingdino.jd_spacedepot.quest.DepotQuest;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public class SpaceAutoLauncherScreen extends AbstractLauncherScreen<SpaceAutoLauncherMenu> {
    public SpaceAutoLauncherScreen(SpaceAutoLauncherMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    // Helper to get the specific BlockEntity safely
    private SpaceAutoLauncherBlockEntity getAutoLauncher() {
        return (SpaceAutoLauncherBlockEntity) menu.blockEntity;
    }

    @Override
    protected void init() {
        super.init();
        // Auto-select locked quest if present
        UUID lockedId = getAutoLauncher().getLockedQuestId();
        if (lockedId != null) {
            for(DepotQuest q : dailyQuests) {
                if (q.id().equals(lockedId)) {
                    this.selectedQuest = q;
                    break;
                }
            }
        }
    }

    @Override
    protected int getSelectionColor(DepotQuest q) {
        UUID lockedId = getAutoLauncher().getLockedQuestId();

        // Green if this is the locked quest
        if (lockedId != null && q.id().equals(lockedId)) {
            return 0xFF00AA00;
        }
        // Gold otherwise (standard selection)
        return 0xFFDDAA00;
    }

    @Override
    protected String getButtonText(DepotQuest q) {
        UUID lockedId = getAutoLauncher().getLockedQuestId();
        // If this specific quest is locked, button says UNLOCK
        // If anything else is locked (or nothing), button says LOCK
        return (lockedId != null && q.id().equals(lockedId)) ? "UNLOCK" : "LOCK";
    }

    @Override
    protected void onButtonClick() {
        if (selectedQuest != null) {
            UUID currentLocked = getAutoLauncher().getLockedQuestId();

            // If the selected quest is already locked, we want to unlock it (isLocking = false)
            // Otherwise, we want to lock the selected quest (isLocking = true)
            boolean isCurrentlyLocked = currentLocked != null && selectedQuest.id().equals(currentLocked);

            PacketDistributor.sendToServer(new LockQuestPacket(menu.pos, selectedQuest.id(), !isCurrentlyLocked));
        }
    }
}