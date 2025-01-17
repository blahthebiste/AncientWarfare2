package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.structure.block.BlockBrazierEmber;

public class ItemBlockBrazierEmber extends ItemBlockBase {
	public ItemBlockBrazierEmber(Block block) {
		super(block);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return super.getTranslationKey(stack);
		}

		//noinspection ConstantConditions
		return String.format("%s.%s", super.getTranslationKey(stack),
				stack.getTagCompound().getBoolean(BlockBrazierEmber.LIT_TAG) ? "lit" : "unlit");
	}
}
