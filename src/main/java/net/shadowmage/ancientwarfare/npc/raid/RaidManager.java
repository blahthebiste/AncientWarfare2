package net.shadowmage.ancientwarfare.npc.raid;

import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
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
    public static final long RAID_INITIAL_DELAY = 200L; // 10 seconds
    public static final RaidSize DEFAULT_RAID_SIZE = RaidSize.SMALL;

    public static void scheduleInitialRaid(World world, EntityPlayer player, RaidSize size) {
        if (!RAIDS_ENABLED) return;

        long delay = RAID_INITIAL_DELAY;
        BlockPos townHallPos = RaidUtils.getTownHallPositionNear(world, player.getPosition());
        if (townHallPos == null) townHallPos = player.getPosition();

        String faction = RaidUtils.getRandomHostileFaction();
        RaidData raid = new RaidData(world.getWorldTime() + delay, faction, townHallPos);
        ACTIVE_RAIDS.put(player.getUniqueID(), raid);

        AncientWarfareCore.LOG.info("Scheduled raid for faction " + faction + " at " + townHallPos + " with delay " + delay);
    }

    public static void startRaidNow(World world, BlockPos center, String faction, RaidSize size) {
        RaidData temp = new RaidData(world.getWorldTime(), faction, center);
        spawnRaid(world, temp, size);
    }

    @SubscribeEvent
    public static void onWorldTick(WorldEvent.Load event) {
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
        Iterator<RaidData> iterator = ACTIVE_RAIDS.values().iterator();
        while (iterator.hasNext()) {
            RaidData raid = iterator.next();
            if (time >= raid.getStartTime()) {
                spawnRaid(world, raid, DEFAULT_RAID_SIZE);
                iterator.remove();
            }
        }
    }

    public static void spawnRaid(World world, RaidData raid, RaidSize size) {
        AncientWarfareCore.LOG.info("Starting raid from " + raid.getFactionName() + " at " + raid.getTargetPos());

        int forceSize = size.getRandomSize(world.rand);
        for (int i = 0; i < forceSize; i++) {
            BlockPos spawn = RaidUtils.findSpawnEdge(raid.getTargetPos(), world);
            String[] raidUnitTypes = { "soldier", "archer", "leader", "mounted_archer", "cavalry" };
            String unitType = raidUnitTypes[world.rand.nextInt(raidUnitTypes.length)];

            NpcBase npc = AWNPCEntities.createNpc(world, "combat", unitType, raid.getFactionName());
            if (npc instanceof NpcFaction) {
                ((NpcFaction) npc).setFactionNameAndDefaults(raid.getFactionName());
                npc.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
                world.spawnEntity(npc);
                faceTarget(npc, raid.getTargetPos());
                npc.getEntityData().setBoolean("isRaidUnit", true);
            }
        }

        int elites = size.getLeaderCount(world.rand);
        for (int i = 0; i < elites; i++) {
            BlockPos spawn = RaidUtils.findSpawnEdge(raid.getTargetPos(), world);
            NpcBase elite = AWNPCEntities.createNpc(world, "combat", "elite_commander", raid.getFactionName());
            if (elite instanceof NpcFaction) {
                ((NpcFaction) elite).setFactionNameAndDefaults(raid.getFactionName());
                elite.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
                world.spawnEntity(elite);
                faceTarget(elite, raid.getTargetPos());
                elite.getEntityData().setBoolean("isRaidUnit", true);
            }
        }

        TileTownHall townHall = RaidUtils.getTownHallTileAt(world, raid.getTargetPos());
        if (townHall != null)
            AncientWarfareCore.LOG.info("Town Hall found at " + townHall.getPos() + " — alarm triggered.");
        else
            AncientWarfareCore.LOG.warn("No Town Hall found at raid target " + raid.getTargetPos());

        RaidUtils.broadcastMessage(world, "Raiders from " + raid.getFactionName() + " are attacking!");
        MinecraftForge.EVENT_BUS.post(new net.shadowmage.ancientwarfare.npc.raid.event.RaidStartedEvent(world, raid.getFactionName(), raid.getTargetPos(), null));
    }

    private static void faceTarget(NpcBase npc, BlockPos target) {
        Vec3d npcPos = npc.getPositionVector();
        double dx = target.getX() - npcPos.x;
        double dz = target.getZ() - npcPos.z;
        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90;
        npc.rotationYaw = (float) yaw;
        npc.rotationYawHead = (float) yaw;
    }
}
