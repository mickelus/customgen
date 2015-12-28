package se.mickelus.customgen.items;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.CustomgenCreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class PlaceholderItem extends Item {
	
	private static PlaceholderItem instance; 

	public PlaceholderItem() {
		super();
		
		instance = this;
        
        maxStackSize = 64;
        GameRegistry.registerItem(this, Constants.PLACEHOLDERITEM_UNLOC_NAME);
        setUnlocalizedName(Constants.PLACEHOLDERITEM_UNLOC_NAME);
        setCreativeTab(CustomgenCreativeTabs.getInstance());
    }
	
	public static PlaceholderItem getInstance() {
		return instance;
	}

}
