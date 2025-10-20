package net.shadowmage.ancientwarfare.npc.command;


import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.shadowmage.ancientwarfare.npc.trade.TradeCaravanData;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.trade.FactionStorageManager;

import java.util.Random;

public class CommandSpawnTradeCaravan extends CommandBase {

    @Override
    public String getName() {
        return "spawncaravan";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/spawncaravan <factionName>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) sender;
        World world = player.getEntityWorld();

        if (args.length < 1) {
            player.sendMessage(new TextComponentString("§cUsage: /spawncaravan <factionName>"));
            return;
        }

        String faction = args[0];
        BlockPos townHallPos = FactionStorageManager.getFactionBase(world, faction);
        if (townHallPos == null) {
            player.sendMessage(new TextComponentString("§cNo registered Town Hall found for faction " + faction));
            return;
        }

        Random rand = new Random();
        int distance = 100 + rand.nextInt(100);
        double angle = rand.nextDouble() * 2 * Math.PI;
        int offsetX = (int) (Math.cos(angle) * distance);
        int offsetZ = (int) (Math.sin(angle) * distance);
        BlockPos spawnPos = townHallPos.add(offsetX, 0, offsetZ);

        // Spawn the caravan
        TradeCaravanData caravan = new TradeCaravanData(faction, spawnPos, townHallPos);
        caravan.spawnCaravan(world);
        player.sendMessage(new TextComponentString("§eTrade caravan from §a" + faction + " §espawned " + distance + " blocks away."));

        // Schedule stay + departure (1 in-game day = 24000 ticks)
        world.scheduleUpdate(spawnPos, world.getBlockState(spawnPos).getBlock(), 24000);
        server.addScheduledTask(() -> {
            caravan.cleanup(world);
            player.sendMessage(new TextComponentString("§eThe caravan from §a" + faction + " §ehas departed."));
        });
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // admin-level command
    }
}

