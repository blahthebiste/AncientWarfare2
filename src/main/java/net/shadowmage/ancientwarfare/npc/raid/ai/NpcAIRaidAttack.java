package net.shadowmage.ancientwarfare.npc.raid.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

/**
 * Raid attack AI: march toward the Town Hall (raid target) and keep pressure on it.
 * Uses the NpcAI base which provides robust pathing helpers & retry logic.
 */
public class NpcAIRaidAttack extends NpcAI<NpcFaction> {

    private final BlockPos targetPos;   // Town Hall (or raid center)
    private final double rushSpeed;     // desired pathing speed while raiding

    // tunables
    private static final double CLOSE_ENOUGH_SQ = 6.0D;  // ~2.45 blocks
    private static final int REPATH_TICKS = 20;          // coarse repath interval when close
    private static final int MAX_TIMEOUT = 20 * 60;      // fail-safe: 60s

    private int ticks;
    private int repathTicker;

    public NpcAIRaidAttack(NpcFaction npc, BlockPos targetPos, double speed) {
        super(npc);
        this.targetPos = targetPos;
        this.rushSpeed = speed;
        this.moveSpeed = speed; // inherited from NpcAI
        // Prevent other MOVE/ATTACK tasks from colliding while this runs
        this.setMutexBits(MOVE | ATTACK);
    }

    @Override
    public boolean shouldExecute() {
        if (!super.shouldExecute()) return false;
        if (npc.isDead || npc.isAIDisabled() || targetPos == null) return false;
        return true;
    }


    @Override
    public void startExecuting() {
        ticks = 0;
        repathTicker = 0;
        // initial push toward the Town Hall
        forceMoveToPosition(targetPos, npc.getDistanceSqToCenter(targetPos));
    }

    @Override
    public void resetTask() {
        // Let other AI take over cleanly
        npc.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        ticks++;
        repathTicker++;

        // If we already have a valid live combat target (from targetTasks), bias toward it
        EntityLivingBase attackTarget = npc.getAttackTarget();
        if (attackTarget != null && attackTarget.isEntityAlive()) {
            double d2 = npc.getDistanceSq(attackTarget);
            // close in using NpcAI helpers (handles long/short hops + retries)
            moveToEntity(attackTarget, d2);
            // small look control so ranged units orient properly
            npc.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);
            return;
        }

        // Otherwise, keep marching on the Town Hall center
        double distSq = npc.getDistanceSqToCenter(targetPos);

        if (distSq > CLOSE_ENOUGH_SQ) {
            // NpcAI handles retry windows & long-distance partitioning internally
            moveToPosition(targetPos, distSq);
        } else {
            // We’re close — gently re-path every so often to avoid idle stalls
            if (repathTicker >= REPATH_TICKS) {
                repathTicker = 0;
                // Nudge slightly around the point to keep flow going
                Vec3d nudge = new Vec3d(
                        (npc.world.rand.nextDouble() - 0.5D) * 2.0D,
                        0,
                        (npc.world.rand.nextDouble() - 0.5D) * 2.0D
                );
                BlockPos around = targetPos.add(nudge.x * 2.0D, 0, nudge.z * 2.0D);
                moveToPosition(around, npc.getDistanceSqToCenter(around));
            }
        }
    }
}
