package net.shadowmage.ancientwarfare.npc.raid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;
import net.shadowmage.ancientwarfare.npc.raid.ai.NpcAIRaidAttack;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionRestrictSun;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionFleeSun;

import java.util.*;

/**
 * RaidManager handles scheduling, spawning, and managing raid events.
 */
@Mod.EventBusSubscriber(modid = AncientWarfareCore.MOD_ID)
public class RaidManager {

    private static final Map<UUID, RaidData> ACTIVE_RAIDS = new HashMap<>();
    public static final boolean RAIDS_ENABLED = true;
    public static final long RAID_INITIAL_DELAY = 200L; // ~10 seconds for testing

    /** Helper wrapper for logical → registry NPC mapping */
    private static class NpcKey {
        final String typeKey;
        final String subtype;
        NpcKey(String typeKey, String subtype) {
            this.typeKey = typeKey;
            this.subtype = subtype;
        }
    }

    /** Maps logical names (used in raids) to actual entity registry names */
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

    /** Schedule a raid to occur later */
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

    /** Start a raid immediately */
    public static void startRaidNow(World world, BlockPos center, String faction, RaidSize size) {
        RaidData raid = new RaidData(world.getWorldTime(), faction, center, size);
        spawnRaid(world, raid);
    }

    /** Register the tick event for processing scheduled raids */
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

    /** Check active raids each tick */
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

    /** Spawn a raid force */
    private static void spawnRaid(World world, RaidData raid) {
        RaidSize size = raid.getRaidSize();
        AncientWarfareCore.LOG.info("Spawning " + size.name() + " raid from faction " +
                raid.getFactionName() + " at " + raid.getTargetPos());

        String[] raidUnitTypes = {"soldier", "archer", "leader", "mounted_archer", "cavalry"};
        int forceSize = size.getRandomSize(world.rand);

        for (int i = 0; i < forceSize; i++) {
            BlockPos spawn = RaidUtils.findSpawnEdge(raid.getTargetPos(), world);
            String logical = raidUnitTypes[world.rand.nextInt(raidUnitTypes.length)];

            if (!factionSupportsUnit(raid.getFactionName(), logical)) {
                AncientWarfareCore.LOG.info("Faction " + raid.getFactionName() +
                        " has no " + logical + " — using fallback unit.");
                logical = getFallbackUnit(logical, world);
            }

            NpcKey key = resolveFactionNpc(logical);
            if (key == null) continue;

            NpcBase npc = AWNPCEntities.createNpc(world, key.typeKey, key.subtype, raid.getFactionName());
            if (!(npc instanceof NpcFaction)) continue;

            NpcFaction factionNpc = (NpcFaction) npc;
            factionNpc.setFactionNameAndDefaults(raid.getFactionName());
            injectFullRaidAI(factionNpc, raid.getTargetPos(), 1.25D);

            npc.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
            world.spawnEntity(npc);
            faceTarget(npc, raid.getTargetPos());
            npc.getEntityData().setBoolean("isRaidUnit", true);
        }

        // Elite / commander units
        int elites = size.getLeaderCount(world.rand);
        for (int i = 0; i < elites; i++) {
            BlockPos spawn = RaidUtils.findSpawnEdge(raid.getTargetPos(), world);
            NpcKey eliteKey = resolveFactionNpc("elite_commander");
            if (eliteKey == null || !factionSupportsUnit(raid.getFactionName(), "elite_commander")) {
                AncientWarfareCore.LOG.info("Faction " + raid.getFactionName() +
                        " has no elite units — replacing with base soldier.");
                eliteKey = resolveFactionNpc(getFallbackUnit("soldier", world));
            }

            NpcBase elite = AWNPCEntities.createNpc(world, eliteKey.typeKey, eliteKey.subtype, raid.getFactionName());
            if (!(elite instanceof NpcFaction)) continue;

            NpcFaction eliteNpc = (NpcFaction) elite;
            eliteNpc.setFactionNameAndDefaults(raid.getFactionName());
            injectFullRaidAI(eliteNpc, raid.getTargetPos(), 1.35D);

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
        MinecraftForge.EVENT_BUS.post(new net.shadowmage.ancientwarfare.npc.raid.event.RaidStartedEvent(
                world, raid.getFactionName(), raid.getTargetPos(), null));
    }

    /** Adds raid + base faction AIs */
    private static void injectFullRaidAI(NpcFaction npc, BlockPos townHall, double speed) {
        npc.setNoAI(false);
        npc.tasks.taskEntries.removeIf(e -> e.action instanceof NpcAIRaidAttack);

        npc.tasks.addTask(0, new NpcAIRaidAttack(npc, townHall, speed));
        npc.tasks.addTask(2, new NpcAIFactionRestrictSun(npc));
        npc.tasks.addTask(3, new NpcAIFactionFleeSun(npc, 1.0D));
        npc.tasks.addTask(2, new NpcAIDoor(npc, true));
        npc.tasks.addTask(4, new NpcAIWander(npc, 0.8D));
        npc.tasks.addTask(7, new NpcAIWatchClosest(npc, EntityPlayer.class, 8.0F));

        npc.getNavigator().clearPath();
        npc.getNavigator().tryMoveToXYZ(townHall.getX() + 0.5, townHall.getY(), townHall.getZ() + 0.5, speed);
    }

    /** Faction capabilities — determines which unit types are valid */
    private static boolean factionSupportsUnit(String faction, String unitType) {
        String f = faction.toLowerCase();
        String u = unitType.toLowerCase();

        // Bandit-like factions
        if (f.contains("bandit") || f.contains("pirate") || f.contains("raider")) {
            return !(u.contains("mounted") || u.contains("cavalry") || u.contains("elite"));
        }

        // Undead or dark factions
        if (f.contains("undead") || f.contains("skeleton") || f.contains("zombie")) {
            return !(u.contains("mounted") || u.contains("cavalry") || u.contains("elite"));
        }

        // Civilians / traders
        if (f.contains("civilian") || f.contains("trader")) {
            return u.equals("soldier") || u.equals("archer");
        }

        return true; // default: full combat capability
    }

    /** Fallback to soldier or archer */
    private static String getFallbackUnit(String unitType, World world) {
        return world.rand.nextBoolean() ? "soldier" : "archer";
    }

    /** Makes NPC face a specific point */
    private static void faceTarget(NpcBase npc, BlockPos target) {
        Vec3d pos = npc.getPositionVector();
        double dx = target.getX() - pos.x;
        double dz = target.getZ() - pos.z;
        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90;
        npc.rotationYaw = (float) yaw;
        npc.rotationYawHead = (float) yaw;
    }
}
