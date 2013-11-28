package se.mickelus.customgen.segment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import se.mickelus.customgen.Constants;

public class SegmentStore {
	
	private static ArrayList<SegmentCollection> segmentCollections;

		
	public static void init() {
		segmentCollections = new ArrayList<SegmentCollection>();
		loadDataFiles();		
	}
	
	public static void registerSegmentCollection(SegmentCollection collection) {
		segmentCollections.add(collection);
	}
	
	
	private static SegmentCollection generateTestCollection() {
		SegmentCollection collection = new SegmentCollection(0);
		
		return collection;
	}
	
	public static Segment getSegment(int top, int bottom, int north, int south, int east, int west, int type, Random random) {
		for (SegmentCollection segmentCollection : segmentCollections) {
			int size = segmentCollection.getNumSegments();
			int offset = random.nextInt(size);
			for (int i = 0; i < segmentCollection.getNumSegments(); i++) {
				Segment segment = segmentCollection.getSegment((i+offset)%size);				
				if(isSegmentMatching(segment, top, bottom, north, south, east, west, type)) {
					return segment;
				}
				
			}
		}
		System.out.println(segmentCollections.size());
		return null;
	}
	
	public static Segment getSegment(SegmentPlaceholder ph, Random random) {
		return getSegment(ph.getInterfaceTop(), ph.getInterfaceBottom(), 
				ph.getInterfaceNorth(), ph.getInterfaceSouth(),
				ph.getInterfaceEast(), ph.getInterfaceWest(), ph.getType(), random);
	}
	
	private static boolean isSegmentMatching(Segment segment, int top, int bottom, int north, int south, int east, int west, int type) {
		if(
				type != segment.getType() ||
				top != 0 && top != segment.getInterfaceTop() ||
				bottom != 0 && bottom != segment.getInterfaceBottom() ||
				north != 0 && north != segment.getInterfaceNorth() ||
				south != 0 && south != segment.getInterfaceSouth() ||
				east != 0 && east != segment.getInterfaceEast() ||
				west != 0 && west != segment.getInterfaceWest() 
		) {
			return false;
		}
		
		return true;
	}
	
	private static void loadDataFiles(){
		File dataDir = new File(Constants.SAVE_PATH);
		
		if(!dataDir.exists()){
			System.out.println("Data folder does not exists.");
			return;
		}
		
		File[] fileList = dataDir.listFiles();
		File dataFile = null;
		FileInputStream filein = null;
		NBTTagCompound nbt = null;
		
		for(int i = 0; i < fileList.length; i++) {
			if(fileList[i].isFile()) {
				SegmentCollection sCollection = new SegmentCollection(0);
				try {
					dataFile = fileList[i];
					filein = new FileInputStream(dataFile);
					nbt = CompressedStreamTools.readCompressed(filein);
					System.out.println("NBT: " + nbt);
					Collection nbtCollection = nbt.getTags();
					for (Object tag : nbtCollection) {
						NBTTagCompound nbtTag = (NBTTagCompound) tag;
						Segment segment = new Segment(
								nbtTag.getIntArray("blocks"),
								nbtTag.getIntArray("data"),
								nbtTag.getInteger("top"), 
								nbtTag.getInteger("bottom"), 
								nbtTag.getInteger("north"), 
								nbtTag.getInteger("south"), 
								nbtTag.getInteger("east"), 
								nbtTag.getInteger("west"), 
								nbtTag.getInteger("type"),
								nbtTag.getTagList("tileEntities"));
						sCollection.addSegment(segment);
						System.out.println(String.format("%d %d %d %d %d %d %d", 
								segment.getInterfaceTop(), segment.getInterfaceBottom(), segment.getInterfaceNorth(),
								segment.getInterfaceSouth(), segment.getInterfaceEast(), segment.getInterfaceWest(), segment.getType()));
					}
					registerSegmentCollection(sCollection);
				}
				catch(Exception e) {
					System.err.println("WRNg WRNg");
				}
			}
		}
	}
	
	public static void addSegment(String segmentCollectionName, String segmentName, World world, int x, int y, int z) {
		
		/* check for directory, create if non-existant */
		File dir = new File(Constants.SAVE_PATH);
		System.out.println(dir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		File file = MinecraftServer.getServer().getFile(Constants.SAVE_PATH + segmentCollectionName + "." + Constants.FILE_EXT);
		System.out.println(file);
		
		NBTTagCompound nbt = null;
		if(!file.exists()){
			try {
				file.createNewFile();
				nbt = new NBTTagCompound();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		else {
			try {
				FileInputStream filein = new FileInputStream(file);
				nbt = CompressedStreamTools.readCompressed(filein);
				filein.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
		
			int[] blockIDs = new int[4096];
			int[] metadata = new int[4096];
			
			
			NBTTagList tileEntityList = new NBTTagList("tileEntities");	

			int west = 0;
			int east = 0;
			int top = 0;
			int bottom = 0;
			int south = 0;
			int north = 0;
			
			for(int sy = 0; sy < 16; sy++) {
				for(int sz = 0; sz < 16; sz++) {
					for(int sx = 0; sx < 16; sx++) {
						int blockID = world.getBlockId(x+sx, y+sy, z+sz);
						int blockData = world.getBlockMetadata(x+sx, y+sy, z+sz);
						if(world.blockHasTileEntity(x+sx, y+sy, z+sz)) {
							NBTTagCompound tagCompound = new NBTTagCompound();
							world.getBlockTileEntity(x+sx, y+sy, z+sz).writeToNBT(tagCompound);
							tagCompound.setInteger("x", sx);
							tagCompound.setInteger("y", sy);
							tagCompound.setInteger("z", sz);
							tileEntityList.appendTag(tagCompound);
							System.out.println("TAGCOMPOUND:");
							System.out.println(tagCompound);
						}
						
						if(blockID == Constants.EMPTY_ID || blockID == Constants.INTERFACEBLOCK_ID) {
							blockID = -1;
						}
						//System.out.println(String.format("save  x:%d y:%d z:%d i:%d id:%d", x+sx, y+sy, z+sz, sx+sz*16+sy*256, blockID));
						blockIDs[(sx+sz*16+sy*256)] = blockID;
						metadata[(sx+sz*16+sy*256)] = blockData;
					}
				}
			}
			
			NBTTagCompound nbtSegment = new NBTTagCompound();
			nbtSegment.setIntArray("blocks", blockIDs);
			nbtSegment.setIntArray("data", metadata);
			nbtSegment.setInteger("west", west);
			nbtSegment.setInteger("east", east);
			nbtSegment.setInteger("top", top);
			nbtSegment.setInteger("bottom", bottom);
			nbtSegment.setInteger("south", south);
			nbtSegment.setInteger("north", north);
			nbtSegment.setTag("tileEntities", tileEntityList);
			
			System.out.println(nbtSegment);
			
			nbt.setCompoundTag(segmentName, nbtSegment);
		
			FileOutputStream fileos = new FileOutputStream(file);
			CompressedStreamTools.writeCompressed(nbt, fileos);
			fileos.close();
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
}
