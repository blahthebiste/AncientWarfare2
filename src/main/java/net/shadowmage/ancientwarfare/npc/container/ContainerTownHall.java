package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.faction.PlayerFactionEntry;
import net.shadowmage.ancientwarfare.npc.gamedata.FactionData;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ContainerTownHall extends ContainerTileBase<TileTownHall> {

	List<NpcDeathEntry> deathList = new ArrayList<>();
    private List<String> friendlyFactions = new ArrayList<>();


    public ContainerTownHall(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);
		int xPos, yPos;
		IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		for (int i = 0; i < handler.getSlots(); i++) {
			xPos = (i % 9) * 18 + 8;
			yPos = (i / 9) * 18 + 8 + 16;
			addSlotToContainer(new SlotItemHandler(handler, i, xPos, yPos));
		}
		addPlayerSlots(8 + 3 * 18 + 8 + 16);
		if (!player.world.isRemote) {
			deathList.addAll(tileEntity.getDeathList());
			tileEntity.addViewer(this);
		}
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("deathList")) {
			deathList.clear();
			NBTTagList list = tag.getTagList("deathList", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				deathList.add(new NpcDeathEntry(list.getCompoundTagAt(i)));
			}
			refreshGui();
		} else if (tag.hasKey("clear")) {
			tileEntity.clearDeathNotices();
		}

		if (tag.hasKey("range")) {
			tileEntity.setRange(tag.getInteger("range"));
			refreshGui();
		}

		if (tag.hasKey("name")) {
			tileEntity.name = tag.getString("name");
			refreshGui();
		}

		if (!tileEntity.getWorld().isRemote) {
			tileEntity.markDirty();
		}
        // === Client: receive friendly factions list ===
        if (tag.hasKey("friendlyFactions") && player.world.isRemote) {
            friendlyFactions.clear();
            NBTTagList list = tag.getTagList("friendlyFactions", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.tagCount(); i++) {
                friendlyFactions.add(list.getStringTagAt(i));
            }
            refreshGui();
        }

        // === Server: handle request for faction list ===
        if (tag.hasKey("requestFactions") && !player.world.isRemote) {
            FactionData fData = AWGameData.INSTANCE.getData(player.world, FactionData.class);
            PlayerFactionEntry entry = fData.getEntryFor(player.getName());

            List<String> friendly = new ArrayList<>();
            for (String factionName : FactionRegistry.getFactionNames()) {
                if (entry.getStandingFor(factionName) > 0) {
                    friendly.add(factionName);
                }
            }

            NBTTagCompound response = new NBTTagCompound();
            NBTTagList tagList = new NBTTagList();
            for (String name : friendly) {
                tagList.appendTag(new NBTTagString(name));
            }
            response.setTag("friendlyFactions", tagList);
            sendDataToClient(response);
        }

        // === Server: handle reinforcement request ===
        if (tag.hasKey("reinforce") && !player.world.isRemote) {
            String faction = tag.getString("reinforce");
            String size = tag.getString("size");  // small, medium, large

            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            TileTownHall tile = (TileTownHall) this.tileEntity;
            World world = tile.getWorld();
            BlockPos townPos = tile.getPos();

            FactionData fData = AWGameData.INSTANCE.getData(world, FactionData.class);
            PlayerFactionEntry entry = fData.getEntryFor(player.getName());
            int standing = entry.getStandingFor(faction);
            if (standing <= 0) return;

            int goldCost = calculateGoldCost(size, standing);
            if (!consumeGold(playerMP, goldCost)) {
                playerMP.sendMessage(new TextComponentTranslation("guistrings.npc.reinf_not_enough_gold"));
                return;
            }

            spawnReinforcements(world, townPos, playerMP, faction, size);
        }
	}

	@Override
	public void sendInitData() {
		sendTownHallDataToClient(false);
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		tileEntity.removeViewer(this);
	}

	public void onTownHallDeathListUpdated() {
		this.deathList.clear();
		this.deathList.addAll(tileEntity.getDeathList());
		sendTownHallDataToClient(true);
	}

	public void setRange(int value) {
		tileEntity.setRange(value);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("range", value);
		sendDataToServer(tag);
	}

	public void setName(String name) {
		tileEntity.name = name;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		sendDataToServer(tag);
	}

	public void teleportPlayer(String playerName) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("playerName", playerName);
		sendDataToServer(tag);
	}

	public void clearList() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("clear", true);
		sendDataToServer(tag);
	}

	private void sendTownHallDataToClient(boolean onlyDeathList) {
		NBTTagList list = new NBTTagList();
		for (NpcDeathEntry entry : deathList) {
			list.appendTag(entry.writeToNBT(new NBTTagCompound()));
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("deathList", list);
		if (!onlyDeathList) {
			tag.setInteger("range", tileEntity.getRange());
			tag.setString("name", tileEntity.name);
		}
		sendDataToClient(tag);
	}

	public List<NpcDeathEntry> getDeathList() {
		return deathList;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot theSlot = this.getSlot(slotClickedIndex);
		if (theSlot.getHasStack()) {
			ItemStack slotStack = theSlot.getStack();
			slotStackCopy = slotStack.copy();
			IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if (slotClickedIndex < handler.getSlots())//book slot
			{
				if (!this.mergeItemStack(slotStack, handler.getSlots(), handler.getSlots() + playerSlots, false))//merge into player inventory
				{
					return ItemStack.EMPTY;
				}
			} else {
				if (!this.mergeItemStack(slotStack, 0, handler.getSlots(), false))//merge into player inventory
				{
					return ItemStack.EMPTY;
				}
			}
			if (slotStack.getCount() == 0) {
				theSlot.putStack(ItemStack.EMPTY);
			} else {
				theSlot.onSlotChanged();
			}
			if (slotStack.getCount() == slotStackCopy.getCount()) {
				return ItemStack.EMPTY;
			}
			theSlot.onTake(par1EntityPlayer, slotStack);
		}
		return slotStackCopy;
	}

    public List<String> getFriendlyFactions() {
        return friendlyFactions;
    }

    public void requestFriendlyFactions() {
        if (!player.world.isRemote) return;  // only run on client
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("requestFactions", true);
        sendDataToServer(tag);
    }

    public void callReinforcements(String factionName) {
        if (!player.world.isRemote) return;  // only run on client
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("reinforce", factionName);
        sendDataToServer(tag);
    }

    private int calculateGoldCost(String size, int standing) {
        int baseCost;
        switch (size.toLowerCase()) {
            case "small":  baseCost = 4; break;
            case "medium": baseCost = 7; break;
            case "large":  baseCost = 10; break;
            default:       baseCost = 6;
        }

        // Discount: up to 40% off for high standing
        float discount = Math.min(0.4f, standing / 250f);
        return Math.max(1, Math.round(baseCost * (1 - discount)));
    }

    private boolean consumeGold(EntityPlayerMP player, int cost) {
        int found = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == Items.GOLD_INGOT) {
                found += stack.getCount();
            }
        }

        if (found < cost) return false;

        int toRemove = cost;
        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.GOLD_INGOT) {
                int removed = Math.min(toRemove, stack.getCount());
                stack.shrink(removed);
                toRemove -= removed;
                if (toRemove <= 0) break;
            }
        }

        player.inventory.markDirty();
        return true;
    }

    private void spawnReinforcements(World world, BlockPos center, EntityPlayerMP player, String faction, String size) {
        int count;
        switch (size.toLowerCase()) {
            case "small":  count = 3 + world.rand.nextInt(2); break;   // 3–4
            case "medium": count = 5 + world.rand.nextInt(5); break;   // 5–9
            case "large":  count = 10 + world.rand.nextInt(3); break;  // 10–12
            default:       count = 4;
        }

        for (int i = 0; i < count; i++) {
            spawnNpc(world, center, faction, "soldier", AWEntityRegistry.NPC_FACTION_SOLDIER);
        }
    }

    private void spawnNpc(World world, BlockPos center, String faction, String subtype, String entityId) {
        NpcBase npc = AWNPCEntities.createNpc(world, entityId, subtype, faction);
        if (npc != null) {
            double dx = center.getX() + 2 + world.rand.nextInt(4);
            double dz = center.getZ() + 2 + world.rand.nextInt(4);
            npc.setPosition(dx + 0.5, center.getY() + 1, dz + 0.5);
            npc.setHomeAreaAtCurrentPosition(); // optional: so it doesn’t wander too far

            // Save the spawn origin to tag for return logic
            npc.getEntityData().setTag("aw_spawn_origin", new NBTTagIntArray(new int[]{center.getX(), center.getY(), center.getZ()}));

            world.spawnEntity(npc);
        }
    }





}
