package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * TradeCaravanData represents a single trade caravan.
 * Each caravan has:
 *  - Two merchant NPCs
 *  - Three to four escort NPCs
 *  - One driver NPC
 */
public class TradeCaravanData {

    private final String faction;
    private final BlockPos source;
    private final BlockPos destination;
    private final List<Integer> merchantIds = new ArrayList<>();
    private final List<Integer> escortIds = new ArrayList<>();
    private int driverId = -1;

    private boolean arrivedAtDestination = false;
    private long arrivalTime = 0L;
    private static final long MAX_STAY_DURATION_TICKS = 48000L; // ~2 in-game days

    public TradeCaravanData(String faction, BlockPos source, BlockPos destination) {
        this.faction = faction;
        this.source = source;
        this.destination = destination;
    }

    public void spawnCaravan(World world) {
        // === Merchants (2 traders) ===
        for (int i = 0; i < 2; i++) {
            NpcFaction merchant = (NpcFaction) AWNPCEntities.createNpc(world, "merchant", "trader", faction);
            if (merchant != null) {
                merchant.setPosition(source.getX() + (i * 1.5), source.getY(), source.getZ() + (i * 1.5));
                world.spawnEntity(merchant);
                merchantIds.add(merchant.getEntityId());
                merchant.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), 0.8D);
            }
        }

        // === Escorts (3-4 soldiers) ===
        int escortCount = 3 + world.rand.nextInt(2);
        for (int i = 0; i < escortCount; i++) {
            NpcFaction soldier = (NpcFaction) AWNPCEntities.createNpc(world, "combat", "soldier", faction);
            if (soldier != null) {
                soldier.setPosition(source.getX() + 2 + i, source.getY(), source.getZ() + 1 + i);
                world.spawnEntity(soldier);
                escortIds.add(soldier.getEntityId());
                if (!merchantIds.isEmpty()) {
                    Entity firstMerchant = world.getEntityByID(merchantIds.get(0));
                    if (firstMerchant != null) {
                        soldier.getNavigator().tryMoveToEntityLiving((net.minecraft.entity.EntityLivingBase) firstMerchant, 1.1D);
                    }
                }
            }
        }

        // === Driver (cart handler) ===
        NpcFaction driver = (NpcFaction) AWNPCEntities.createNpc(world, "combat", "siege_engineer", faction);
        if (driver != null) {
            driver.setPosition(source.getX() + 1, source.getY(), source.getZ() - 1);
            world.spawnEntity(driver);
            driver.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), 0.8D);
            driverId = driver.getEntityId();
        }
    }

    public boolean updateAndCheckLifecycle(World world) {
        if (!arrivedAtDestination) {
            for (int id : merchantIds) {
                Entity e = world.getEntityByID(id);
                if (e != null && e.getDistanceSq(destination) < 25.0) {
                    arrivedAtDestination = true;
                    arrivalTime = world.getTotalWorldTime();
                    break;
                }
            }
        } else {
            long elapsed = world.getTotalWorldTime() - arrivalTime;
            if (elapsed >= MAX_STAY_DURATION_TICKS) {
                return true; // Despawn
            }
        }
        return false;
    }

    public void cleanup(World world) {
        for (int id : merchantIds) {
            Entity e = world.getEntityByID(id);
            if (e != null) e.setDead();
        }
        for (int id : escortIds) {
            Entity e = world.getEntityByID(id);
            if (e != null) e.setDead();
        }
        if (driverId >= 0) {
            Entity e = world.getEntityByID(driverId);
            if (e != null) e.setDead();
        }
    }

    public BlockPos getMidpoint() {
        int x = (source.getX() + destination.getX()) / 2;
        int y = (source.getY() + destination.getY()) / 2;
        int z = (source.getZ() + destination.getZ()) / 2;
        return new BlockPos(x, y, z);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("faction", faction);
        tag.setInteger("srcX", source.getX());
        tag.setInteger("srcY", source.getY());
        tag.setInteger("srcZ", source.getZ());
        tag.setInteger("dstX", destination.getX());
        tag.setInteger("dstY", destination.getY());
        tag.setInteger("dstZ", destination.getZ());
        tag.setBoolean("arrived", arrivedAtDestination);
        tag.setLong("arrivalTime", arrivalTime);
        return tag;
    }

    public static TradeCaravanData readFromNBT(NBTTagCompound tag) {
        String faction = tag.getString("faction");
        BlockPos src = new BlockPos(tag.getInteger("srcX"), tag.getInteger("srcY"), tag.getInteger("srcZ"));
        BlockPos dst = new BlockPos(tag.getInteger("dstX"), tag.getInteger("dstY"), tag.getInteger("dstZ"));
        TradeCaravanData data = new TradeCaravanData(faction, src, dst);
        data.arrivedAtDestination = tag.getBoolean("arrived");
        data.arrivalTime = tag.getLong("arrivalTime");
        return data;
    }
}
