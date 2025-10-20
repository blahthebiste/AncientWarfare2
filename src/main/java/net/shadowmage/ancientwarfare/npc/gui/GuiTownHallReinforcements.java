package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;
import net.shadowmage.ancientwarfare.npc.raid.reinforcements.ReinforcementManager;
import net.shadowmage.ancientwarfare.npc.raid.reinforcements.ReinforcementUtils;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import java.io.IOException;
import java.util.List;

public class GuiTownHallReinforcements extends GuiContainerBase<ContainerTownHall> {

    private final GuiTownHallInventory parent;
    private CompositeScrolled area;

    public GuiTownHallReinforcements(GuiTownHallInventory parent) {
        super(parent.getContainer());
        this.parent = parent;
    }

    @Override
    public void initElements() {
        Label title = new Label(6, 6, I18n.format("guistrings.npc.reinforcements.title"));
        addGuiElement(title);

        area = new CompositeScrolled(this, 6, 18, xSize - 12, ySize - 40);
        addGuiElement(area);

        Button back = new Button(xSize / 2 - 25, ySize - 16, 50, 12, "gui.button.back") {
            @Override protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(parent);
                getContainer().addSlots();
                parent.refreshGui();
            }
        };
        addGuiElement(back);
    }

    @Override
    public void setupElements() {
        area.clearElements();

        List<String> factions = getContainer().getFriendlyFactions();
        int y = 0;

        for (String factionName : factions) {
            area.addGuiElement(new Label(4, y, factionName));
            area.addGuiElement(new Label(110, y, I18n.format("guistrings.npc.reinf_cost", 5)));

            Button callBtn = new Button(170, y - 2, 30, 12, "guistrings.npc.call") {
                @Override protected void onPressed() {
                    getContainer().callReinforcements(factionName);
                }
            };
            area.addGuiElement(callBtn);
            y += 14;
        }

        area.setAreaSize(y);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        getContainer().addSlots();
        parent.refreshGui();
        return false;
    }
}


