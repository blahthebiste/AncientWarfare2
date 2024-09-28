package net.shadowmage.ancientwarfare.structure;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.structure.init.AWStructureItems;

public class AWStructureTab extends CreativeTabs {
	public AWStructureTab() {
		super("tabs.structures");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack createIcon() {
		return new ItemStack(AWStructureItems.STRUCTURE_SCANNER);
	}
}
