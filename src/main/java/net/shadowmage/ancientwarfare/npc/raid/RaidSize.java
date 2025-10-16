package net.shadowmage.ancientwarfare.npc.raid;

import java.util.Random;

public enum RaidSize {
    SMALL(6, 8, 0),
    MEDIUM(9, 14, 0),
    LARGE(15, 21, 2); // up to 2 elites

    private final int min;
    private final int max;
    private final int leader;

    RaidSize(int min, int max, int leader) {
        this.min = min;
        this.max = max;
        this.leader = leader;
    }

    public int getRandomSize(Random rand) {
        return rand.nextInt((max - min) + 1) + min;
    }

    public int getLeaderCount(Random rand) {
        if (this == LARGE) return 1 + rand.nextInt(2);
        return leader;
    }
}
