package net.shadowmage.ancientwarfare.npc.raid.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a raid officially begins.
 * Other systems (alarm, sounds, GUIs, etc.) can subscribe to this event.
 */
public class RaidStartedEvent extends Event {

    private final World world;
    private final String factionName;
    private final BlockPos targetTownHall;
    private final EntityPlayer targetPlayer;

    public RaidStartedEvent(World world, String factionName, BlockPos targetTownHall, EntityPlayer targetPlayer) {
        this.world = world;
        this.factionName = factionName;
        this.targetTownHall = targetTownHall;
        this.targetPlayer = targetPlayer;
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

    public EntityPlayer getTargetPlayer() {
        return targetPlayer;
    }
}

