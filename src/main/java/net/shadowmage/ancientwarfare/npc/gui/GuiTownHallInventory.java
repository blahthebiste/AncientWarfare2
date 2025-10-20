package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;

public class GuiTownHallInventory extends GuiContainerBase<ContainerTownHall> {

	private NumberInput input;
	private Text name;

	public GuiTownHallInventory(ContainerBase container) {
		super(container);
		this.ySize = 3 * 18 + 4 * 18 + 8 + 8 + 4 + 8 + 16;
		this.xSize = 178;
	}

    @Override
    public void initElements() {
        this.getContainer().addSlots();

        // Replace Death List with Options Button
        Button optionsButton = new Button(8, 8, 60, 12, "Options") {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(new GuiTownHallOptions(GuiTownHallInventory.this));
            }
        };
        addGuiElement(optionsButton);

        addGuiElement(new Label(110, 10, "guistrings.npc.town_range"));
        input = new NumberInput(145, 8, 24, getContainer().tileEntity.getRange(), this);
        input.setIntegerValue();
        addGuiElement(input);
    }

	@Override
	public void setupElements() {
		// draw label for naming town hall
		name = new Text(50, 8, 54, getContainer().tileEntity.name, this);
		addGuiElement(name);

		input.setValue(getContainer().tileEntity.getRange());
	}

	@Override
	protected boolean onGuiCloseRequested() {
		if (getContainer().tileEntity.getRange() != input.getIntegerValue())
			getContainer().setRange(input.getIntegerValue());
		if (!getContainer().tileEntity.name.equals(name.getText()))
			getContainer().setName(name.getText());

		return super.onGuiCloseRequested();
	}
}
