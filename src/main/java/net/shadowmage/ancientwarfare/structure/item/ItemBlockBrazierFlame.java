package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.structure.block.BlockBrazierFlame;

public class ItemBlockBrazierFlame extends ItemBlockBase {
	public ItemBlockBrazierFlame(Block block) {
		super(block);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return super.getTranslationKey(stack);
		}

		//noinspection ConstantConditions
		return String.format("%s.%s", super.getTranslationKey(stack),
				stack.getTagCompound().getBoolean(BlockBrazierFlame.LIT_TAG) ? "lit" : "unlit");
	}
}
