package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;

public class GuiTownHallOptions extends GuiContainerBase<ContainerTownHall> {

    private final GuiTownHallInventory parent;

    public GuiTownHallOptions(GuiTownHallInventory parent) {
        super(parent.getContainer());
        this.parent = parent;
        this.ySize = 3 * 18 + 4 * 18 + 48;
        this.xSize = 178;
    }

    @Override
    public void initElements() {
        int x = 8;
        int y = 8;
        int width = 120;
        int height = 14;
        int spacing = 20;

        Button deathList = new Button(x, y, width, height, I18n.format("guistrings.npc.death_list")) {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(new GuiTownHallDeathList(parent));
            }
        };
        addGuiElement(deathList);

        Button reinf = new Button(x, y + spacing, width, height, I18n.format("guistrings.npc.reinforcements")) {
            @Override
            protected void onPressed() {
                getContainer().removeSlots();
                getContainer().requestFriendlyFactions();
                Minecraft.getMinecraft().displayGuiScreen(new GuiTownHallReinforcements(parent));
            }
        };
        addGuiElement(reinf);

        Button back = new Button(x, y + 2 * spacing, width, height, I18n.format("gui.back")) {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(back);
    }

    @Override
    public void setupElements() {
        // No dynamic elements to update in this menu
    }
}
