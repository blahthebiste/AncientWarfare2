package net.shadowmage.ancientwarfare.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.init.AWCoreItems;

public class AWCoreTab extends CreativeTabs {
	public AWCoreTab() {
		super("tabs.awcore");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack createIcon() {
		return new ItemStack(AWCoreItems.RESEARCH_BOOK);
	}
}
