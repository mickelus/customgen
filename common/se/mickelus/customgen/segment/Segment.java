package se.mickelus.customgen.segment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.blocks.InterfaceBlock;
import se.mickelus.customgen.newstuff.Gen;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary.Type;

public class Segment {
	
	public static final String NAME_KEY = "name";
	public static final String BLOCKS_KEY = "blocks";
	public static final String DATA_KEY = "data";
	public static final String MAP_KEY = "map";
	public static final String TILE_ENTITY_KEY = "tileentity";
	public static final String ENTITY_KEY = "entity";
	public static final String INTERFACE_KEY = "interfaces";
	
	private String name;
	
	private BiMap<Integer, Block> blockMap;
	
	private int[] blocks;
	private byte[] data;
	
	private ArrayList<NBTTagCompound> tileEntityNBTList;
	private ArrayList<NBTTagCompound> entityNBTList;
	
	private int[] interfaces;
	
	public Segment(String name) {
		
		blockMap = HashBiMap.create();
		
		blockMap.put(0, Blocks.air);
		
		blocks = new int[4096];
		data = new byte[4096];
		
		tileEntityNBTList = new ArrayList<NBTTagCompound>();
		entityNBTList = new ArrayList<NBTTagCompound>();
		
		interfaces = new int[6];
		
		this.name = name;
				
	}
	
	public void setBlock(int x, int y, int z, Block block, int blockData) {
		
		int blockID;
		
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
		data[x+z*16+y*256] = (byte)blockData;
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
	public int getBlockData(int x, int y, int z) {
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
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public NBTTagCompound writeToNBT(boolean writeBlocks) {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList tileEntityTagList = new NBTTagList();
		NBTTagList entityTagList = new NBTTagList();
		
		// write name
		nbt.setString(NAME_KEY, name);
		
		// write interfaces
		nbt.setIntArray(INTERFACE_KEY, interfaces);

		// write blocks and meta
		if(writeBlocks) {
			
			// store block "ID"s
			nbt.setIntArray(BLOCKS_KEY, blocks);
			nbt.setByteArray(DATA_KEY, data);
			
			// convert id to block mapping to a storable format
			Map<Integer, String> nameMap = new HashMap<Integer, String>(blockMap.size());
			for (int key : blockMap.keySet()) {
				Block block = blockMap.get(key);
				UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(block);
				nameMap.put(key, identifier.toString());
			}
			
			// serialize to byte array
			try {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
				objectStream.writeObject(nameMap);
				byte[] byteArray = byteStream.toByteArray();
				nbt.setByteArray(MAP_KEY, byteArray);
				objectStream.close();	
			} catch (IOException e) {
				MLogger.log("Encountered an exception when writing block map to nbt");
			}
			
			
		}
		
		// write tile entities
		/*for (NBTTagCompound nbtTagCompound : tileEntityNBTList) {
			tileEntityTagList.appendTag(nbtTagCompound);
			if(nbtTagCompound.hasKey("Items")) {
				NBTTagList list = nbtTagCompound.getTagList("Items", 10);
				for (int i = 0; i < list.tagCount(); i++) {
					NBTTagCompound item = (NBTTagCompound)list.getCompoundTagAt(i);
					if(item.getShort("id") == Constants.PLACEHOLDERITEM_ID) {
						item.setShort("id", (short)-1);
					}
				}
			}
		}*/
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
		} else {
			MLogger.logf("Failed to read interfaces for %s, %d", segment.getName(), interfaces.length);
		}
		
		// set block "ID"s and data
		segment.blocks = nbt.getIntArray(BLOCKS_KEY);
		segment.data = nbt.getByteArray(DATA_KEY);
		
		
		if(nbt.hasKey(MAP_KEY)) {
			try {
				
				// read mappings from nbt
				byte[] byteArray = nbt.getByteArray(MAP_KEY);
				ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);
				ObjectInputStream objectStream = new ObjectInputStream(byteStream);
				Map<Integer, String> nameMap = (Map<Integer, String>)objectStream.readObject();
				objectStream.close();
				
				// attempt to get block references from identifier
				for (Integer key : nameMap.keySet()) {
					UniqueIdentifier identifier = new UniqueIdentifier(nameMap.get(key));
					Block block = GameRegistry.findBlock(identifier.modId, identifier.name);
					if(block == null) {
						MLogger.logf("Could not find block for identifier %s, replacing with air.", identifier.toString());
						block = Blocks.air;
					}
					
					segment.blockMap.put(key, block);
				}
				
			} catch (IOException e) {
				MLogger.logf("Failed to read block map for segment %s", segment.getName());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				MLogger.logf("Failed to read block map for segment %s", segment.getName());
				e.printStackTrace();
			}
		}
		
		

		
		/*for (int i = 0; i < tileEntityList.tagCount(); i++) {
			tileEntityArray[i] = (NBTTagCompound)tileEntityList.tagAt(i);
			if(tileEntityArray[i].hasKey("Items")) {
				NBTTagList list = tileEntityArray[i].getTagList("Items");
				for (int j = 0; j < list.tagCount(); j++) {
					NBTTagCompound item = (NBTTagCompound)list.tagAt(j);
					if(item.getShort("id") == -1) {
						item.setShort("id", (short)Constants.PLACEHOLDERITEM_ID);
					}
				}
			}
		}
		segment.setTileEntityNBTs(tileEntityArray);
		
		for (int i = 0; i < entityList.tagCount(); i++) {
			entityArray[i] = (NBTTagCompound)entityList.tagAt(i);
		}*/
		

		return segment;
	}
	
