package com.jammingdino.jd_spacedepot.block.crate;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class SpaceCrateScreen extends AbstractContainerScreen<SpaceCrateMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("spacedepot", "textures/gui/space_crate.png");
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");

    public SpaceCrateScreen(SpaceCrateMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // FIX: Removed the "176, 166" arguments at the end.
        // Now it defaults to assuming the texture file is 256x256, which is correct for your new texture.
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        for (Slot slot : this.menu.slots) {
            if (slot.container != this.menu.getSlot(0).container) {
                continue;
            }
            guiGraphics.blitSprite(SLOT_SPRITE, x + slot.x - 1, y + slot.y - 1, 18, 18);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}