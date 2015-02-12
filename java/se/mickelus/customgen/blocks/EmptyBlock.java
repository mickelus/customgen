package se.mickelus.customgen.blocks;


import se.mickelus.customgen.Constants;
import se.mickelus.customgen.CustomgenCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class EmptyBlock extends Block {
	
	private static EmptyBlock instance;

	public EmptyBlock() {
		super(Material.ground);		
		
		GameRegistry.registerBlock(this, Constants.EMPTY_UNLOC_NAME);
		setUnlocalizedName(Constants.EMPTY_UNLOC_NAME);
		setCreativeTab(CustomgenCreativeTabs.getInstance());
		instance = this;
	}
	
	public static EmptyBlock getInstance() {
		return instance;
	}
}

