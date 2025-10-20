package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
import net.shadowmage.ancientwarfare.npc.ai.NpcAITradeDealRunner;
import net.shadowmage.ancientwarfare.npc.trade.TradeDeal;

import java.util.HashMap;

public class ItemTradeDealOrder extends ItemOrders {

    public ItemTradeDealOrder() {
        super("trade_deal_order");
        setMaxStackSize(1);
    }

    /** Player right-clicks a block (Warehouse Interface) */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,
                                      EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return EnumActionResult.SUCCESS;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileWarehouseInterface) {
            ItemStack stack = player.getHeldItem(hand);
            NBTTagCompound tag = stack.getOrCreateSubCompound("TradeDealData");
            tag.setInteger("wh_x", pos.getX());
            tag.setInteger("wh_y", pos.getY());
            tag.setInteger("wh_z", pos.getZ());

            player.sendMessage(new TextComponentString("§aWarehouse Interface selected for Trade Deals."));
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    /** Player right-clicks on a Trader NPC */
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, net.minecraft.entity.EntityLivingBase target, EnumHand hand) {
        if (player.world.isRemote) return false;
        if (!(target instanceof NpcTrader)) return false;

        NpcTrader trader = (NpcTrader) target;
        NBTTagCompound tag = stack.getSubCompound("TradeDealData");
        if (tag == null || !tag.hasKey("wh_x")) {
            player.sendMessage(new TextComponentString("§cNo Warehouse selected! Right-click a Warehouse Interface first."));
            return false;
        }

        BlockPos warehousePos = new BlockPos(tag.getInteger("wh_x"), tag.getInteger("wh_y"), tag.getInteger("wh_z"));
        TradeDeal deal = new TradeDeal(
                trader.getFactionName(),
                "player_faction",
                new HashMap<>(),
                new HashMap<>(),
                player.world.getTotalWorldTime(),
                240000
        );

        NpcAITradeDealRunner ai = trader.getTradeDealAI();
        ai.createAndAssignOrder(warehousePos, deal);

        player.sendMessage(new TextComponentString("§eTrade Deal assigned to " + trader.getName() + " using Warehouse at " + warehousePos.toShortString()));
        stack.shrink(1);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        NBTTagCompound tag = stack.getSubCompound("TradeDealData");
        if (tag != null && tag.hasKey("wh_x")) {
            int x = tag.getInteger("wh_x");
            int y = tag.getInteger("wh_y");
            int z = tag.getInteger("wh_z");
            tooltip.add("§7Linked Warehouse: §a" + x + ", " + y + ", " + z);
        } else {
            tooltip.add("§cNo Warehouse Linked");
        }
        tooltip.add("§8Right-click a Warehouse Interface to link");
        tooltip.add("§8Then place in Trader’s Work Order slot");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_TRADE_DEAL_ORDER, 0, 0, 0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }


}
