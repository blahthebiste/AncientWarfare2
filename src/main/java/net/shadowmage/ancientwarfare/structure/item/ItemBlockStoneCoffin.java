package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.structure.block.BlockCoffin;
import net.shadowmage.ancientwarfare.structure.block.BlockStoneCoffin;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ItemBlockStoneCoffin extends ItemBlockCoffin {
	private static final String VARIANT_TAG = "variant";

	public ItemBlockStoneCoffin(Block block) {
		super(block);
	}

	@Override
	protected boolean mayPlace(World world, BlockPos pos, EnumFacing sidePlacedOn, EntityPlayer placer) {
		return canPlace(world, pos, sidePlacedOn, placer);
	}

	public static boolean canPlace(World world, BlockPos pos, EnumFacing sidePlacedOn, EntityPlayer placer) {
		EnumFacing facing = placer.getHorizontalFacing();
		for (int offset = 1; offset < 4; offset++) {
			if (!mayPlaceAt(world, pos.offset(facing, offset), sidePlacedOn, false)) {
				return false;
			}
			if (!mayPlaceAt(world, pos.offset(facing.rotateYCCW(), 1).offset(facing, offset), sidePlacedOn, false)) {
				return false;
			}
		}
		return true;
	}

	public static BlockStoneCoffin.Variant getVariant(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTagCompound() ? BlockStoneCoffin.Variant.fromName(stack.getTagCompound().getString(VARIANT_TAG)) : BlockStoneCoffin.Variant.getDefault();
	}

	public static ItemStack getVariantStack(BlockCoffin.IVariant variant) {
		ItemStack stack = new ItemStack(AWStructureBlocks.STONE_COFFIN);
		stack.setTagCompound(new NBTBuilder().setString(VARIANT_TAG, variant.getName()).build());
		return stack;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return super.getTranslationKey(stack);
		}

		//noinspection ConstantConditions
		return String.format("%s.%s", super.getTranslationKey(stack), stack.getTagCompound().getString(VARIANT_TAG));
	}
}
