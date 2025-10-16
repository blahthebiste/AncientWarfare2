package net.shadowmage.ancientwarfare.npc.raid;

import java.util.UUID;

import net.minecraft.util.math.BlockPos;

/** Simple data holder for raid scheduling */
public class RaidData {
    private final long startTime;
    private final String factionName;
    private final BlockPos targetPos;
    private boolean spawned = false;
    private UUID ownerId;

    public RaidData(long startTime, String factionName, BlockPos targetPos) {
        this.startTime = startTime;
        this.factionName = factionName;
        this.targetPos = targetPos;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getFactionName() {
        return factionName;
    }

    public BlockPos getTargetPos() {
        return targetPos;
    }
    
    public boolean isSpawned() { return spawned; }
    public void setSpawned(boolean s) { this.spawned = s; }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID id) { this.ownerId = id; }
}

