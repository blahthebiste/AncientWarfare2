package net.shadowmage.ancientwarfare.npc.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.npc.registry.StandingChanges;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandFaction extends CommandBase {

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getName() {
		return "awfaction";
	}

	// Version that can show individual usages for each subcommand
	public String getUsage(String subcommand) {
		if (subcommand.equals("set") ||
				subcommand.equals("get") ||
				subcommand.equals("setall") ||
				subcommand.equals("add") ||
				subcommand.equals("subtract")) {
			return "command.aw.faction." + subcommand+".usage";
		}
		else {
			return "command.aw.faction.usage";
		}
	}

	@Override
	public String getUsage(ICommandSender var1) {
			return "command.aw.faction.usage";
	}


	@Override
	public void execute(MinecraftServer server, ICommandSender var1, String[] var2) throws CommandException {
		if (var2.length < 1) {
			throw new WrongUsageException(getUsage(var1));
		}
		World world = var1.getEntityWorld();
		String cmd = var2[0];
		if (var2.length == 1) {
			throw new WrongUsageException(getUsage(cmd));
		}
		String playerName = var2[1];
		String factionName;
		int adjustment;
		if (cmd.equalsIgnoreCase("get")) {
			showFactionStandings(var1, playerName);
			return;
		}
		// setall requires an additional arg, the adjustment amount
		if (cmd.equalsIgnoreCase("setall")) {
			if (var2.length < 3) {
				throw new WrongUsageException(getUsage(cmd));
			}
			try {
				adjustment = Integer.parseInt(var2[2]);
			}
			catch (NumberFormatException e) {
				throw new WrongUsageException(getUsage(cmd));
			}
		}
		// All other commands require 2 additional args, the faction and adjustment amount
		if (var2.length < 4) {
			throw new WrongUsageException(getUsage(cmd));
		}
		factionName = var2[2];
		try {
			adjustment = Integer.parseInt(var2[3]);
		}
		catch (NumberFormatException e) {
			throw new WrongUsageException(getUsage(cmd));
		}
		var1.sendMessage(new TextComponentTranslation("command.aw.faction.add", factionName, playerName));
		if (cmd.equalsIgnoreCase("add")) {
			System.out.println("Adding "+adjustment+" standing to "+factionName+" for "+playerName);
			FactionTracker.INSTANCE.adjustStandingFor(world, playerName, factionName, adjustment);
			return;
		}
		else if (cmd.equalsIgnoreCase("subtract")) {
			FactionTracker.INSTANCE.adjustStandingFor(world, playerName, factionName, -adjustment);
			return;
		}
		else if (cmd.equalsIgnoreCase("set")) {
			FactionTracker.INSTANCE.setStandingFor(world, playerName, factionName, adjustment);
			return;
		}
		else {
			throw new WrongUsageException(getUsage(cmd));
		}
	}

	private void showFactionStandings(ICommandSender var1, String playerName) {
		World world = var1.getEntityWorld();
		var1.sendMessage(new TextComponentTranslation("command.aw.faction.status.player", playerName));
		for (String faction : FactionRegistry.getFactionNames()) {
			int standing = FactionTracker.INSTANCE.getStandingFor(world, playerName, faction);
			var1.sendMessage(new TextComponentTranslation("command.aw.faction.status.value", faction, standing));
		}
	}



	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "get");
		} else if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 1;
	}

}
