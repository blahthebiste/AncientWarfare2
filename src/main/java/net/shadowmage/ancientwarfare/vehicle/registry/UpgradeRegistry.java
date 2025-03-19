package net.shadowmage.ancientwarfare.vehicle.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.vehicle.item.ItemUpgrade;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradeAim;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradePitchDown;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradePitchUp;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradePower;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradeReload;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradeSpeed;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradeTurretPitch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UpgradeRegistry {

	public static IVehicleUpgradeType speedUpgrade;
	public static IVehicleUpgradeType aimUpgrade;
	public static IVehicleUpgradeType reloadUpgrade;

	public static IVehicleUpgradeType powerUpgrade;
	public static IVehicleUpgradeType pitchExtUpgrade;
	public static IVehicleUpgradeType pitchUpUpgrade;
	public static IVehicleUpgradeType pitchDownUpgrade;

	private static Map<ResourceLocation, IVehicleUpgradeType> upgradeInstances = new HashMap<>();

	private UpgradeRegistry() {
	}

	public static UpgradeRegistry instance() {
		if (INSTANCE == null) {
			INSTANCE = new UpgradeRegistry();
		}
		return INSTANCE;
	}

	private static UpgradeRegistry INSTANCE;

	public Collection<IVehicleUpgradeType> getUpgradeList() {
		return this.upgradeInstances.values();
	}

	/**
	 * called during init to register upgrade types as items
	 */
	public static void registerUpgrades(IForgeRegistry<Item> registry) {
		speedUpgrade = registerUpgrade(new VehicleUpgradeSpeed(), registry, ""+AWCoreStatics.vehicleUpgradeMaxSpeed);
		aimUpgrade = registerUpgrade(new VehicleUpgradeAim(), registry, Float.toString(AWCoreStatics.vehicleUpgradeAccuracy));
		reloadUpgrade = registerUpgrade(new VehicleUpgradeReload(), registry, Integer.toString(AWCoreStatics.vehicleUpgradeReloadSpeed));
		powerUpgrade = registerUpgrade(new VehicleUpgradePower(), registry, ""+AWCoreStatics.vehicleUpgradeProjectileSpeed);
		pitchExtUpgrade = registerUpgrade(new VehicleUpgradeTurretPitch(), registry, ""+AWCoreStatics.vehicleUpgradePitchExtension);
		pitchUpUpgrade = registerUpgrade(new VehicleUpgradePitchUp(), registry, ""+AWCoreStatics.vehicleUpgradePitchUp);
		pitchDownUpgrade = registerUpgrade(new VehicleUpgradePitchDown(), registry, ""+AWCoreStatics.vehicleUpgradePitchDown);
	}

	private static IVehicleUpgradeType registerUpgrade(IVehicleUpgradeType upgrade, IForgeRegistry<Item> registry) {
		upgradeInstances.put(upgrade.getRegistryName(), upgrade);
		ItemUpgrade item = new ItemUpgrade(upgrade.getRegistryName(), "");
		registry.register(item);
		return upgrade;
	}

	private static IVehicleUpgradeType registerUpgrade(IVehicleUpgradeType upgrade, IForgeRegistry<Item> registry, String dynamicInfo) {
		upgradeInstances.put(upgrade.getRegistryName(), upgrade);
		ItemUpgrade item = new ItemUpgrade(upgrade.getRegistryName(), dynamicInfo);
		registry.register(item);
		return upgrade;
	}

	public static Optional<IVehicleUpgradeType> getUpgrade(ResourceLocation type) {
		return Optional.ofNullable(upgradeInstances.get(type));
	}

	public static Optional<IVehicleUpgradeType> getUpgrade(ItemStack stack) {
		return Optional.ofNullable(upgradeInstances.get(stack.getItem().getRegistryName()));
	}
}
