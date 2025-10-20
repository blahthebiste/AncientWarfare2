package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.block.Block;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Mod.EventBusSubscriber
public class TradeCaravanManager {

    private static final Map<World, List<TradeCaravanData>> activeCaravans = new WeakHashMap<>();
    private static final long BANDIT_CHECK_INTERVAL = 200L;
    private static final double BANDIT_SPAWN_CHANCE = 0.05;
    private static final String CARAVAN_SAVE_FILE = "caravans.dat";

    private static final Map<World, Long> lastBanditCheck = new WeakHashMap<>();

    public static void registerCaravan(World world, TradeCaravanData data) {
        activeCaravans.computeIfAbsent(world, w -> new ArrayList<>()).add(data);
    }

    public static void removeCaravan(World world, TradeCaravanData data) {
        if (activeCaravans.containsKey(world)) {
            activeCaravans.get(world).remove(data);
        }
    }

    public static List<TradeCaravanData> getActiveCaravans(World world) {
        return activeCaravans.getOrDefault(world, Collections.emptyList());
    }

    public static BlockPos findNearestTownHall(World world, BlockPos center, int radius) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos nearest = null;
        double closestSq = radius * radius;

        int minX = center.getX() - radius;
        int minY = Math.max(0, center.getY() - 16);
        int minZ = center.getZ() - radius;
        int maxX = center.getX() + radius;
        int maxY = Math.min(255, center.getY() + 16);
        int maxZ = center.getZ() + radius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    cursor.setPos(x, y, z);
                    Block block = world.getBlockState(cursor).getBlock();
                    if (block.getRegistryName() != null &&
                            block.getRegistryName().toString().toLowerCase().contains("townhall")) {
                        double distSq = center.distanceSq(cursor);
                        if (distSq < closestSq) {
                            closestSq = distSq;
                            nearest = cursor.toImmutable();
                        }
                    }
                }
            }
        }

        return nearest;
    }

    public static BlockPos findValidCaravanSpawn(World world, BlockPos townHall, Random rand) {
        double angle = rand.nextDouble() * 2 * Math.PI;
        int distance = 200 + rand.nextInt(100);
        int dx = (int) (Math.cos(angle) * distance);
        int dz = (int) (Math.sin(angle) * distance);
        return townHall.add(dx, 0, dz);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;

        List<TradeCaravanData> worldCaravans = getActiveCaravans(event.world);
        Iterator<TradeCaravanData> iterator = worldCaravans.iterator();

        while (iterator.hasNext()) {
            TradeCaravanData caravan = iterator.next();
            boolean shouldDespawn = caravan.updateAndCheckLifecycle(event.world);
            if (shouldDespawn) {
                caravan.cleanup(event.world);
                iterator.remove();
            } else {
                maybeSpawnBandits(event.world, caravan);
            }
        }
    }

    private static void maybeSpawnBandits(World world, TradeCaravanData caravan) {
        long last = lastBanditCheck.getOrDefault(world, 0L);
        long current = world.getTotalWorldTime();
        if (current - last < BANDIT_CHECK_INTERVAL) return;
        lastBanditCheck.put(world, current);

        if (world.rand.nextDouble() < BANDIT_SPAWN_CHANCE) {
            BlockPos midPath = caravan.getMidpoint();
            spawnBanditAmbush(world, midPath);
        }
    }

    public static void spawnBanditAmbush(World world, BlockPos pos) {
        System.out.println("[Caravan] Bandit ambush triggered at " + pos + " (not yet implemented).");
        for (int i = 0; i < 3 + world.rand.nextInt(2); i++) {
            // Placeholder for future bandit entity spawning
        }
    }

    public static void saveCaravans(World world) {
        File dir = world.getSaveHandler() != null ? world.getSaveHandler().getWorldDirectory() : null;
        if (dir == null) {
            System.err.println("[Caravan] World save handler is null. Cannot save caravan data.");
            return;
        }

        File file = new File(dir, CARAVAN_SAVE_FILE);
        NBTTagCompound root = new NBTTagCompound();
        List<TradeCaravanData> list = activeCaravans.get(world);
        if (list != null) {
            int i = 0;
            for (TradeCaravanData data : list) {
                root.setTag("caravan_" + i++, data.writeToNBT());
            }
        }
        try {
            CompressedStreamTools.write(root, file);
        } catch (IOException e) {
            System.err.println("[Caravan] Failed to save caravans: " + e);
        }
    }

    public static void loadCaravans(World world) {
        File dir = world.getSaveHandler() != null ? world.getSaveHandler().getWorldDirectory() : null;
        if (dir == null) {
            System.err.println("[Caravan] World save handler is null. Cannot load caravan data.");
            return;
        }

        File file = new File(dir, CARAVAN_SAVE_FILE);
        if (!file.exists()) return;

        try {
            NBTTagCompound root = CompressedStreamTools.read(file);
            List<TradeCaravanData> loaded = new ArrayList<>();
            for (String key : root.getKeySet()) {
                loaded.add(TradeCaravanData.readFromNBT(root.getCompoundTag(key)));
            }
            activeCaravans.put(world, loaded);
        } catch (IOException e) {
            System.err.println("[Caravan] Failed to load caravans: " + e);
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            loadCaravans(event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) {
        if (!event.getWorld().isRemote) {
            saveCaravans(event.getWorld());
        }
    }
}
