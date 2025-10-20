package net.shadowmage.ancientwarfare.npc.raid.reinforcements;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.registry.FactionDefinition;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

import java.util.*;

public class ReinforcementUtils {

    private static final Random RAND = new Random();

    public static List<String> getAllFriendlyFactions(EntityPlayer player) {
        Set<String> factions = FactionRegistry.getFactionNames();
        List<String> friendly = new ArrayList<>();

        for (String name : factions) {
            FactionDefinition def = FactionRegistry.getFaction(name);
            if (def == null || def.getStandingSettings() == null) continue;

            int standing = def.getStandingSettings().getPlayerDefaultStanding();
            boolean friendlyToPlayer = !def.isHostileTowards("player") && standing > 0;

            if (friendlyToPlayer) friendly.add(name);
        }

        return friendly;
    }

    public static boolean hasSufficientFunds(EntityPlayer player) {
        int goldCount = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.getItem() == Items.GOLD_INGOT) goldCount += stack.getCount();
        }
        return goldCount >= 10;
    }

    public static void deductGold(EntityPlayer player, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.inventory.mainInventory.size() && remaining > 0; i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.getItem() == Items.GOLD_INGOT) {
                int used = Math.min(remaining, stack.getCount());
                stack.shrink(used);
                remaining -= used;
            }
        }
    }

    public static BlockPos findMessengerDestination(World world, BlockPos origin) {
        int dist = 80 + RAND.nextInt(40);
        int dx = (RAND.nextBoolean() ? 1 : -1) * dist;
        int dz = (RAND.nextBoolean() ? 1 : -1) * dist;
        int y = world.getHeight(origin.add(dx, 0, dz)).getY();
        return new BlockPos(origin.getX() + dx, y, origin.getZ() + dz);
    }
}
