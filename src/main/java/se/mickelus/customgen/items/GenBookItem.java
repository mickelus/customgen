package se.mickelus.customgen.items;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.CustomgenCreativeTabs;
import se.mickelus.customgen.gui.GuiScreenGenBook;
import se.mickelus.customgen.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GenBookItem extends Item {
	
	private static GenBookItem instance;

	public GenBookItem() {
    super();
        
        maxStackSize = 1;
        
        GameRegistry.registerItem(this, Constants.BOOKITEM_UNLOC_NAME);
        setUnlocalizedName(Constants.BOOKITEM_UNLOC_NAME);
        setCreativeTab(CustomgenCreativeTabs.getInstance());
        
        instance = this;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world,
    		EntityPlayer player) {
    	openBook(player, world);
    	return super.onItemRightClick(stack, world, player);
    }
    
    private void openBook(EntityPlayer player, World world) {
    	if(!world.isRemote) {
    		PacketBuilder.sendGenListResponse(player);
    	} else {
    		GuiScreenGenBook.getInstance().SetPlayer(player);
    	}
    }
    
    public static GenBookItem getInstance() {
    	return instance;
    }
    
}
