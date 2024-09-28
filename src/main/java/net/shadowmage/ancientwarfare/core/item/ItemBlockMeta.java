package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends ItemBlockBase {

	public ItemBlockMeta(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey(stack) + "." + stack.getItemDamage();
	}

	@Override
	public int getMetadata(int itemDamage) {
		return itemDamage;
	}

}
