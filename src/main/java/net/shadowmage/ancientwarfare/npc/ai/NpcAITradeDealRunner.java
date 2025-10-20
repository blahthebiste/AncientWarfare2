package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.orders.TradeDealOrder;

/**
 * NpcAITradeDealRunner
 *
 * AI task that allows trader NPCs to automatically execute TradeDealOrders.
 * The NPC will:
 *  1. Fetch goods from the town hall
 *  2. Leave the town for trade (simulate journey)
 *  3. Return with traded goods
 *  4. Complete the trade
 */
public class NpcAITradeDealRunner extends EntityAIBase {

    private final NpcFaction npc;
    private final World world;
    private TradeDealOrder currentOrder;
    private boolean executingTrade = false;

    public NpcAITradeDealRunner(NpcFaction npc) {
        this.npc = npc;
        this.world = npc.world;
        this.setMutexBits(1);
    }

    /** Assigns a trade order to this NPC (usually from TradeDealManager). */
    public void assignOrder(TradeDealOrder order) {
        this.currentOrder = order;
        this.executingTrade = true;
    }

    @Override
    public boolean shouldExecute() {
        return currentOrder != null && executingTrade && npc.getIsAIEnabled();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return executingTrade && currentOrder != null;
    }

    @Override
    public void startExecuting() {
        npc.say("Starting trade deal route...");
    }

    @Override
    public void resetTask() {
        executingTrade = false;
        currentOrder = null;
        npc.say("Trade deal route cleared.");
    }

    @Override
    public void updateTask() {
        if (currentOrder != null) {
            currentOrder.tick(world, npc);
        }
    }

    /** Utility: creates a TradeDealOrder at runtime for testing or scripting. */
    public void createAndAssignOrder(BlockPos townHall, net.shadowmage.ancientwarfare.npc.trade.TradeDeal deal) {
        TradeDealOrder order = new TradeDealOrder(deal, townHall);
        assignOrder(order);
    }
}
