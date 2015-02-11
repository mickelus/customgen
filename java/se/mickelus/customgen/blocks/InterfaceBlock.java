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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InterfaceBlock extends Block {
	
	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", InterfaceBlock.EnumType.class);

	private static InterfaceBlock instance;

	public InterfaceBlock() {
		super(Material.ground);
		
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
		
		IBlockState blockstate = null;
		switch(facing) {
			case UP:
				pos.add(0, 1, 0);
				blockstate = world.getBlockState(pos);
				break;
			case DOWN:
				pos.add(0, -1, 0);
				blockstate = world.getBlockState(pos);
				break;
			case SOUTH:
				pos.add(0, 0, 1);
				blockstate = world.getBlockState(pos);
				break;
			case NORTH:
				pos.add(0, 0, -1);
				blockstate = world.getBlockState(pos);
				break;
			case EAST:
				pos.add(1, 0, 0);
				blockstate = world.getBlockState(pos);
				break;
			case WEST:
				pos.add(-1, 0, 0);
				blockstate = world.getBlockState(pos);
				break;
		}
		if(getInstance().equals(blockstate.getBlock())) {
			return blockstate;
		}
		
		return super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
	}
	
	public static InterfaceBlock getInstance() {
		return instance;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        BlockStone.EnumType[] aenumtype = BlockStone.EnumType.values();
        int i = aenumtype.length;

        for (int j = 0; j < i; ++j)
        {
            BlockStone.EnumType enumtype = aenumtype[j];
            list.add(new ItemStack(itemIn, 1, enumtype.getMetadata()));
        }
    }

	@Override
    public IBlockState getStateFromMeta(int meta) {
		if(meta >= 9) {
			meta = 0;
		} else if (meta < 0) {
			meta = 8;
		}
        return this.getDefaultState().withProperty(VARIANT, InterfaceBlock.EnumType.byMetadata(meta));
    }

	@Override
    public int getMetaFromState(IBlockState state) {
        return ((InterfaceBlock.EnumType)state.getValue(VARIANT)).getMetadata();
    }

	@Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {VARIANT});
    }

    public static enum EnumType implements IStringSerializable {
        ONE(0, "one"),
        TWO(1, "two"),
        THREE(2, "three"),
        FOUR(3, "four"),
        FIVE(4, "five"),
        SIX(5, "six"),
        SEVEN(6, "seven"),
        EIGHT(7, "eight"),
        NINE(8, "nine");
        private static final InterfaceBlock.EnumType[] META_LOOKUP = new InterfaceBlock.EnumType[values().length];
        private final int meta;
        private final String name;
        private final String unlocalizedName;

        private static final String __OBFID = "CL_00002058";

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
        	InterfaceBlock.EnumType[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
            	InterfaceBlock.EnumType var3 = var0[var2];
                META_LOOKUP[var3.getMetadata()] = var3;
            }
        }
    }

}
