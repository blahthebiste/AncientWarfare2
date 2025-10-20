package net.shadowmage.ancientwarfare.npc.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.shadowmage.ancientwarfare.npc.trade.TradeCaravanData;
import net.shadowmage.ancientwarfare.npc.trade.TradeCaravanManager;
import net.shadowmage.ancientwarfare.npc.util.TownHallLocator;

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
        BlockPos townHallPos = TownHallLocator.findNearestTownHall(world, player.getPosition(), 256);

        if (townHallPos == null) {
            player.sendMessage(new TextComponentString("§cNo nearby Town Hall found for faction §4" + faction));
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
        TradeCaravanManager.registerCaravan(world, caravan);

        player.sendMessage(new TextComponentString("§eTrade caravan from §a" + faction + "§e spawned ~" + distance + " blocks from the town hall."));

        // Schedule cleanup after ~2 in-game days (48000 ticks)
        server.addScheduledTask(() -> {
            server.getCommandManager().executeCommand(server, String.format("schedule function ancientwarfare:cleanup_caravan_%s %d", faction, 48000));
        });
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // Admin level
    }
}