	public void parseFromWorld(World world, int chunkX, int chunkY, int chunkZ) {
		int[] interfaces = new int[6];
		
		int xOffset = chunkX*16;
		int yOffset = chunkY;
		int zOffset = chunkZ*16;
		
		ArrayList<NBTTagCompound> tileEntityNBTList = new ArrayList<NBTTagCompound>();
		
		
		// set blocks
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					setBlock(x, y, z,
							world.getBlock(xOffset+x, yOffset+y, zOffset+z),
							world.getBlockMetadata(xOffset+x, yOffset+y, zOffset+z));
				}
			}
		}
		
		// set interfaces
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				// top
				if(world.getBlock(xOffset+i, yOffset+15, zOffset+j).equals(InterfaceBlock.getInstance())) {
					interfaces[0] += 1 + world.getBlockMetadata(xOffset+i, yOffset+15, zOffset+j);
				}
				// bottom
				if(world.getBlock(xOffset+i, yOffset, zOffset+j).equals(InterfaceBlock.getInstance())) {
					interfaces[1] += 1 + world.getBlockMetadata(xOffset+i, yOffset, zOffset+j);
				}
				
				// south
				if(world.getBlock(xOffset+i, yOffset+j, zOffset+15).equals(InterfaceBlock.getInstance())) {
					interfaces[2] += 1 + world.getBlockMetadata(xOffset+i, yOffset+j, zOffset+15);
				}
				// north
				if(world.getBlock(xOffset+i, yOffset+j, zOffset).equals(InterfaceBlock.getInstance())) {
					interfaces[4] += 1 + world.getBlockMetadata(xOffset+i, yOffset+j, zOffset);
				}
				
				// east
				if(world.getBlock(xOffset+15, yOffset+i, zOffset+j).equals(InterfaceBlock.getInstance())) {
					interfaces[3] += 1 + world.getBlockMetadata(xOffset+15, yOffset+i, zOffset+j);
				}
				// west
				if(world.getBlock(xOffset, yOffset+i, zOffset+j).equals(InterfaceBlock.getInstance())) {
					interfaces[5] += 1 + world.getBlockMetadata(xOffset, yOffset+i, zOffset+j);
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
					/*if(world.blockHasTileEntity(xOffset+x, yOffset+y, zOffset+z)) {
						NBTTagCompound tagCompound = new NBTTagCompound();
				        world.getBlockTileEntity(xOffset+x, yOffset+y, zOffset+z).writeToNBT(tagCompound);
				        tagCompound.setInteger("x", x);
				        tagCompound.setInteger("y", y);
				        tagCompound.setInteger("z", z);
				        tileEntityNBTList.add(tagCompound);
					}*/
					
				}
			}
		}
		setTileEntityNBTs(tileEntityNBTList.toArray(new NBTTagCompound[tileEntityNBTList.size()]));
		
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

}
