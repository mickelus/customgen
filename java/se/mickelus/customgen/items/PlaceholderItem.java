package se.mickelus.customgen.items;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.CustomgenCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;


public class PlaceholderItem extends Item {
	
	private static PlaceholderItem instance; 

	public PlaceholderItem() {
		super();
		
		instance = this;
        
        maxStackSize = 64;
        setUnlocalizedName(Constants.PLACEHOLDERITEM_UNLOC_NAME);
        
        setTextureName(Constants.TEXTURE_LOCATION + ":" + Constants.PLACEHOLDERITEM_TEXTURE);
        
        setCreativeTab(CustomgenCreativeTabs.getInstance());
    }
	
	public static PlaceholderItem getInstance() {
		return instance;
	}

}
