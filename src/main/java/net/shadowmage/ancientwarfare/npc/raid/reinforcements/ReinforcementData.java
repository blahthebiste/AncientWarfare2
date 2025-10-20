package net.shadowmage.ancientwarfare.npc.raid.reinforcements;

import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class ReinforcementData {
    private final UUID playerId;
    private final String allyFaction;
    private final BlockPos townHallPos;
    private final long resolveTime;

    public ReinforcementData(UUID playerId, String allyFaction, BlockPos townHallPos, long resolveTime) {
        this.playerId = playerId;
        this.allyFaction = allyFaction;
        this.townHallPos = townHallPos;
        this.resolveTime = resolveTime;
    }

    public UUID getPlayerId() { return playerId; }
    public String getAllyFaction() { return allyFaction; }
    public BlockPos getTownHallPos() { return townHallPos; }
    public long getResolveTime() { return resolveTime; }
}
