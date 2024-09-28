package net.shadowmage.ancientwarfare.vehicle;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleItems;

public class AWVehicleTab extends CreativeTabs {
	public AWVehicleTab() {
		super("tabs.vehicles");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack createIcon() {
		return new ItemStack(AWVehicleItems.SPAWNER);
	}
}
