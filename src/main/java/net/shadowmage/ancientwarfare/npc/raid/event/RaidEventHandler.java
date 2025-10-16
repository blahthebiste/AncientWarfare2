package net.shadowmage.ancientwarfare.npc.raid.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.text.TextComponentString;

public class RaidEventHandler {

    @SubscribeEvent
    public void onRaidStart(RaidStartedEvent event) {
        event.getWorld().playerEntities.forEach(p ->
                p.sendMessage(new TextComponentString("[Raid] "
                        + event.getFactionName() + " have begun their attack!")));
    }

    @SubscribeEvent
    public void onRaidEnd(RaidEndedEvent event) {
        event.getWorld().playerEntities.forEach(p ->
                p.sendMessage(new TextComponentString("[Raid] The attack by "
                        + event.getFactionName() + " has ended.")));
    }
}

