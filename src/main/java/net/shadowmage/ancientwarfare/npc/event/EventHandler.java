package net.shadowmage.ancientwarfare.npc.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.gamedata.TownMap;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.tile.ISpecialLootContainer;
import net.shadowmage.ancientwarfare.structure.tile.TileProtectionFlag;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class EventHandler {
	public static final String NO_SPAWN_PREVENTION_TAG = "noSpawnPrevention";
	private Set<Predicate<EntityAIBase>> additionalHostileAIChecks = new HashSet<>();
	public static final EventHandler INSTANCE = new EventHandler();

	private EventHandler() {
	}

	public void registerAdditionalHostileAICheck(Predicate<EntityAIBase> aiCheck) {
		additionalHostileAIChecks.add(aiCheck);
	}

	private int getHostileAIPriority(EntityCreature entity) {
		// Again search for a task to use for the same priority as our attack NPC task, but
		// look for "EntityAINearestAttackableTarget" instead which is for ranged units
		for (EntityAITaskEntry taskEntry : entity.targetTasks.taskEntries) {
			if (((taskEntry.action instanceof EntityAINearestAttackableTarget && ((EntityAINearestAttackableTarget) taskEntry.action).targetClass == EntityPlayer.class)
					|| anyAdditionalHostileAIChecksMatch(taskEntry)) && taskEntry.priority != -1) {
				return taskEntry.priority;
			}
		}
		return -1;
	}

	private boolean anyAdditionalHostileAIChecksMatch(EntityAITaskEntry taskEntry) {
		for (Predicate<EntityAIBase> additionalCheck : additionalHostileAIChecks) {
			if (additionalCheck.test(taskEntry.action)) {
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		injectAi(event);
		preventHostileSpawnsInStructures(event);
	}

	private void preventHostileSpawnsInStructures(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity.getEntityWorld().isRemote || !IMob.MOB_SELECTOR.apply(entity) || isMarkedWithNoPreventionTag(entity)) {
			return;
		}

		World world = event.getWorld();

		BlockPos pos = event.getEntity().getPosition();

		if (AWGameData.INSTANCE.getPerWorldData(world, StructureMap.class).shouldPreventSpawnAtPos(world, pos)
				|| AWGameData.INSTANCE.getPerWorldData(world, TownMap.class).shouldPreventSpawnAtPos(world, pos)) {
			event.setCanceled(true);
		}
	}

	private boolean isMarkedWithNoPreventionTag(Entity entity) {
		return entity.getTags().contains(NO_SPAWN_PREVENTION_TAG);
	}

	private void injectAi(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof NpcBase) {
			return;
		}
		if (!(event.getEntity() instanceof EntityCreature)) {
			return;
		}
		// Use new "auto injection"
		EntityCreature entity = (EntityCreature) event.getEntity();

		// all mobs that can attack somebody should have targetTasks
		int targetTaskPriority = getHostileAIPriority(entity);
		if (targetTaskPriority != -1) {
			entity.targetTasks.addTask(targetTaskPriority, new EntityAINearestAttackableTarget<>(entity, NpcBase.class, 0, true, false,
					e -> entityShouldTarget(entity, e)
			));
		}
	}

	private boolean entityShouldTarget(EntityCreature entity, @Nullable NpcBase e) {
		if (e == null) {
			return false;
		}

		if ((e instanceof NpcFaction) && !e.isPassive() && FactionRegistry.getFaction(((NpcFaction) e).getFaction()).isTarget(entity)) {
			return true;
		}

		return (e instanceof NpcPlayerOwned) && NpcDefaultsRegistry.getOwnedNpcDefault((NpcPlayerOwned) e).isTarget(entity);

	}

	@SubscribeEvent
	public void onChestClicked(PlayerInteractEvent.RightClickBlock evt) {
		if (!AWCoreStatics.chestProtection) return; // No need to check for locked chests when this config option is false
		World world = evt.getWorld();
		BlockPos pos = evt.getPos();
		EntityPlayer player = evt.getEntityPlayer();
		if (!player.capabilities.isCreativeMode && isLockedContainer(world, pos)) {
			AWGameData.INSTANCE.getPerWorldData(world, StructureMap.class).getStructureAt(world, pos).ifPresent(structure -> {
				Optional<TileProtectionFlag> tile = WorldTools.getTile(world, structure.getProtectionFlagPos(), TileProtectionFlag.class);
				if(AWCoreStatics.allowStealing) {
//					System.out.println("Allow stealing attempt!");
					// Skip the flag protection. Always let the player loot chests if no one is around to see it
					for (NpcFaction factionNpc : world.getEntitiesWithinAABB(NpcFaction.class, structure.getBB().getAABB())) {
						// If one of the entities can see the player, prevent them from opening the chest.
						if (factionNpc.canTarget(player) && player.canEntityBeSeen(factionNpc)) {
							evt.setCanceled(true);
							evt.setCancellationResult(EnumActionResult.FAIL);
							factionNpc.addPotionEffect(new PotionEffect(MobEffects.GLOWING, AWCoreStatics.glowDuration));
							if (world.isRemote) {
								player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarenpc.no_chest_stealing",
										StringUtils.capitalize(factionNpc.getFaction())), true);
							}
							return;
						}
					}
				}
				else { // Old code for full loot chest protection. Player must clear enemies and claim the flag to open chests.
//					System.out.println("Old-school chest protection!");
					if (tile.isPresent() && tile.get().shouldProtectAgainst(player)) {
						evt.setCanceled(true);
						evt.setCancellationResult(EnumActionResult.FAIL);
						if (world.isRemote) {
							player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarenpc.no_chest_access_flag_not_claimed"), true);
						}
					} else {
						for (NpcFaction factionNpc : world.getEntitiesWithinAABB(NpcFaction.class, structure.getBB().getAABB())) {
							if (!factionNpc.isPassive()) {
								evt.setCanceled(true);
								evt.setCancellationResult(EnumActionResult.FAIL);
								factionNpc.addPotionEffect(new PotionEffect(MobEffects.GLOWING, AWCoreStatics.glowDuration));
								if (world.isRemote) {
									player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarenpc.no_chest_access",
											StringUtils.capitalize(factionNpc.getFaction())), true);
								}
								return;
							}
						}
					}
				}
			});
		}
	}

	@SubscribeEvent
	public void playerDigSpeed(PlayerEvent.BreakSpeed evt) {
		if (evt.getEntityPlayer().isCreative() || evt.getEntityPlayer().isSpectator()) {
			return;
		}

		World world = evt.getEntityPlayer().world;
		AWGameData.INSTANCE.getPerWorldData(world, StructureMap.class).getStructureAt(world, evt.getPos())
				.flatMap(structureEntry -> WorldTools.getTile(world, structureEntry.getProtectionFlagPos(), TileProtectionFlag.class)).ifPresent(tile -> {
			if (tile.shouldProtectAgainst(evt.getEntityPlayer()) && shouldBlockSlowDownDigging(world, evt.getPos())) {
				evt.setNewSpeed(evt.getOriginalSpeed() / AWCoreStatics.blockProtectionMulti);
			}
		});
	}

	private boolean shouldBlockSlowDownDigging(World world, BlockPos pos) {
		if(!AWCoreStatics.blockProtection) return false;
		IBlockState state = world.getBlockState(pos);
		if (!state.isFullBlock()) {
			return isLockedContainer(world, pos);
		}

		Block block = state.getBlock();
		return !(block == Blocks.MOB_SPAWNER || block == AWStructureBlocks.ADVANCED_SPAWNER);
	}

	public static final String GENERATED_INVENTORY_TAG = "generatedInventory";

	private boolean isLockedContainer(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (!block.hasTileEntity(state)) {
			return false;
		}

		return WorldTools.getTile(world, pos).map(te -> te.getTileData().getBoolean(GENERATED_INVENTORY_TAG) || te instanceof ISpecialLootContainer).orElse(false);
	}

	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent evt)
	{
		// When demons are going to take damage, if it is fire/lava/heat damage, ignore it.
		if(AWCoreStatics.demonsImmuneToFire && evt.getSource().isFireDamage()) {
			// Only active if the demons_immune_to_fire option is enabled in the config.
			if (evt.getEntity() instanceof NpcFaction && ((NpcFaction) evt.getEntity()).getFaction().equals("demon")) {
				evt.setCanceled(true);
			}
		}
	}

}
