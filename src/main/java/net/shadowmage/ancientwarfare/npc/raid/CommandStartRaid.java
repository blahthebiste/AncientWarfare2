package net.shadowmage.ancientwarfare.npc.raid;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandStartRaid extends CommandBase {

    @Override
    public String getName() {
        return "startraid";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/startraid [small|medium|large] [now]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender.getCommandSenderEntity() instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString("Only players can start raids."));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        World world = player.world;

        RaidSize raidSize = RaidSize.SMALL;
        boolean startNow = false;

        try {
            // Parse arguments
            for (String arg : args) {
                String a = arg.toLowerCase();
                if ("small".equals(a)) raidSize = RaidSize.SMALL;
                else if ("medium".equals(a)) raidSize = RaidSize.MEDIUM;
                else if ("large".equals(a)) raidSize = RaidSize.LARGE;
                else if ("now".equals(a)) startNow = true;
            }

            // Execute raid
            if (startNow) {
                String faction = RaidUtils.getRandomHostileFaction();
                RaidManager.startRaidNow(world, player.getPosition(), faction, raidSize);
                sender.sendMessage(new TextComponentString("§aRaid started immediately! Type: §e" + raidSize.name()));
            } else {
                RaidManager.scheduleInitialRaid(world, player, raidSize);
                sender.sendMessage(new TextComponentString("§aRaid scheduled! Type: §e" + raidSize.name()));
            }

        } catch (Exception e) {
            // Log and notify in-game
            sender.sendMessage(new TextComponentString("§cRaid command failed: " + e.getClass().getSimpleName() + " - " + e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // Admin-only by default
    }
}
