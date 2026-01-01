package com.jammingdino.jd_spacedepot.client;

import com.jammingdino.jd_spacedepot.block.menu.SpaceLauncherMenu;
import com.jammingdino.jd_spacedepot.network.LaunchPacket;
import com.jammingdino.jd_spacedepot.quest.DepotQuest;
import com.jammingdino.jd_spacedepot.quest.QuestManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SpaceLauncherScreen extends AbstractContainerScreen<SpaceLauncherMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("spacedepot", "textures/gui/space_launcher.png");
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");

    // Vanilla Button Sprites
    private static final ResourceLocation BUTTON_SPRITE = ResourceLocation.withDefaultNamespace("widget/button");
    private static final ResourceLocation BUTTON_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/button_highlighted");

    private List<DepotQuest> dailyQuests;
    private int scrollOffset = 0;
    private DepotQuest selectedQuest = null;

    public SpaceLauncherScreen(SpaceLauncherMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 10000;
        this.titleLabelY = 5;
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null && this.minecraft.level != null) {
            this.dailyQuests = QuestManager.getDailyQuests(this.minecraft.level.getDayTime() / 24000L);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int listX = x + 7;
        int listY = y + 15;
        int listW = 85;
        int listH = 65;

        int slotX = x + 100;
        int slotY = y + 20;

        int rewardX = x + 130;
        int rewardY = y + 20;

        int btnX = x + 100;
        int btnY = y + 45;
        int btnW = 50;
        int btnH = 20;

        // 1. Draw List Background & Borders
        guiGraphics.fill(listX, listY, listX + listW, listY + listH, 0xFF111111);
        guiGraphics.fill(listX - 1, listY - 1, listX + listW + 1, listY, 0xFF000000);
        guiGraphics.fill(listX - 1, listY + listH, listX + listW + 1, listY + listH + 1, 0xFF000000);
        guiGraphics.fill(listX - 1, listY, listX, listY + listH, 0xFF000000);
        guiGraphics.fill(listX + listW, listY, listX + listW + 1, listY + listH, 0xFF000000);

        // 2. Render Quest List
        guiGraphics.enableScissor(listX, listY, listX + listW, listY + listH);
        for (int i = 0; i < dailyQuests.size(); i++) {
            int relativeIndex = i - scrollOffset;
            int entryY = listY + (relativeIndex * 24);

            if (entryY + 24 < listY || entryY > listY + listH) continue;

            DepotQuest q = dailyQuests.get(i);

            boolean isItemHovered = (mouseX >= listX && mouseX <= listX + listW && mouseY >= entryY && mouseY <= entryY + 24);

            int color = 0xFF2A2A2A;
            if (selectedQuest == q) color = 0xFFDDAA00;
            else if (isItemHovered) color = 0xFF444444;
            else if (q.isSpecial()) color = 0xFF330033;

            guiGraphics.fill(listX, entryY, listX + listW, entryY + 23, color);

            ItemStack icon = q.requiredItem().getItems().length > 0 ? q.requiredItem().getItems()[0] : ItemStack.EMPTY;
            guiGraphics.renderItem(icon, listX + 2, entryY + 4);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.5f, 0.5f, 1.0f);
            guiGraphics.drawString(font, q.getDisplayName(), (listX + 20) * 2, (entryY + 5) * 2, 0xFFFFFF);
            guiGraphics.drawString(font, "Required: " + q.requiredCount(), (listX + 20) * 2, (entryY + 13) * 2, 0xAAAAAA);
            guiGraphics.pose().popPose();
        }
        guiGraphics.disableScissor();

        // 3. Draw Input Slot Sprite
        guiGraphics.blitSprite(SLOT_SPRITE, slotX - 1, slotY - 1, 18, 18);

        // 4. Draw Reward & Button
        if (selectedQuest != null) {
            // Reward Box
            guiGraphics.fill(rewardX - 2, rewardY - 2, rewardX + 18, rewardY + 18, 0xFF555555);
            guiGraphics.fill(rewardX - 1, rewardY - 1, rewardX + 17, rewardY + 17, 0xFF000000);

            guiGraphics.renderItem(selectedQuest.reward(), rewardX, rewardY);
            guiGraphics.renderItemDecorations(font, selectedQuest.reward(), rewardX, rewardY);

            // --- BUTTON RENDERING ---
            boolean isBtnHovered = (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH);

            // Use vanilla sprites for the authentic look
            ResourceLocation sprite = isBtnHovered ? BUTTON_HIGHLIGHTED_SPRITE : BUTTON_SPRITE;
            guiGraphics.blitSprite(sprite, btnX, btnY, btnW, btnH);

            // Draw centered text with shadow
            // Using Red color (0xFF5555) to indicate "Action" similar to a launch button, or White (0xFFFFFF) for standard
            int textColor = isBtnHovered ? 0xFFFFA0A0 : 0xFFFFFFFF;
            guiGraphics.drawCenteredString(font, "LAUNCH", btnX + (btnW / 2), btnY + (btnH - 8) / 2, textColor);

        } else {
            guiGraphics.drawString(font, "Select Quest", slotX, btnY + 6, 0x404040, false);
        }

        // 5. Draw Energy Bar
        int power = menu.getEnergy();
        int max = menu.getMaxEnergy();
        if (max > 0) {
            int barX = x + 160;
            int barY = y + 10;
            int barH = 65;
            int filled = (int) ((float) power / max * barH);

            guiGraphics.fill(barX, barY, barX + 6, barY + barH, 0xFF330000);
            guiGraphics.fill(barX, barY + (barH - filled), barX + 6, barY + barH, 0xFFFF0000);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Energy Tooltip with Commas
        if (mouseX >= x + 160 && mouseX <= x + 166 && mouseY >= y + 10 && mouseY <= y + 75) {
            NumberFormat fmt = NumberFormat.getInstance(Locale.US);
            String tooltip = fmt.format(menu.getEnergy()) + " / " + fmt.format(menu.getMaxEnergy()) + " FE";
            guiGraphics.renderTooltip(font, Component.literal(tooltip), mouseX, mouseY);
        }

        // Reward Tooltip
        if (selectedQuest != null && mouseX >= x + 130 && mouseX <= x + 146 && mouseY >= y + 20 && mouseY <= y + 36) {
            guiGraphics.renderTooltip(font, selectedQuest.reward(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int listX = x + 7;
        int listY = y + 15;
        int listW = 85;
        int listH = 65;

        // Check List Click
        if (mouseX >= listX && mouseX <= listX + listW && mouseY >= listY && mouseY <= listY + listH) {
            double relativeY = mouseY - listY;
            int indexClicked = (int) (relativeY / 24) + scrollOffset;

            if (indexClicked >= 0 && indexClicked < dailyQuests.size()) {
                this.selectedQuest = dailyQuests.get(indexClicked);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        // Check Button Click
        if (selectedQuest != null) {
            int btnX = x + 100;
            int btnY = y + 45;
            int btnW = 50;
            int btnH = 20;

            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                PacketDistributor.sendToServer(new LaunchPacket(menu.pos, selectedQuest.id()));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (mouseX >= x + 7 && mouseX <= x + 92 && mouseY >= y + 15 && mouseY <= y + 80) {
            if (scrollY > 0) scrollOffset = Math.max(0, scrollOffset - 1);
            if (scrollY < 0) scrollOffset = Math.min(Math.max(0, dailyQuests.size() - 2), scrollOffset + 1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}