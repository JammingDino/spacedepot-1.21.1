package com.jammingdino.jd_spacedepot.client;

import com.jammingdino.jd_spacedepot.block.menu.AbstractLauncherMenu;
import com.jammingdino.jd_spacedepot.quest.DepotQuest;
import com.jammingdino.jd_spacedepot.quest.QuestManager;
import com.jammingdino.jd_spacedepot.quest.QuestRequirement;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractLauncherScreen<T extends AbstractLauncherMenu> extends AbstractContainerScreen<T> {
    protected static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("spacedepot", "textures/gui/space_launcher.png");
    protected static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");
    protected static final ResourceLocation BUTTON_SPRITE = ResourceLocation.withDefaultNamespace("widget/button");
    protected static final ResourceLocation BUTTON_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/button_highlighted");

    protected List<DepotQuest> dailyQuests;
    protected int scrollOffset = 0;
    protected DepotQuest selectedQuest = null;
    protected int tickCount = 0;

    protected final int listX, listY, listW, listH;
    protected final int slotX, slotY;
    protected final int rewardX, rewardY;
    protected final int btnX, btnY, btnW, btnH;

    public AbstractLauncherScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 10000;
        this.titleLabelY = 5;

        // Common Coordinates relative to top-left of GUI
        this.listX = 7; this.listY = 15; this.listW = 85; this.listH = 65;
        this.slotX = 100; this.slotY = 20;
        this.rewardX = 130; this.rewardY = 20;
        this.btnX = 100; this.btnY = 45; this.btnW = 50; this.btnH = 20;
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null && this.minecraft.level != null) {
            this.dailyQuests = QuestManager.get().getQuestsForDay(this.minecraft.level.getDayTime() / 24000L);
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        tickCount++;
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

        // Calculate absolute coordinates
        int absListX = x + listX, absListY = y + listY;

        // 1. List Background
        guiGraphics.fill(absListX, absListY, absListX + listW, absListY + listH, 0xFF111111);

        // 2. Render Quest List
        guiGraphics.enableScissor(absListX, absListY, absListX + listW, absListY + listH);
        for (int i = 0; i < dailyQuests.size(); i++) {
            int relativeIndex = i - scrollOffset;
            int entryY = absListY + (relativeIndex * 24);

            if (entryY + 24 < absListY || entryY > absListY + listH) continue;

            DepotQuest q = dailyQuests.get(i);
            boolean isHovered = (mouseX >= absListX && mouseX <= absListX + listW && mouseY >= entryY && mouseY <= entryY + 24);
            boolean isSelected = (selectedQuest == q);

            if (isSelected) {
                // Full fill for selection
                guiGraphics.fill(absListX, entryY, absListX + listW, entryY + 23, getSelectionColor(q));
            } else {
                // Background
                int bgCol = isHovered ? (q.isSpecial() ? 0xFF221A2A : 0xFF444444) : (q.isSpecial() ? 0xFF15101A : 0xFF2A2A2A);
                guiGraphics.fill(absListX, entryY, absListX + listW, entryY + 23, bgCol);

                // Rotating Border with 1px Padding
                // We draw at x+1, y+1 with width-2, height-2
                drawRotatingBorder(guiGraphics, absListX + 1, entryY + 1, listW - 2, 23 - 2, tickCount + partialTick + (i * 20), q.isSpecial());
            }

            // Icons & Text
            if (!q.requirements().isEmpty()) {
                int reqIndex = (tickCount / 40) % q.requirements().size();
                QuestRequirement req = q.requirements().get(reqIndex);
                guiGraphics.renderItem(req.getIcon(), absListX + 2, entryY + 4);

                String countStr = String.valueOf(req.count());
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0, 0, 200);
                guiGraphics.pose().scale(0.5f, 0.5f, 1.0f);
                int txtX = (int) ((absListX + 18) / 0.5f) - font.width(countStr);
                int txtY = (int) ((entryY + 14) / 0.5f);
                guiGraphics.drawString(font, countStr, txtX, txtY, 0xFFFFFF, true);
                guiGraphics.pose().popPose();
            }

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.5f, 0.5f, 1.0f);
            guiGraphics.drawString(font, q.getDisplayName(), (absListX + 20) * 2, (entryY + 5) * 2, 0xFFFFFF);
            if (!q.requirements().isEmpty()) {
                int reqIndex = (tickCount / 40) % q.requirements().size();
                QuestRequirement req = q.requirements().get(reqIndex);
                guiGraphics.drawString(font, req.getIcon().getHoverName(), (absListX + 20) * 2, (entryY + 13) * 2, 0xAAAAAA);
            }
            guiGraphics.pose().popPose();
        }
        guiGraphics.disableScissor();

        // 3. Input Slot Outline
        guiGraphics.blitSprite(SLOT_SPRITE, x + slotX - 1, y + slotY - 1, 18, 18);

        // 4. Reward & Button
        if (selectedQuest != null) {
            int absRewardX = x + rewardX, absRewardY = y + rewardY;
            int absBtnX = x + btnX, absBtnY = y + btnY;

            // Reward Background
            guiGraphics.fill(absRewardX - 2, absRewardY - 2, absRewardX + 18, absRewardY + 18, 0xFF555555);
            guiGraphics.fill(absRewardX - 1, absRewardY - 1, absRewardX + 17, absRewardY + 17, 0xFF000000);

            guiGraphics.renderItem(selectedQuest.reward(), absRewardX, absRewardY);
            guiGraphics.renderItemDecorations(font, selectedQuest.reward(), absRewardX, absRewardY);

            // Button
            boolean isBtnHovered = (mouseX >= absBtnX && mouseX <= absBtnX + btnW && mouseY >= absBtnY && mouseY <= absBtnY + btnH);
            guiGraphics.blitSprite(isBtnHovered ? BUTTON_HIGHLIGHTED_SPRITE : BUTTON_SPRITE, absBtnX, absBtnY, btnW, btnH);
            int textColor = isBtnHovered ? 0xFFFFA0A0 : 0xFFFFFFFF;
            guiGraphics.drawCenteredString(font, getButtonText(selectedQuest), absBtnX + (btnW / 2), absBtnY + (btnH - 8) / 2, textColor);
        } else {
            guiGraphics.drawString(font, "Select", x + slotX, y + btnY + 6, 0x404040, false);
        }

        // 5. Energy Bar
        int power = menu.getEnergy();
        int max = menu.getMaxEnergy();
        if (max > 0) {
            int barX = x + 160, barY = y + 10, barH = 65;
            int filled = (int) ((float) power / max * barH);
            guiGraphics.fill(barX, barY, barX + 6, barY + barH, 0xFF330000);
            guiGraphics.fill(barX, barY + (barH - filled), barX + 6, barY + barH, 0xFFFF0000);
        }
    }

    protected abstract int getSelectionColor(DepotQuest q);
    protected abstract String getButtonText(DepotQuest q);
    protected abstract void onButtonClick();

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Energy Tooltip
        if (mouseX >= x + 160 && mouseX <= x + 166 && mouseY >= y + 10 && mouseY <= y + 75) {
            NumberFormat fmt = NumberFormat.getInstance(Locale.US);
            String tooltip = fmt.format(menu.getEnergy()) + " / " + fmt.format(menu.getMaxEnergy()) + " FE";
            guiGraphics.renderTooltip(font, Component.literal(tooltip), mouseX, mouseY);
        }

        // Reward Tooltip
        if (selectedQuest != null && mouseX >= x + rewardX && mouseX <= x + rewardX + 16 && mouseY >= y + rewardY && mouseY <= y + rewardY + 16) {
            guiGraphics.renderTooltip(font, selectedQuest.reward(), mouseX, mouseY);
        }

        // List Tooltip
        int absListX = x + listX, absListY = y + listY;
        if (mouseX >= absListX && mouseX <= absListX + listW && mouseY >= absListY && mouseY <= absListY + listH) {
            int index = (int) ((mouseY - absListY) / 24) + scrollOffset;
            if (index >= 0 && index < dailyQuests.size()) {
                DepotQuest q = dailyQuests.get(index);
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(q.getDisplayName().copy().withStyle(ChatFormatting.YELLOW));
                for(QuestRequirement req : q.requirements()) {
                    tooltip.add(Component.translatable(req.getIcon().getDescriptionId()).append(" x" + req.count()).withStyle(ChatFormatting.GRAY));
                }
                guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int absListX = x + listX, absListY = y + listY;
        int absBtnX = x + btnX, absBtnY = y + btnY;

        // List Click
        if (mouseX >= absListX && mouseX <= absListX + listW && mouseY >= absListY && mouseY <= absListY + listH) {
            int idx = (int) ((mouseY - absListY) / 24) + scrollOffset;
            if (idx >= 0 && idx < dailyQuests.size()) {
                this.selectedQuest = dailyQuests.get(idx);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        // Button Click
        if (selectedQuest != null && mouseX >= absBtnX && mouseX <= absBtnX + btnW && mouseY >= absBtnY && mouseY <= absBtnY + btnH) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            onButtonClick();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int absListX = x + listX, absListY = y + listY;
        if (mouseX >= absListX && mouseX <= absListX + listW && mouseY >= absListY && mouseY <= absListY + listH) {
            if (scrollY > 0) scrollOffset = Math.max(0, scrollOffset - 1);
            if (scrollY < 0) scrollOffset = Math.min(Math.max(0, dailyQuests.size() - 2), scrollOffset + 1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    // --- GRADIENT LOGIC ---
    private void drawRotatingBorder(GuiGraphics guiGraphics, int x, int y, int w, int h, float time, boolean isSpecial) {
        float perimeter = 2 * w + 2 * h;
        float speed = 2.0f;
        float currentPos = (time * speed) % perimeter;
        float tTL = (0 - currentPos + perimeter) % perimeter / perimeter;
        float tTR = (w - currentPos + perimeter) % perimeter / perimeter;
        float tBR = (w + h - currentPos + perimeter) % perimeter / perimeter;
        float tBL = (2 * w + h - currentPos + perimeter) % perimeter / perimeter;
        int cTL = isSpecial ? getHummingPurpleGold(tTL * Mth.TWO_PI) : getSubtleGrey(tTL * Mth.TWO_PI);
        int cTR = isSpecial ? getHummingPurpleGold(tTR * Mth.TWO_PI) : getSubtleGrey(tTR * Mth.TWO_PI);
        int cBR = isSpecial ? getHummingPurpleGold(tBR * Mth.TWO_PI) : getSubtleGrey(tBR * Mth.TWO_PI);
        int cBL = isSpecial ? getHummingPurpleGold(tBL * Mth.TWO_PI) : getSubtleGrey(tBL * Mth.TWO_PI);
        drawHorizontalGradient(guiGraphics, x, y, w, 1, cTL, cTR);
        drawVerticalGradient(guiGraphics, x + w - 1, y, 1, h, cTR, cBR);
        drawHorizontalGradient(guiGraphics, x, y + h - 1, w, 1, cBL, cBR);
        drawVerticalGradient(guiGraphics, x, y, 1, h, cTL, cBL);
    }
    private void drawHorizontalGradient(GuiGraphics graphics, int x, int y, int width, int height, int colorStart, int colorEnd) {
        Matrix4f matrix = graphics.pose().last().pose();
        VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.gui());
        float z = 0;
        buffer.addVertex(matrix, x, y, z).setColor(colorStart);
        buffer.addVertex(matrix, x, y + height, z).setColor(colorStart);
        buffer.addVertex(matrix, x + width, y + height, z).setColor(colorEnd);
        buffer.addVertex(matrix, x + width, y, z).setColor(colorEnd);
    }
    private void drawVerticalGradient(GuiGraphics graphics, int x, int y, int width, int height, int colorStart, int colorEnd) {
        graphics.fillGradient(x, y, x + width, y + height, colorStart, colorEnd);
    }
    private int getHummingPurpleGold(float phase) {
        float t = (Mth.sin(phase) + 1.0f) * 0.5f;
        int r = (int) (80 + (160 - 80) * t);
        int g = (int) (20 + (120 - 20) * t);
        int b = (int) (100 + (40 - 100) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
    private int getSubtleGrey(float phase) {
        float t = (Mth.sin(phase) + 1.0f) * 0.5f;
        int val = (int) (40 + (90 - 40) * t);
        return 0xFF000000 | (val << 16) | (val << 8) | val;
    }
}