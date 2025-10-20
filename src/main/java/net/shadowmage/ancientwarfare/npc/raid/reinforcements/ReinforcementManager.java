package net.shadowmage.ancientwarfare.npc.raid.reinforcements;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;
import net.shadowmage.ancientwarfare.npc.raid.reinforcements.ai.NpcAIMessengerRun;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import java.util.*;

public class ReinforcementManager {

    private static final Map<UUID, ReinforcementData> ACTIVE_REQUESTS = new HashMap<>();

    public static void requestReinforcements(World world, EntityPlayer player, TileTownHall townHall, String allyFaction) {
        if (!ReinforcementUtils.hasSufficientFunds(player)) {
            player.sendMessage(new TextComponentString("§cYou do not have enough gold for reinforcements (10 required)."));
            return;
        }

        // Deduct cost
        ReinforcementUtils.deductGold(player, 10);
        player.sendMessage(new TextComponentString("§eYour messenger is departing to contact " + allyFaction + " for reinforcements."));

        // Spawn messenger
        BlockPos spawn = townHall.getPos().add(2, 0, 2);
        BlockPos destination = ReinforcementUtils.findMessengerDestination(world, townHall.getPos());
        NpcFaction messenger = (NpcFaction) AWNPCEntities.createNpc(world, "utility", "messenger", allyFaction); // You can define this type

        if (messenger != null) {
            messenger.setFactionNameAndDefaults(allyFaction);
            messenger.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
            world.spawnEntity(messenger);
            messenger.tasks.addTask(0, new NpcAIMessengerRun(messenger, townHall.getPos(), destination));

            ReinforcementData data = new ReinforcementData(player.getUniqueID(), allyFaction, townHall.getPos(), world.getWorldTime() + 400);
            ACTIVE_REQUESTS.put(player.getUniqueID(), data);
        } else {
            player.sendMessage(new TextComponentString("§cFailed to create messenger entity."));
        }
    }

    public static ReinforcementData getRequestByPlayer(UUID playerId) {
        return ACTIVE_REQUESTS.get(playerId);
    }

    public static void resolveRequest(World world, ReinforcementData data) {
        String faction = data.getAllyFaction();
        EntityPlayer player = world.getPlayerEntityByUUID(data.getPlayerId());
        AncientWarfareCore.LOG.info("Reinforcements from " + faction + " have arrived!");

        for (int i = 0; i < 5; i++) {
            BlockPos spawn = data.getTownHallPos().add(16 + world.rand.nextInt(8), 0, 16 + world.rand.nextInt(8));
            NpcFaction ally = (NpcFaction) AWNPCEntities.createNpc(world, "combat", "soldier", faction);
            if (ally != null) {
                ally.setFactionNameAndDefaults(faction);
                ally.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
                world.spawnEntity(ally);
            }
        }

        if (player != null)
            player.sendMessage(new TextComponentString("§aReinforcements from " + faction + " have arrived to defend your town!"));

        ACTIVE_REQUESTS.remove(data.getPlayerId());
    }

    public static void failRequest(World world, ReinforcementData data) {
        EntityPlayer player = world.getPlayerEntityByUUID(data.getPlayerId());
        if (player != null)
            player.sendMessage(new TextComponentString("§cYour messenger never returned. Reinforcements did not arrive."));
        ACTIVE_REQUESTS.remove(data.getPlayerId());
    }
}
