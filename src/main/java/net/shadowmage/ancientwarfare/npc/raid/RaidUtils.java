package net.shadowmage.ancientwarfare.npc.raid;

import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.npc.registry.FactionDefinition;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RaidUtils {

    private static final Random RAND = new Random();

    public static BlockPos findSpawnEdge(BlockPos target, World world) {
        int radius = 40 + RAND.nextInt(20);
        int dx = (RAND.nextBoolean() ? 1 : -1) * radius;
        int dz = (RAND.nextBoolean() ? 1 : -1) * radius;
        int y = world.getHeight(target.add(dx, 0, dz)).getY();
        return new BlockPos(target.getX() + dx, y, target.getZ() + dz);
    }

    public static void broadcastMessage(World world, String msg) {
        if (!world.isRemote) {
            world.playerEntities.forEach(p ->
                    p.sendMessage(new TextComponentString("[Raid] " + msg)));
        }
    }

    /** Try to locate a Town Hall tile near the given position */
    public static TileTownHall getTownHallAt(World world, BlockPos pos) {
        int searchRadius = 16;
        for (BlockPos checkPos : BlockPos.getAllInBoxMutable(
                pos.add(-searchRadius, -4, -searchRadius),
                pos.add(searchRadius, 4, searchRadius))) {
            TileEntity tile = world.getTileEntity(checkPos);
            if (tile instanceof TileTownHall) {
                return (TileTownHall) tile;
            }
        }
        return null;
    }

    public static String getRandomHostileFaction() {
        Set<String> factions = FactionRegistry.getFactionNames();
        if (factions.isEmpty()) {
            return "Bandits"; // fallback
        }

        List<String> hostileFactions = new ArrayList<>();

        for (String name : factions) {
            FactionDefinition def = FactionRegistry.getFaction(name);
            if (def == null || def.getStandingSettings() == null) continue;

            int standing = def.getStandingSettings().getPlayerDefaultStanding();
            boolean hostileToPlayer = def.isHostileTowards("player") || standing < 0;

            if (hostileToPlayer) {
                hostileFactions.add(name);
            }
        }

        if (hostileFactions.isEmpty()) {
            return "Bandits"; // fallback default if no hostile faction found
        }

        return hostileFactions.get(RAND.nextInt(hostileFactions.size()));
    }



    /** Wrapper: returns the BlockPos of the nearest Town Hall or null if none found */
    public static BlockPos getTownHallPositionNear(World world, BlockPos pos) {
        TileTownHall hall = getTownHallAt(world, pos);
        return hall != null ? hall.getPos() : null;
    }

    /** Wrapper: returns the actual TileTownHall at a given position or nearby */
    public static TileTownHall getTownHallTileAt(World world, BlockPos pos) {
        TileTownHall hall = getTownHallAt(world, pos);
        if (hall != null) {
            return hall;
        }

        // Fallback: try to find another nearby Town Hall
        int searchRadius = 24;
        for (BlockPos checkPos : BlockPos.getAllInBoxMutable(
                pos.add(-searchRadius, -4, -searchRadius),
                pos.add(searchRadius, 4, searchRadius))) {
            TileEntity tile = world.getTileEntity(checkPos);
            if (tile instanceof TileTownHall) {
                return (TileTownHall) tile;
            }
        }
        return null;
    }




}

