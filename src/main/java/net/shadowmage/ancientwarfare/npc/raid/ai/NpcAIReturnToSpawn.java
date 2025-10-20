package net.shadowmage.ancientwarfare.npc.raid.ai;

import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

public class NpcAIReturnToSpawn extends NpcAI<NpcFaction> {
    private final NpcBase npc;
    private BlockPos targetPos;
    private boolean done;

    public NpcAIReturnToSpawn(NpcFaction npc) {
        super(npc);
        this.npc = npc;
        this.setMutexBits(1); // standard movement bit
        this.done = false;
    }

    @Override
    public boolean shouldExecute() {
        if (done || npc.getAttackTarget() != null) return false;

        if (targetPos == null) {
            NBTTagIntArray tag = (NBTTagIntArray) npc.getEntityData().getTag("aw_spawn_origin");
            if (tag != null) {
                int[] coords = tag.getIntArray();
                targetPos = new BlockPos(coords[0], coords[1], coords[2]);
            }
        }

        return targetPos != null && npc.getDistanceSqToCenter(targetPos) > 4;
    }

    @Override
    public void startExecuting() {
        npc.getNavigator().tryMoveToXYZ(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 1.0);
    }

    @Override
    public void updateTask() {
        if (npc.getDistanceSqToCenter(targetPos) <= 4) {
            done = true;
            npc.setDead();  // or npc.setNoAI(true) to freeze it instead of despawn
        }
    }
}

