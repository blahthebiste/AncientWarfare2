// TradeCaravanEventHandler.java
package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

@Mod.EventBusSubscriber
public class TradeCaravanEventHandler {

    private static final int CHECK_INTERVAL = 12000; // ~10 minutes in ticks
    private static long lastCheckTime = 0;
    private static final double SPAWN_CHANCE = 0.01; // 1% chance

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;
        World world = event.world;
        long time = world.getTotalWorldTime();

        if (time - lastCheckTime >= CHECK_INTERVAL) {
            lastCheckTime = time;
            if (world.playerEntities.isEmpty()) return;

            EntityPlayerMP targetPlayer = (EntityPlayerMP) world.playerEntities.get(world.rand.nextInt(world.playerEntities.size()));
            BlockPos townHall = TradeCaravanManager.findNearestTownHall(world, targetPlayer.getPosition(), 300);

            if (townHall != null && world.rand.nextDouble() < SPAWN_CHANCE) {
                BlockPos spawnPos = TradeCaravanManager.findValidCaravanSpawn(world, townHall, world.rand);
                TradeCaravanData caravan = new TradeCaravanData("random_faction", spawnPos, townHall);
                caravan.spawnCaravan(world);
                TradeCaravanManager.registerCaravan(world, caravan);

                // Notify all players nearby
                for (EntityPlayerMP player : world.getMinecraftServer().getPlayerList().getPlayers()) {
                    if (player.getDistanceSq(townHall) < 256 * 256) {
                        player.sendMessage(new TextComponentString(
                                TextFormatting.GOLD + "[Caravan] A trade caravan is arriving at your town!"
                        ));
                    }
                }
            }
        }
    }
}
