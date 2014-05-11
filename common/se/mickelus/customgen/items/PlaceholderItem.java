package se.mickelus.customgen.items;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.CustomgenCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;


public class PlaceholderItem extends Item {

	public PlaceholderItem() {
    super();
        
        maxStackSize = 64;
        setUnlocalizedName(Constants.PLACEHOLDERITEM_UNLOC_NAME);
        
        setTextureName(Constants.TEXTURE_LOCATION + ":" + Constants.PLACEHOLDERITEM_TEXTURE);
        
        setCreativeTab(CustomgenCreativeTabs.getInstance());
    }

}
