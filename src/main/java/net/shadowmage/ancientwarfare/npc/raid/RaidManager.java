package net.shadowmage.ancientwarfare.npc.raid;

import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = AncientWarfareCore.MOD_ID)
public class RaidManager {

    private static final Map<UUID, RaidData> ACTIVE_RAIDS = new HashMap<>();
    public static final boolean RAIDS_ENABLED = true;
    public static final long RAID_INITIAL_DELAY = 200L; // ~10s for testing

    /** Logical-to-registry mapping for faction NPCs */
    private static class NpcKey {
        final String typeKey;
        final String subtype;
        NpcKey(String typeKey, String subtype) {
            this.typeKey = typeKey;
            this.subtype = subtype;
        }
    }

    private static NpcKey resolveFactionNpc(String logical) {
        switch (logical) {
            case "soldier": return new NpcKey(AWEntityRegistry.NPC_FACTION_SOLDIER, "soldier");
            case "archer": return new NpcKey(AWEntityRegistry.NPC_FACTION_ARCHER, "archer");
            case "leader": return new NpcKey(AWEntityRegistry.NPC_FACTION_COMMANDER, "commander");
            case "mounted_archer": return new NpcKey(AWEntityRegistry.NPC_FACTION_MOUNTED_ARCHER, "archer");
            case "cavalry": return new NpcKey(AWEntityRegistry.NPC_FACTION_CAVALRY, "soldier");
            case "soldier_elite": return new NpcKey(AWEntityRegistry.NPC_FACTION_SOLDIER_ELITE, "soldier");
            case "archer_elite": return new NpcKey(AWEntityRegistry.NPC_FACTION_ARCHER_ELITE, "archer");
            case "elite_commander": return new NpcKey(AWEntityRegistry.NPC_FACTION_LEADER_ELITE, "commander");
            default: return null;
        }
    }

    /** Schedule a raid for a player’s town hall */
    public static void scheduleInitialRaid(World world, EntityPlayer player, RaidSize size) {
        if (!RAIDS_ENABLED) return;

        long delay = RAID_INITIAL_DELAY;
        BlockPos townHallPos = RaidUtils.getTownHallPositionNear(world, player.getPosition());
        if (townHallPos == null) townHallPos = player.getPosition();

        String faction = RaidUtils.getRandomHostileFaction();
        RaidData raid = new RaidData(world.getWorldTime() + delay, faction, townHallPos, size);
        raid.setOwnerId(player.getUniqueID());

        ACTIVE_RAIDS.put(player.getUniqueID(), raid);
        AncientWarfareCore.LOG.info("Scheduled " + size.name() + " raid for faction " + faction + " at " + townHallPos);
    }

    /** Start an immediate raid */
    public static void startRaidNow(World world, BlockPos center, String faction, RaidSize size) {
        RaidData raid = new RaidData(world.getWorldTime(), faction, center, size);
        spawnRaid(world, raid);
    }

    /** World tick listener to trigger delayed raids */
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        MinecraftForge.EVENT_BUS.register(new Object() {
            @SubscribeEvent
            public void onWorldTick(net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent tick) {
                if (tick.phase == net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END) {
                    tickWorld(tick.world);
                }
            }
        });
    }

    private static void tickWorld(World world) {
        long time = world.getWorldTime();
        Iterator<RaidData> it = ACTIVE_RAIDS.values().iterator();
        while (it.hasNext()) {
            RaidData raid = it.next();
            if (!raid.isSpawned() && time >= raid.getStartTime()) {
                spawnRaid(world, raid);
                raid.setSpawned(true);
                it.remove();
            }
        }
    }

    /** Spawn a raid using the stored RaidData’s RaidSize */
    private static void spawnRaid(World world, RaidData raid) {
        RaidSize size = raid.getRaidSize();
        AncientWarfareCore.LOG.info("Spawning " + size.name() + " raid from faction " + raid.getFactionName() + " at " + raid.getTargetPos());

        String[] raidUnitTypes = {
                "soldier", "archer", "commander", "mounted_archer", "cavalry"
        };

        int forceSize = size.getRandomSize(world.rand);
        for (int i = 0; i < forceSize; i++) {
            BlockPos spawn = RaidUtils.findSpawnEdge(raid.getTargetPos(), world);
            String logical = raidUnitTypes[world.rand.nextInt(raidUnitTypes.length)];
            NpcKey key = resolveFactionNpc(logical);
            if (key == null) {
                AncientWarfareCore.LOG.error("Unknown NPC type '" + logical + "'");
                continue;
            }

            NpcBase npc = AWNPCEntities.createNpc(world, key.typeKey, key.subtype, raid.getFactionName());
            if (npc == null) {
                AncientWarfareCore.LOG.error("Failed to create NPC: " + key.typeKey + " (" + key.subtype + ")");
                continue;
            }

            if (npc instanceof NpcFaction)
                ((NpcFaction) npc).setFactionNameAndDefaults(raid.getFactionName());

            npc.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
            world.spawnEntity(npc);
            faceTarget(npc, raid.getTargetPos());
            npc.getEntityData().setBoolean("isRaidUnit", true);
        }

        // Elite/commander spawn for large raids
        int elites = size.getLeaderCount(world.rand);
        for (int i = 0; i < elites; i++) {
            BlockPos spawn = RaidUtils.findSpawnEdge(raid.getTargetPos(), world);
            NpcKey eliteKey = resolveFactionNpc("leader_elite");
            if (eliteKey == null) continue;

            NpcBase elite = AWNPCEntities.createNpc(world, eliteKey.typeKey, eliteKey.subtype, raid.getFactionName());
            if (elite == null) continue;

            if (elite instanceof NpcFaction)
                ((NpcFaction) elite).setFactionNameAndDefaults(raid.getFactionName());

            elite.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
            world.spawnEntity(elite);
            faceTarget(elite, raid.getTargetPos());
            elite.getEntityData().setBoolean("isRaidUnit", true);
        }

        TileTownHall hall = RaidUtils.getTownHallTileAt(world, raid.getTargetPos());
        if (hall != null)
            AncientWarfareCore.LOG.info("Town Hall located at " + hall.getPos());
        else
            AncientWarfareCore.LOG.warn("No Town Hall found for raid at " + raid.getTargetPos());

        RaidUtils.broadcastMessage(world, "Raiders from " + raid.getFactionName() + " are attacking!");
        MinecraftForge.EVENT_BUS.post(new net.shadowmage.ancientwarfare.npc.raid.event.RaidStartedEvent(world, raid.getFactionName(), raid.getTargetPos(), null));
    }

    private static void faceTarget(NpcBase npc, BlockPos target) {
        Vec3d pos = npc.getPositionVector();
        double dx = target.getX() - pos.x;
        double dz = target.getZ() - pos.z;
        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90;
        npc.rotationYaw = (float) yaw;
        npc.rotationYawHead = (float) yaw;
    }
}
