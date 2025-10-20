package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.npc.trade.TradeCaravanSpawnEvent;

public class TradeCaravanEventHandler {

    @SubscribeEvent
    public void onTradeCaravanSpawn(TradeCaravanSpawnEvent evt) {
        TradeCaravanData caravan = new TradeCaravanData(evt.sourceFaction, evt.sourceBase, evt.targetBase);
        caravan.spawnCaravan(evt.world);
        TradeDealManager.get(evt.world).registerActiveCaravan(evt.world, evt.sourceFaction, evt.targetFaction, caravan);
    }
}

