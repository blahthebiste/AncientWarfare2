package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.item.ItemTradeDealOrder;

public class ContainerTradeDealOrder extends ContainerBase {

    private final ItemStackHandler offeredItems = new ItemStackHandler(9);
    private final ItemStackHandler requestedItems = new ItemStackHandler(9);

    private final ItemStack tradeDealItem;

    /**
     * Required by NetworkHandler (called via reflection)
     */
    public ContainerTradeDealOrder(EntityPlayer player, int x, int y, int z) {
        super(player);
        this.tradeDealItem = player.getHeldItemMainhand();

        int startX = 8;
        int startY = 20;

        // Offered goods (top row)
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new SlotItemHandler(offeredItems, i, startX + i * 18, startY));
        }

        // Requested goods (bottom row)
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new SlotItemHandler(requestedItems, i, startX + i * 18, startY + 40));
        }

        // Player inventory slots
        addPlayerSlots(84);
    }

    @Override
    public void sendInitData() {
        if (player.world.isRemote) return;
        NBTTagCompound tag = tradeDealItem.getSubCompound("TradeDealData");
        if (tag != null) {
            offeredItems.deserializeNBT(tag.getCompoundTag("offered"));
            requestedItems.deserializeNBT(tag.getCompoundTag("requested"));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!player.world.isRemote && tradeDealItem.getItem() instanceof ItemTradeDealOrder) {
            NBTTagCompound tag = tradeDealItem.getOrCreateSubCompound("TradeDealData");
            tag.setTag("offered", offeredItems.serializeNBT());
            tag.setTag("requested", requestedItems.serializeNBT());
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !tradeDealItem.isEmpty() && tradeDealItem.getItem() instanceof ItemTradeDealOrder;
    }

    public IItemHandler getOfferedInventory() {
        return offeredItems;
    }

    public IItemHandler getRequestedInventory() {
        return requestedItems;
    }
}
