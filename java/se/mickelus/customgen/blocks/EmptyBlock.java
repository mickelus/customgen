package se.mickelus.customgen.blocks;


import se.mickelus.customgen.Constants;
import se.mickelus.customgen.CustomgenCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;


public class EmptyBlock extends Block {
	
	private static EmptyBlock instance;

	public EmptyBlock() {
		super(Material.ground);		
		
		setUnlocalizedName(Constants.EMPTY_UNLOC_NAME);
		setCreativeTab(CustomgenCreativeTabs.getInstance());
		
		instance = this;
	}
	
	public static EmptyBlock getInstance() {
		return instance;
	}
	
	public String getName() {
		return Constants.EMPTY_UNLOC_NAME;
	}

}

