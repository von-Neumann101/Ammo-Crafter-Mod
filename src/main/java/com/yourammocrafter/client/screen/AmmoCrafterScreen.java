package com.yourammocrafter.client.screen;

import com.yourammocrafter.menu.AmmoCrafterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class AmmoCrafterScreen extends AbstractContainerScreen<AmmoCrafterMenu> {
    private static final int BACKGROUND_COLOR = 0xFF2F3338;
    private static final int PANEL_COLOR = 0xFF41474F;
    private static final int SLOT_BORDER_COLOR = 0xFF202327;
    private static final int SLOT_FILL_COLOR = 0xFF8A929C;

    public AmmoCrafterScreen(AmmoCrafterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 194;
        this.imageHeight = 204;
        this.inventoryLabelY = 90;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xE6E6E6, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xE6E6E6, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        guiGraphics.fill(left, top, left + this.imageWidth, top + this.imageHeight, BACKGROUND_COLOR);
        guiGraphics.fill(left + 7, top + 17, left + 187, top + 187, PANEL_COLOR);

        drawSlotGrid(guiGraphics, left + 87, top + 35, 1, 1);
        drawSlotGrid(guiGraphics, left + 16, top + 17, 3, 3);
        drawSlotGrid(guiGraphics, left + 122, top + 17, 3, 3);
        drawSlotGrid(guiGraphics, left + 16, top + 101, 9, 3);
        drawSlotGrid(guiGraphics, left + 16, top + 159, 9, 1);
    }

    private static void drawSlotGrid(GuiGraphics guiGraphics, int x, int y, int columns, int rows) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int slotX = x + column * 18;
                int slotY = y + row * 18;
                guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, SLOT_BORDER_COLOR);
                guiGraphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, SLOT_FILL_COLOR);
            }
        }
    }
}
