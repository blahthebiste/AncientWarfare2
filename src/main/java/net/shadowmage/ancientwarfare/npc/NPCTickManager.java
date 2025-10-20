package net.shadowmage.ancientwarfare.npc;


import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.shadowmage.ancientwarfare.npc.trade.TradeCaravanManager;
// future: import net.shadowmage.ancientwarfare.npc.raid.RaidManager;

public class NPCTickManager {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;
        World world = event.world;

        TradeCaravanManager.tick(world);
        // RaidManager.tick(world); // ← future hook for raid events
        // PatrolManager.tick(world); // ← for scouting/defense events
    }
}

