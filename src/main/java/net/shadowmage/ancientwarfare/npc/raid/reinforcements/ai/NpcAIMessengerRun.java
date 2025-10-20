package net.shadowmage.ancientwarfare.npc.raid.reinforcements.ai;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.raid.reinforcements.ReinforcementData;
import net.shadowmage.ancientwarfare.npc.raid.reinforcements.ReinforcementManager;

public class NpcAIMessengerRun extends NpcAI<NpcFaction> {

    private final World world;
    private final BlockPos townHallPos;
    private final BlockPos destination;
    private boolean reached = false;
    private int ticksSinceStart = 0;

    public NpcAIMessengerRun(NpcFaction messenger, BlockPos townHallPos, BlockPos destination) {
        super(messenger);
        this.world = messenger.world;
        this.townHallPos = townHallPos;
        this.destination = destination;
        this.setMutexBits(1);
        this.moveSpeed = 1.25D;
    }

    @Override
    public boolean shouldExecute() {
        return npc != null && npc.getOwner() != null && destination != null;
    }

    @Override
    public void startExecuting() {
        AncientWarfareCore.LOG.info("Messenger departing from Town Hall at " + townHallPos);
        forceMoveToPosition(destination, 9999);
    }

    @Override
    public void updateTask() {
        ticksSinceStart++;

        double distSq = npc.getDistanceSqToCenter(destination);

        if (distSq < 4.0D && !reached) {
            reached = true;
            AncientWarfareCore.LOG.info("Messenger reached destination.");

            ReinforcementData data = ReinforcementManager.getRequestByPlayer(npc.getOwner().getUUID());
            if (data != null) {
                if (world.rand.nextFloat() < 0.6f) {
                    ReinforcementManager.resolveRequest(world, data);
                } else {
                    ReinforcementManager.failRequest(world, data);
                }
            }

            npc.setDead(); // Optionally replace with return pathing
        }

        if (ticksSinceStart >= 1200 && !reached) {
            AncientWarfareCore.LOG.info("Messenger timed out.");
            ReinforcementData data = ReinforcementManager.getRequestByPlayer(npc.getOwner().getUUID());
            if (data != null) {
                ReinforcementManager.failRequest(world, data);
            }
            npc.setDead();
        }
    }
}
