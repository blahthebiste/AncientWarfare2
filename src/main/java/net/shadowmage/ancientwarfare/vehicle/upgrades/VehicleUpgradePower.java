package net.shadowmage.ancientwarfare.vehicle.upgrades;

import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class VehicleUpgradePower extends VehicleUpgradeBase {

	public VehicleUpgradePower() {
		super("vehicle_upgrade_power");
	}

	@Override
	public void applyVehicleEffects(VehicleBase vehicle) {
        // Why did this increase missile speed by 10% instead of a flat 2m/s like the upgrade claimed to?
        //vehicle.currentLaunchSpeedPowerMax *= 1.1f;
        // New code that operates as written:
        vehicle.currentLaunchSpeedPowerMax += AWCoreStatics.vehicleUpgradeProjectileSpeed;
	}

}
