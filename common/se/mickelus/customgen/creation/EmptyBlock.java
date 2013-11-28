package se.mickelus.customgen.creation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import se.mickelus.customgen.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.Configuration;

public class EmptyBlock extends Block {

	public EmptyBlock(int id) {
		super(id, Material.ground);
		
		setCreativeTab(CreativeTabs.tabMisc);
		
		setUnlocalizedName(Constants.EMPTY_UNLOC_NAME);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(Constants.TEXTURE_LOCATION + ":" + Constants.EMPTY_TEXTURE);
	}

}

