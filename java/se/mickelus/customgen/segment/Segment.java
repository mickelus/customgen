package se.mickelus.customgen.segment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.blocks.InterfaceBlock;
import se.mickelus.customgen.newstuff.Gen;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;

public class Segment {
	
	public static final String NAME_KEY = "name";
	public static final String BLOCKS_KEY = "blocks";
	public static final String DATA_KEY = "data";
	public static final String BLOCK_MAP_KEY = "map";
	public static final String ITEM_MAP_KEY = "itemmap";
	public static final String TILE_ENTITY_KEY = "tileentity";
	public static final String ENTITY_KEY = "entity";
	public static final String INTERFACE_KEY = "interfaces";
	
	public static final String NBT_POSITION_KEY = "Pos";
	public static final String NBT_DIMENSION_KEY = "Dimension";
	
	private String name;
	
	private BiMap<Integer, Block> blockMap;
	
	private int[] blocks;
	private int[] data;
	
	private ArrayList<NBTTagCompound> tileEntityNBTList;
	private ArrayList<NBTTagCompound> entityNBTList;
	
	private int[] interfaces;
	
	public Segment(String name) {
		
		blockMap = HashBiMap.create();
		blockMap.put(0, Blocks.air);
		
		blocks = new int[4096];
		data = new int[4096];
		
		tileEntityNBTList = new ArrayList<NBTTagCompound>();
		entityNBTList = new ArrayList<NBTTagCompound>();
		
		interfaces = new int[6];
		
		this.name = name;
				
	}
	
	public void setBlock(int x, int y, int z, IBlockState state) {
		
		int blockID;
		Block block = state.getBlock();
		
		// check if block exists in segment
		if(!blockMap.containsValue(block)) {
			
			// index for this block will be the size of the array
			blockID = blockMap.size();
			// add block if it does not exist
			blockMap.put(blockID, block);
		} else {
			blockID = blockMap.inverse().get(block);
		}
		
		blocks[x+z*16+y*256] = blockID;
		data[x+z*16+y*256] = block.getMetaFromState(state);
	}
	
	/**
	 * @param side The side of the segment. values and sides map like this:
	 *     0 - top
	 *     1 - bottom
	 *     2 - north
	 *     3 - east
	 *     4 - south
	 *     5 - west
	 * @return The interface value for the given side
	 */
	public int getInterface(int side) {
		return interfaces[side];
	}

	/**
	 * 
	 * @param side The side of the segment. values and sides map like this:
	 *     0 - top
	 *     1 - bottom
	 *     2 - north
	 *     3 - east
	 *     4 - south
	 *     5 - west
	 * @param value The value of this interface, should be >= 0
	 */
	public void setInterface(int side, int value) {
		interfaces[side] = value;
	}

	/**
	 * Returns the block at the given position in this segment.
	 * @param x The x coordinate, a value between 0 and 15 (inclusive)
	 * @param y The y coordinate, a value between 0 and 15 (inclusive)
	 * @param z The z coordinate, a value between 0 and 15 (inclusive)
	 * @return 
	 */
	public Block getBlock(int x, int y, int z) {
		return blockMap.get(blocks[x+z*16+y*256]);
	}
	
	/**
	 * Returns the metadata for the block at the given position in this segment.
	 * @param x The x coordinate, a value between 0 and 15 (inclusive)
	 * @param y The y coordinate, a value between 0 and 15 (inclusive)
	 * @param z The z coordinate, a value between 0 and 15 (inclusive)
	 * @return 
	 */
	public int getBlockMetadata(int x, int y, int z) {
		return data[x+z*16+y*256];
	}
	
	public int getNumTileEntities() {
		return tileEntityNBTList.size();
	}
	
	public NBTTagCompound getTileEntityNBT(int index) {
		return (NBTTagCompound)tileEntityNBTList.get(index).copy();
	}
	
	public void setTileEntityNBTs(NBTTagCompound[] tags) {
		tileEntityNBTList.clear();
		tileEntityNBTList.ensureCapacity(tags.length);
		for (NBTTagCompound nbtTagCompound : tags) {
			tileEntityNBTList.add(nbtTagCompound);
		}
	}
	
	public int getNumEntities() {
		return entityNBTList.size();
	}
	
	public NBTTagCompound getEntityNBT(int index) {
		return (NBTTagCompound)entityNBTList.get(index).copy();
	}
	
