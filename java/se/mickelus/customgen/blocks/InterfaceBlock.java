package se.mickelus.customgen.blocks;

import java.util.List;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.CustomgenCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InterfaceBlock extends Block {
	
	public static final PropertyEnum VALUE = PropertyEnum.create("value", InterfaceBlock.EnumType.class);

	private static InterfaceBlock instance;

	public InterfaceBlock() {
		super(Material.ground);
		
		GameRegistry.registerBlock(this, Constants.INTERFACEBLOCK_UNLOC_NAME);
		setUnlocalizedName(Constants.INTERFACEBLOCK_UNLOC_NAME);
		setCreativeTab(CustomgenCreativeTabs.getInstance());
		
		instance = this;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		
		if(!player.isSneaking()) {
			if(!world.isRemote) {
				world.setBlockState(pos, getStateFromMeta(getMetaFromState(state) + 1), 3);
			}
			return true;
		}

		return false;
	}
	
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		
		IBlockState blockstate = world.getBlockState(pos.offset(facing.getOpposite()));

		if(getInstance().equals(blockstate.getBlock())) {
			return blockstate;
		}
		
		return super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
	}
	
	public static InterfaceBlock getInstance() {
		return instance;
	}

	@Override
    public IBlockState getStateFromMeta(int meta) {
		if(meta >= 9) {
			meta = 0;
		} else if (meta < 0) {
			meta = 8;
		}
		return this.getDefaultState().withProperty(VALUE, InterfaceBlock.EnumType.byMetadata(meta));
    }

	@Override
    public int getMetaFromState(IBlockState state) {
		return ((InterfaceBlock.EnumType)state.getValue(VALUE)).getMetadata();
    }

	@Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {VALUE});
    }
	
	public static enum EnumType implements IStringSerializable {
        ONE(0, "1"),
        TWO(1, "2"),
        THREE(2, "3"),
        FOUR(3, "4"),
        FIVE(4, "5"),
        SIX(5, "6"),
        SEVEN(6, "7"),
        EIGHT(7, "8"),
        NINE(8, "9");
        private static final InterfaceBlock.EnumType[] META_LOOKUP = new InterfaceBlock.EnumType[values().length];
        private final int meta;
        private final String name;
        private final String unlocalizedName;

        private EnumType(int meta, String name)
        {
            this(meta, name, name);
        }

        private EnumType(int meta, String name, String unlocalizedName)
        {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public static InterfaceBlock.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        static
        {
        	InterfaceBlock.EnumType[] enumTypes = values();
            int length = enumTypes.length;

            for (int i = 0; i < length; ++i)
            {
            	InterfaceBlock.EnumType enumType = enumTypes[i];
                META_LOOKUP[enumType.getMetadata()] = enumType;
            }
        }
    }
}
