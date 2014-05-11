package se.mickelus.customgen.items;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.Customgen;
import se.mickelus.customgen.CustomgenCreativeTabs;
import se.mickelus.customgen.gui.GuiScreenGenBook;
import se.mickelus.customgen.network.GenListReponsePacket;
import se.mickelus.customgen.network.PacketHandler;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.newstuff.GenManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GenBookItem extends Item {
	
	private static GenBookItem instance;

	public GenBookItem() {
    super();
        
        maxStackSize = 1;
        setUnlocalizedName(Constants.BOOKITEM_UNLOC_NAME);
        
        setTextureName(Constants.TEXTURE_LOCATION + ":" + Constants.BOOKITEM_TEXTURE);
        
        setCreativeTab(CustomgenCreativeTabs.getInstance());
        
        instance = this;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world,
    		EntityPlayer player) {
    	openBook(player, world);
    	return super.onItemRightClick(stack, world, player);
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack,
    		EntityPlayer player, World world, int par4, int par5,
    		int par6, int par7, float par8, float par9, float par10) {
    	//openBook(player, world);
    	return super.onItemUse(itemStack, player, world, par4, par5,
    			par6, par7, par8, par9, par10);
    }
    
    private void openBook(EntityPlayer player, World world) {
    	if(!world.isRemote) {
    		PacketHandler.sendGenListResponse(player);
    	} else {
    		GuiScreenGenBook.getInstance().SetPlayer(player);
    	}
    }
    
    public static GenBookItem getInstance() {
    	return instance;
    }
    
}
