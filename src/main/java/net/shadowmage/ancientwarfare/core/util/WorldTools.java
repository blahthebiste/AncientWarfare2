package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class WorldTools {
	private WorldTools() {}

	/*
	 * SERVER ONLY
	 */
	public static List<TileEntity> getTileEntitiesInArea(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		if (world instanceof WorldServer) {

			List<TileEntity> tileEntities = Lists.newArrayList();
			for (int x = (x1 >> 4); x <= (x2 >> 4); x++) {
				for (int z = (z1 >> 4); z <= (z2 >> 4); z++) {
					addValidTilesInChunkArea(world, x1, y1, z1, x2, y2, z2, tileEntities, x, z);
				}
			}
			return tileEntities;
		}
		return Collections.emptyList();
	}

	private static void addValidTilesInChunkArea(World world, int x1, int y1, int z1, int x2, int y2, int z2, List<TileEntity> tileEntities, int x, int z) {
		Chunk chunk = world.getChunkFromChunkCoords(x, z);
		for (TileEntity tile : chunk.getTileEntityMap().values()) {
			if (!tile.isInvalid() && isTileInArea(x1, y1, z1, x2, y2, z2, tile)) {
				tileEntities.add(tile);
			}
		}
	}

	private static boolean isTileInArea(int x1, int y1, int z1, int x2, int y2, int z2, TileEntity tile) {
		return tile.getPos().getX() >= x1 && tile.getPos().getY() >= y1 && tile.getPos().getZ() >= z1 && tile.getPos().getX() <= x2 && tile.getPos().getY() <= y2 && tile.getPos().getZ() <= z2;
	}

	public static Optional<IItemHandler> getItemHandlerFromTile(IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
		return getTile(world, pos, TileEntity.class).map(t -> getItemHandlerFromTile(t, side)).orElse(Optional.empty());
	}

	public static Optional<IItemHandler> getItemHandlerFromTile(TileEntity t, @Nullable EnumFacing side) {
		return t.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side) ?
				Optional.ofNullable(t.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) : Optional.empty();
	}

	public static boolean sendClientEventToTile(IBlockAccess world, BlockPos pos, int id, int param) {
		return getTile(world, pos).map(t -> t.receiveClientEvent(id, param)).orElse(false);
	}

	public static Optional<TileEntity> getTile(IBlockAccess world, BlockPos pos) {
		return getTile(world, pos, TileEntity.class);
	}

	public static <T> Optional<T> getTile(@Nullable IBlockAccess world, @Nullable BlockPos pos, Class<T> teClass) {
		if (world == null || pos == null) {
			return Optional.empty();
		}

		TileEntity te = world.getTileEntity(pos);

		if (teClass.isInstance(te)) {
			return Optional.of(teClass.cast(te));
		}

		return Optional.empty();
	}

	public static boolean clickInteractableTileWithHand(World world, BlockPos pos, EntityPlayer player, EnumHand hand) {
		return getTile(world, pos, IInteractableTile.class).map(t -> t.onBlockClicked(player, hand)).orElse(false);
	}

	private static final Map<Integer, Function<World, Boolean>> DIMENSION_DAY_TIMES = new HashMap<>();

	public static void registerDimensionDaytimeLogic(int dimension, Function<World, Boolean> isDayTime) {
		DIMENSION_DAY_TIMES.put(dimension, isDayTime);
	}

	public static boolean isDaytimeInDimension(World world) {
		return DIMENSION_DAY_TIMES.getOrDefault(world.provider.getDimension(), World::isDaytime).apply(world);
	}
}
