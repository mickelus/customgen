package se.mickelus.customgen.creation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import se.mickelus.customgen.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class InterfaceBlock extends Block {
	
	@SideOnly(Side.CLIENT)
	protected Icon[] icons;

	 

	public InterfaceBlock(int id) {
		super(id, Material.ground);
		
		setCreativeTab(CreativeTabs.tabMisc);
		
		setUnlocalizedName(Constants.INTERFACEBLOCK_UNLOC_NAME);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		icons = new Icon[9];
		for (int i = 0; i < 9; i++) {
			icons[i] = iconRegister.registerIcon(Constants.TEXTURE_LOCATION + ":" + Constants.INTERFACEBLOCK_TEXTURE + (i+1));
		}
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int meta) {
		if(meta < 9) {
			return icons[meta];
		}
		return icons[0];
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y,
			int z, EntityPlayer player, int side, float par7,
			float par8, float par9) {	
		
		if(!player.isSneaking()) {
			if(!world.isRemote) {
				int newMeta = world.getBlockMetadata(x, y, z) + 1;
				
				if(newMeta >= 9) {
					newMeta = 0;
				}
				
				world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
			}
			return true;
		}
			
		
			
		return false;
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z,
			int side, float par6, float par7, float par8, int meta) {
		
		int clickedBlockID = -1;
		int clickedBlockMeta = -1;
		switch(side) {
			case 0:
				clickedBlockID = world.getBlockId(x, y+1, z);
				clickedBlockMeta = world.getBlockMetadata(x, y+1, z);
				break;
			case 1:
				clickedBlockID = world.getBlockId(x, y-1, z);
				clickedBlockMeta = world.getBlockMetadata(x, y-1, z);
				break;
			case 2:
				clickedBlockID = world.getBlockId(x, y, z+1);
				clickedBlockMeta = world.getBlockMetadata(x, y, z+1);
				break;
			case 3:
				clickedBlockID = world.getBlockId(x, y, z-1);
				clickedBlockMeta = world.getBlockMetadata(x, y, z-1);
				break;
			case 4:
				clickedBlockID = world.getBlockId(x+1, y, z);
				clickedBlockMeta = world.getBlockMetadata(x+1, y, z);
				break;
			case 5:
				clickedBlockID = world.getBlockId(x-1, y, z);
				clickedBlockMeta = world.getBlockMetadata(x-1, y, z);
				break;
		}
		if(clickedBlockID == Constants.INTERFACEBLOCK_ID) {
			return clickedBlockMeta;
		}
		
		return super.onBlockPlaced(world, x, y, z, side, par6, par7, par8,
				meta);
	}

}
