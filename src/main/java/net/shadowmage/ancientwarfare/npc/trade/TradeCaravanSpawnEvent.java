package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

public class TradeCaravanSpawnEvent extends Event {
    public final World world;
    public final String sourceFaction;
    public final String targetFaction;
    public final BlockPos sourceBase;
    public final BlockPos targetBase;

    public TradeCaravanSpawnEvent(World world, String sourceFaction, String targetFaction, BlockPos src, BlockPos dst) {
        this.world = world;
        this.sourceFaction = sourceFaction;
        this.targetFaction = targetFaction;
        this.sourceBase = src;
        this.targetBase = dst;
    }
}

