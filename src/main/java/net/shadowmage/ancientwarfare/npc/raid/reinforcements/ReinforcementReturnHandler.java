package net.shadowmage.ancientwarfare.npc.raid.reinforcements;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.raid.ai.NpcAIReturnToSpawn;
import net.shadowmage.ancientwarfare.npc.raid.event.RaidEndedEvent;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcementReturnHandler {

    @SubscribeEvent
    public void onRaidEnded(RaidEndedEvent event) {
        World world = event.getWorld();

        for (Entity entity : world.loadedEntityList) {
            if (entity instanceof NpcBase) {
                NpcFaction npc = (NpcFaction)  entity;

                if (npc.getEntityData().hasKey("aw_spawn_origin")) {
                    // Prevent targeting more enemies
                    npc.setAttackTarget(null);
                    npc.setRevengeTarget(null);

                    npc.tasks.addTask(0, new NpcAIReturnToSpawn(npc));
                }
            }
        }
    }
}

