package net.shadowmage.ancientwarfare.npc.raid;

import net.minecraft.util.math.BlockPos;
import java.util.UUID;

/**
 * Simple data holder for raid scheduling and tracking.
 * Now includes RaidSize for configurable raid strength.
 */
public class RaidData {

    private final long startTime;
    private final String factionName;
    private final BlockPos targetPos;
    private final RaidSize raidSize;   // ← Added this properly

    private boolean spawned = false;
    private UUID ownerId;

    public RaidData(long startTime, String factionName, BlockPos targetPos, RaidSize raidSize) {
        this.startTime = startTime;
        this.factionName = factionName;
        this.targetPos = targetPos;
        this.raidSize = raidSize;
    }

    /** Optional constructor for backward compatibility (defaults to SMALL) */
    public RaidData(long startTime, String factionName, BlockPos targetPos) {
        this(startTime, factionName, targetPos, RaidSize.SMALL);
    }

    // --- Getters ---
    public long getStartTime() { return startTime; }

    public String getFactionName() { return factionName; }

    public BlockPos getTargetPos() { return targetPos; }

    public RaidSize getRaidSize() { return raidSize; }

    public boolean isSpawned() { return spawned; }

    public void setSpawned(boolean s) { this.spawned = s; }

    public UUID getOwnerId() { return ownerId; }

    public void setOwnerId(UUID id) { this.ownerId = id; }

    @Override
    public String toString() {
        return "RaidData{" +
                "startTime=" + startTime +
                ", factionName='" + factionName + '\'' +
                ", targetPos=" + targetPos +
                ", raidSize=" + raidSize +
                ", ownerId=" + ownerId +
                ", spawned=" + spawned +
                '}';
    }
}
