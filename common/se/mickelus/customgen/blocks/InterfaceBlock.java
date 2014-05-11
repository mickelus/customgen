package se.mickelus.customgen.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import se.mickelus.customgen.Constants;
import se.mickelus.customgen.CustomgenCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class InterfaceBlock extends Block {
	
	@SideOnly(Side.CLIENT)
	protected IIcon[] icons;

	private static InterfaceBlock instance;

	public InterfaceBlock() {
		super(Material.ground);
		
		setBlockName(Constants.INTERFACEBLOCK_UNLOC_NAME);
		setCreativeTab(CustomgenCreativeTabs.getInstance());
		
		instance = this;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		icons = new IIcon[9];
		for (int i = 0; i < 9; i++) {
			icons[i] = iconRegister.registerIcon(Constants.TEXTURE_LOCATION + ":" + Constants.INTERFACEBLOCK_TEXTURE + (i+1));
		}
		
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if(meta < icons.length) {
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
		
		Block clickedBlock = null;
		int clickedBlockMeta = -1;
		switch(side) {
			case 0:
				clickedBlock = world.getBlock(x, y+1, z);
				clickedBlockMeta = world.getBlockMetadata(x, y+1, z);
				break;
			case 1:
				clickedBlock = world.getBlock(x, y-1, z);
				clickedBlockMeta = world.getBlockMetadata(x, y-1, z);
				break;
			case 2:
				clickedBlock = world.getBlock(x, y, z+1);
				clickedBlockMeta = world.getBlockMetadata(x, y, z+1);
				break;
			case 3:
				clickedBlock = world.getBlock(x, y, z-1);
				clickedBlockMeta = world.getBlockMetadata(x, y, z-1);
				break;
			case 4:
				clickedBlock = world.getBlock(x+1, y, z);
				clickedBlockMeta = world.getBlockMetadata(x+1, y, z);
				break;
			case 5:
				clickedBlock = world.getBlock(x-1, y, z);
				clickedBlockMeta = world.getBlockMetadata(x-1, y, z);
				break;
		}
		if(getInstance().equals(clickedBlock)) {
			return clickedBlockMeta;
		}
		
		return super.onBlockPlaced(world, x, y, z, side, par6, par7, par8,
				meta);
	}
	
	public static InterfaceBlock getInstance() {
		return instance;
	}

}
