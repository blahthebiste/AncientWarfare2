package net.shadowmage.ancientwarfare.npc.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;
import net.shadowmage.ancientwarfare.npc.raid.event.RaidEndedEvent;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;

public class CommandReinforce extends CommandBase {

    @Override
    public String getName() {
        return "reinforce";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/reinforce";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) sender;
        World world = player.getEntityWorld();
        BlockPos origin = player.getPosition().add(0, 0, 4);

        // Spawn 3 soldiers
        for (int i = 0; i < 3; i++) {
            spawnTestNpc(world, origin, "soldier", AWEntityRegistry.NPC_FACTION_SOLDIER);
        }

        player.sendMessage(new TextComponentString("Spawned test reinforcements."));

        // Simulate end of raid after 15 seconds
        server.addScheduledTask(() -> {
            MinecraftForge.EVENT_BUS.post(new RaidEndedEvent(
                    world,
                    "test_faction",
                    origin,
                    player,
                    true
            ));
            player.sendMessage(new TextComponentString("Simulated raid end."));
        });
    }

    private void spawnTestNpc(World world, BlockPos center, String subtype, String entityId) {
        NpcBase npc = AWNPCEntities.createNpc(world, entityId, subtype, "test_faction");
        if (npc != null) {
            double dx = center.getX() + world.rand.nextInt(5) - 2;
            double dz = center.getZ() + world.rand.nextInt(5) - 2;
            npc.setPosition(dx + 0.5, center.getY() + 1, dz + 0.5);
            npc.getEntityData().setTag("aw_spawn_origin",
                    new NBTTagIntArray(new int[]{center.getX(), center.getY(), center.getZ()}));
            npc.setHomeAreaAtCurrentPosition();
            world.spawnEntity(npc);
        }
    }
}

