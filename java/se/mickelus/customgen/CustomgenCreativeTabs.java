package se.mickelus.customgen;

import se.mickelus.customgen.blocks.EmptyBlock;
import se.mickelus.customgen.items.GenBookItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class CustomgenCreativeTabs extends CreativeTabs {
	
	private static CustomgenCreativeTabs instance;
	
	public CustomgenCreativeTabs() {
		super("customgen");
		
		instance = this;
	}

	@Override
	public Item getTabIconItem() {
		return GenBookItem.getInstance();
	}
	
	public static CustomgenCreativeTabs getInstance() {
		return instance;
	}

}