	public void setEntityNBTs(NBTTagCompound[] tags) {
		entityNBTList.clear();
		entityNBTList.ensureCapacity(tags.length);
		for (NBTTagCompound nbtTagCompound : tags) {
			entityNBTList.add(nbtTagCompound);
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public NBTTagCompound writeToNBT(boolean writeBlocks) throws IOException {
		NBTTagCompound nbt = new NBTTagCompound();
		List<NBTTagCompound> tileEntityTempList = new ArrayList<NBTTagCompound>(tileEntityNBTList.size());
		NBTTagList tileEntityTagList = new NBTTagList();
		NBTTagList entityTagList = new NBTTagList();
		
		Map<String, Integer> itemMap = new HashMap<String, Integer>();
		
		// copy tile entities
		for (NBTTagCompound tileEntityNBT : tileEntityNBTList) {
			tileEntityTempList.add((NBTTagCompound)tileEntityNBT.copy());
		}
		
		// write name
		nbt.setString(NAME_KEY, name);
		
		// write interfaces
		nbt.setIntArray(INTERFACE_KEY, interfaces);

		// write blocks and meta
		if(writeBlocks) {
			
			// store block "ID"s
			nbt.setIntArray(BLOCKS_KEY, blocks);
			nbt.setIntArray(DATA_KEY, data);
			
			// convert id to block mapping to a storable format
			Map<Integer, String> nameMap = new HashMap<Integer, String>(blockMap.size());
			for (int key : blockMap.keySet()) {
				Block block = blockMap.get(key);
				UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(block);
				nameMap.put(key, identifier.toString());
			}
			
			// serialize to byte array
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(nameMap);
			byte[] byteArray = byteStream.toByteArray();
			nbt.setByteArray(BLOCK_MAP_KEY, byteArray);
			objectStream.close();	

		}
		
		// write tile entities
		for (NBTTagCompound nbtTagCompound : tileEntityTempList) {
			tileEntityTagList.appendTag(nbtTagCompound);
		}
		nbt.setTag(TILE_ENTITY_KEY, tileEntityTagList);
		
		// write entities
		for (NBTTagCompound nbtTagCompound : entityNBTList) {
			entityTagList.appendTag(nbtTagCompound);
		}
		nbt.setTag(ENTITY_KEY, entityTagList);
		
		return nbt;
	}
	
	public static Segment readFromNBT(NBTTagCompound nbt) {
		
		// read name and create segment
		Segment segment = new Segment(nbt.getString("name"));
		
		NBTTagList tileEntityList = nbt.getTagList(TILE_ENTITY_KEY, 10);
		NBTTagList entityList = nbt.getTagList(ENTITY_KEY, 10);
		NBTTagCompound[] tileEntityArray = new NBTTagCompound[tileEntityList.tagCount()];
		NBTTagCompound[] entityArray = new NBTTagCompound[entityList.tagCount()];
		
		
		int[] interfaces = nbt.getIntArray(INTERFACE_KEY);
		
		// setting interfaces
		if(interfaces.length == 6) {
			for (int i = 0; i < interfaces.length; i++) {
				segment.setInterface(i, interfaces[i]);
			}
		}
		
		// set block "ID"s and data
		segment.blocks = nbt.getIntArray(BLOCKS_KEY);
		segment.data = nbt.getIntArray(DATA_KEY);
		
		
		if(nbt.hasKey(BLOCK_MAP_KEY)) {
			try {
				
				// read block mappings from nbt
				byte[] byteArray = nbt.getByteArray(BLOCK_MAP_KEY);
				ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);
				ObjectInputStream objectStream = new ObjectInputStream(byteStream);
				Map<Integer, String> nameMap = (Map<Integer, String>)objectStream.readObject();
				objectStream.close();
				
				// attempt to get block references from identifier
				for (Integer key : nameMap.keySet()) {
					ResourceLocation resourceLocation = new ResourceLocation(nameMap.get(key));
					
					Block block = GameData.getBlockRegistry().getObject(resourceLocation);
					if(block == null) {
						// if this block does not exist replace it with air
						segment.replaceBlocks(key, 0, (byte)0);
					} else {
						segment.blockMap.put(key, block);
					}
					
				}
				
			} catch (IOException e) {
				MLogger.logf("Failed to read block map for segment %s", segment.getName());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				MLogger.logf("Failed to read block map for segment %s", segment.getName());
				e.printStackTrace();
			}
		}	

		// set tile entities
		for (int i = 0; i < tileEntityList.tagCount(); i++) {
			tileEntityArray[i] = (NBTTagCompound)tileEntityList.getCompoundTagAt(i);
		}
		segment.setTileEntityNBTs(tileEntityArray);
		segment.cleanTileEntityNBTs();

		// set entities
		for (int i = 0; i < entityList.tagCount(); i++) {
			entityArray[i] = (NBTTagCompound)entityList.getCompoundTagAt(i);
		}
		segment.setEntityNBTs(entityArray);

		return segment;
	}
	
	public void parseFromWorld(World world, int chunkX, int chunkY, int chunkZ) {
		int[] interfaces = new int[6];
		
		int xOffset = chunkX*16;
		int yOffset = chunkY;
		int zOffset = chunkZ*16;
		
		ArrayList<NBTTagCompound> tileEntityNBTList = new ArrayList<NBTTagCompound>();
		ClassInheritanceMultiMap entityMap = world.getChunkFromChunkCoords(chunkX, chunkZ).getEntityLists()[chunkY/16];
		
		// set blocks
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					setBlock(x, y, z,world.getBlockState(new BlockPos(xOffset+x, yOffset+y, zOffset+z)));
				}
			}
		}

		// set interfaces
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				IBlockState stateTop = world.getBlockState(new BlockPos(xOffset+i, yOffset+15, zOffset+j));
				IBlockState stateBot = world.getBlockState(new BlockPos(xOffset+i, yOffset, zOffset+j));
				
				IBlockState stateSouth = world.getBlockState(new BlockPos(xOffset+i, yOffset+j, zOffset+15));
				IBlockState stateNorth = world.getBlockState(new BlockPos(xOffset+i, yOffset+j, zOffset));
				
				IBlockState stateEast = world.getBlockState(new BlockPos(xOffset+15, yOffset+i, zOffset+j));
				IBlockState stateWest = world.getBlockState(new BlockPos(xOffset, yOffset+i, zOffset+j));
				
				// top
				if(stateTop.getBlock().equals(InterfaceBlock.getInstance())) {
					interfaces[0] += 1 + InterfaceBlock.getInstance().getMetaFromState(stateTop);
				}
				// bottom
				if(stateBot.getBlock().equals(InterfaceBlock.getInstance())) {
					interfaces[1] += 1 + InterfaceBlock.getInstance().getMetaFromState(stateBot);
				}
				
				// south
				if(stateSouth.getBlock().equals(InterfaceBlock.getInstance())) {
					interfaces[4] += 1 + InterfaceBlock.getInstance().getMetaFromState(stateSouth);
				}
				// north
				if(stateNorth.getBlock().equals(InterfaceBlock.getInstance())) {
					interfaces[2] += 1 + InterfaceBlock.getInstance().getMetaFromState(stateNorth);
				}
				
				// east
				if(stateEast.getBlock().equals(InterfaceBlock.getInstance())) {
					interfaces[3] += 1 + InterfaceBlock.getInstance().getMetaFromState(stateEast);
				}
				// west
				if(stateWest.getBlock().equals(InterfaceBlock.getInstance())) {
					interfaces[5] += 1 + InterfaceBlock.getInstance().getMetaFromState(stateWest);
				}
			}
		}
		
		for (int i = 0; i < interfaces.length; i++) {
			setInterface(i, interfaces[i]);
		}
		
		// set tile entities
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					TileEntity tileEntity = world.getTileEntity(new BlockPos(xOffset+x, yOffset+y, zOffset+z));
					
					if(tileEntity != null) {
						NBTTagCompound tagCompound = new NBTTagCompound();
				        tileEntity.writeToNBT(tagCompound);
				        tagCompound.setInteger("x", x);
				        tagCompound.setInteger("y", y);
				        tagCompound.setInteger("z", z);
				        tileEntityNBTList.add(tagCompound);
					}
				}
			}
		}
		setTileEntityNBTs(tileEntityNBTList.toArray(new NBTTagCompound[tileEntityNBTList.size()]));
		
		// set entities
		if(!entityMap.isEmpty()) {
			ArrayList<NBTTagCompound> entityNBTList = new ArrayList<NBTTagCompound>();

			for (Object object : entityMap) {
				Entity entity = (Entity)object;
				NBTTagCompound nbt = new NBTTagCompound();
				if(entity.writeToNBTOptional(nbt)) {
					NBTTagList positionList = new NBTTagList();
					positionList.appendTag(new NBTTagDouble(entity.posX - xOffset));
					positionList.appendTag(new NBTTagDouble(entity.posY - yOffset));
					positionList.appendTag(new NBTTagDouble(entity.posZ - zOffset));
					nbt.setTag(NBT_POSITION_KEY, positionList);
					
					entityNBTList.add(nbt);
				}
			}
			setEntityNBTs(entityNBTList.toArray(new NBTTagCompound[entityNBTList.size()]));
		}
	}
	
	@Override
	public String toString() {
		String string = "SEGM:";
		string += getName();
		string += "[";
		for (int i = 0; i < interfaces.length; i++) {
			string += String.format("%2d", interfaces[i]);
			if(i != interfaces.length-1) {
				string += ",";
			}
		}
		string += "]";

		return string;
	}
	
	/**
	 * Removes all invalid tile entities 
	 */
	private void cleanTileEntityNBTs() {
		if(blocks.length > 0) {
			for (int i = 0; i < tileEntityNBTList.size(); i++) {
				NBTTagCompound nbt = tileEntityNBTList.get(i);
				Block block = getBlock(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
				if(block == Blocks.air) {
					tileEntityNBTList.remove(i);
					i--;
				}
			}
		}
	}
	
	/**
	 * Replaces blocks in this segment. Make sure a block matching newID is in the blockMap.
	 * @param oldID The ids that should be replaced
	 * @param newID The new block id to be used
	 * @param newData The new data value
	 */
	private void replaceBlocks(int oldID, int newID, byte newData) {
		for (int i = 0; i < blocks.length; i++) {
			if(blocks[i] == oldID) {
				blocks[i] = newID;
				data[i] = newData;
			}
		}
	}

}
