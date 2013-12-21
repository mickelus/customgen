package se.mickelus.customgen.items;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.Customgen;
import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.gui.GuiScreenGenBook;
import se.mickelus.customgen.network.PacketHandler;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.newstuff.GenManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;

public class GenBookItem extends Item {

	public GenBookItem(int itemID) {
    super(itemID);
        
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("PlaceholderItem");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(Constants.TEXTURE_LOCATION + ":" + Constants.BOOKITEM_TEXTURE);
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
    		
    		// send array of gen names to client
    		GenManager genManager = GenManager.getInstance();
    		Gen[] gens = new Gen[genManager.getNumGens()];
    		for (int i = 0; i < gens.length; i++) {
				gens[i] = genManager.getGenByIndex(i);
			}
    		
    		
    		PacketHandler.sendGenList(gens, (Player)player);
    		
    	} else {
    		GuiScreenGenBook.getInstance().SetPlayer(player);
    	}
    }
    
}
