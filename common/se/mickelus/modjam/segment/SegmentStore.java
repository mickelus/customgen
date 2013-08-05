package se.mickelus.modjam.segment;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import se.mickelus.modjam.Constants;

public class SegmentStore {
	
	private static ArrayList<SegmentCollection> segmentCollections;

		
	public static void init() {
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
		System.out.println("INIT SEGMENT STORE");
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
								nbtTag.getInteger("type"));
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
}
