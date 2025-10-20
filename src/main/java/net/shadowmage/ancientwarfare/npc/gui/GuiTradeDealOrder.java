package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.npc.container.ContainerTradeDealOrder;

public class GuiTradeDealOrder extends GuiContainer {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ancientwarfare", "textures/gui/trade_deal_order.png");

    private final ContainerTradeDealOrder container;

    public GuiTradeDealOrder(ContainerTradeDealOrder container) {
        super(container);
        this.container = container;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("gui.trade_deal_order.title");
        String offered = I18n.format("gui.trade_deal_order.offered");
        String requested = I18n.format("gui.trade_deal_order.requested");

        fontRenderer.drawString(title, 8, 6, 0x404040);
        fontRenderer.drawString(offered, 8, 18, 0x00FF00);
        fontRenderer.drawString(requested, 8, 58, 0xFFCC00);
    }
}
