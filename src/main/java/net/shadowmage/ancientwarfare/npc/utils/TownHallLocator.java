package net.shadowmage.ancientwarfare.npc.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.shadowmage.ancientwarfare.npc.init.AWNPCBlocks;
import net.minecraft.util.math.ChunkPos;

public class TownHallLocator {

    // Scans within a cubic radius around center for a valid Town Hall block
    public static BlockPos findNearestTownHall(World world, BlockPos center, int radius) {
        Block targetBlock = AWNPCBlocks.townHall; // Replace with your actual block ref

        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -10; dy <= 10; dy++) { // Only scan around typical ground level
                    BlockPos pos = new BlockPos(cx + dx, cy + dy, cz + dz);
                    if (world.getBlockState(pos).getBlock() == targetBlock) {
                        return pos;
                    }
                }
            }
        }

        return null;
    }
}

