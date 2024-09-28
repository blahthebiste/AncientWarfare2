package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldGenDetailedLogHelper;

import java.util.function.Predicate;

// Same as the WATER validator, but with lava. Useful for generating structures in the Nether sea.
public class StructureValidatorLava extends StructureValidator {

	public StructureValidatorLava() {
		super(StructureValidationType.LAVA);
	}

	@Override
	public boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
		Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
		WorldGenDetailedLogHelper.log("AW2t: LAVA shouldInclude; y={}, block={}",()->y-1, block::getTranslationKey);
		return block == Blocks.LAVA || block == Blocks.FLOWING_LAVA;
	}

	@Override
	public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		int minY = getMinY(template, bb);
		WorldGenDetailedLogHelper.log("AW2t: validating placement for LAVA type structure...");
		return validateBorderBlocks(world, bb, 0, 31, false);
	}

	@Override
	public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {
		//noop
		WorldGenDetailedLogHelper.log("AW2t: LAVA preGen");
	}

	@Override
	public void handleClearAction(World world, BlockPos pos, StructureTemplate template, StructureBB bb) {
		WorldGenDetailedLogHelper.log("AW2t: LAVA handleClear");
		if (pos.getY() < bb.min.getY() + template.getOffset().getY()) {
			world.setBlockState(pos, Blocks.LAVA.getDefaultState());
		} else {
			super.handleClearAction(world, pos, template, bb);
		}
	}

	/*
	 * validates the target block at x,y,z is one of the input valid blocks
	 */
	@Override
	public boolean validateBlockType(World world, int x, int y, int z, Predicate<IBlockState> isValidState) {
		if (y <= 0 || y >= world.getHeight()) {
			return false;
		}
		IBlockState state = world.getBlockState(new BlockPos(x, y, z));
		Block block = state.getBlock();
		if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
			return true;
		}
		else {
			WorldGenDetailedLogHelper.log("Rejected because of invalid target block \"{}\" at: x {} y {} z {} ", block::getRegistryName, () -> x, () -> y, () -> z);
			return false;
		}

	}
}
