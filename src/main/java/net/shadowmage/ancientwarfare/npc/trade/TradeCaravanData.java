package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;


import java.util.ArrayList;
import java.util.List;

/**
 * TradeCaravanData represents a single trade caravan.
 * Each caravan has:
 *  - Two merchant NPCs (representing faction traders)
 *  - Three to four escort NPCs (soldiers)
 *  - One driver NPC (operating a cart or chest)
 */
public class TradeCaravanData {

    private final String faction;
    private final BlockPos source;
    private final BlockPos destination;

    private final List<Integer> merchantIds = new ArrayList<>();
    private final List<Integer> escortIds = new ArrayList<>();

    private int driverId = -1;

    public TradeCaravanData(String faction, BlockPos source, BlockPos destination) {
        this.faction = faction;
        this.source = source;
        this.destination = destination;
    }

    /** Spawns the merchants, escorts, and driver in the world. */
    public void spawnCaravan(net.minecraft.world.World world) {
        // === Merchants (2 traders) ===
        for (int i = 0; i < 2; i++) {
            NpcFaction merchant = (NpcFaction) AWNPCEntities.createNpc(world, "merchant", "trader", faction);
            if (merchant != null) {
                merchant.setPosition(source.getX() + (i * 1.5), source.getY(), source.getZ() + (i * 1.5));
                world.spawnEntity(merchant);
                merchantIds.add(merchant.getEntityId());
                merchant.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), 1.0D);
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
                // Escorts follow the first merchant
                if (!merchantIds.isEmpty()) {
                    Entity firstMerchant = world.getEntityByID(merchantIds.get(0));
                    if (firstMerchant != null) {
                        soldier.getNavigator().tryMoveToEntityLiving((net.minecraft.entity.EntityLivingBase) firstMerchant, 1.1D);
                    }
                }
            }
        }

        // === Driver (chest cart handler) ===
        NpcFaction driver = (NpcFaction) AWNPCEntities.createNpc(world, "combat", "siege_engineer", faction);
        if (driver != null) {
            driver.setPosition(source.getX() + 1, source.getY(), source.getZ() - 1);
            world.spawnEntity(driver);
            driver.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), 1.0D);
            driverId = driver.getEntityId();
        }
    }

    /** Despawns the caravan after trade completion or failure. */
    public void cleanup(net.minecraft.world.World world) {
        for (int id : merchantIds) {
            net.minecraft.entity.Entity e = world.getEntityByID(id);
            if (e != null) e.setDead();
        }
        for (int id : escortIds) {
            net.minecraft.entity.Entity e = world.getEntityByID(id);
            if (e != null) e.setDead();
        }
        if (driverId >= 0) {
            net.minecraft.entity.Entity e = world.getEntityByID(driverId);
            if (e != null) e.setDead();
        }
    }

    /** Serialize this caravan to NBT. */
    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("faction", faction);
        tag.setInteger("srcX", source.getX());
        tag.setInteger("srcY", source.getY());
        tag.setInteger("srcZ", source.getZ());
        tag.setInteger("dstX", destination.getX());
        tag.setInteger("dstY", destination.getY());
        tag.setInteger("dstZ", destination.getZ());
        return tag;
    }

    /** Deserialize caravan data from NBT. */
    public static TradeCaravanData readFromNBT(NBTTagCompound tag) {
        String faction = tag.getString("faction");
        BlockPos src = new BlockPos(tag.getInteger("srcX"), tag.getInteger("srcY"), tag.getInteger("srcZ"));
        BlockPos dst = new BlockPos(tag.getInteger("dstX"), tag.getInteger("dstY"), tag.getInteger("dstZ"));
        return new TradeCaravanData(faction, src, dst);
    }
}

