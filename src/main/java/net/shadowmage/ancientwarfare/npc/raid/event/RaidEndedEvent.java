package net.shadowmage.ancientwarfare.npc.raid.event;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a raid ends — for example when all raiders are dead,
 * have retreated, or the player successfully defends the town.
 *
 * Systems such as alarms, rewards, or faction standing adjustments
 * can subscribe to this event.
 */
public class RaidEndedEvent extends Event {

    private final World world;
    private final String factionName;
    private final BlockPos targetTownHall;
    private final EntityPlayer defendingPlayer;
    private final boolean playerWon;

    /**
     * @param world           World where the raid occurred.
     * @param factionName     Name of the attacking faction.
     * @param targetTownHall  Targeted Town Hall position.
     * @param defendingPlayer Player who was attacked (may be null for NPC towns).
     * @param playerWon       True if defenders survived, false if raiders succeeded.
     */
    public RaidEndedEvent(World world, String factionName, BlockPos targetTownHall,
                          EntityPlayer defendingPlayer, boolean playerWon) {
        this.world = world;
        this.factionName = factionName;
        this.targetTownHall = targetTownHall;
        this.defendingPlayer = defendingPlayer;
        this.playerWon = playerWon;
    }

    public World getWorld() {
        return world;
    }

    public String getFactionName() {
        return factionName;
    }

    public BlockPos getTargetTownHall() {
        return targetTownHall;
    }

    public EntityPlayer getDefendingPlayer() {
        return defendingPlayer;
    }

    public boolean didPlayerWin() {
        return playerWon;
    }
}

