package se.mickelus.modjam.segment;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import se.mickelus.modjam.Constants;

public class SegmentStore {
	
	private static ArrayList<SegmentCollection> segmentCollections;

	public static Segment getSegment(int top, int bottom, int north, int south, int east, int west, int type) {		
		for (SegmentCollection segmentCollection : segmentCollections) {
			for (int i = 0; i < segmentCollection.getNumSegments(); i++) {
				Segment segment = segmentCollection.getSegment(i);
				System.out.println(String.format("%d %d %d %d %d %d %d", 
						top, bottom, north, south, east, west, type));
				System.out.println(String.format("%d %d %d %d %d %d %d", 
						segment.getInterfaceTop(), segment.getInterfaceBottom(), segment.getInterfaceNorth(),
						segment.getInterfaceSouth(), segment.getInterfaceEast(), segment.getInterfaceWest(), segment.getType()));
				
				
				if(
						type != segment.getType() ||
						top != 0 && top != segment.getInterfaceTop() ||
						bottom != 0 && bottom != segment.getInterfaceBottom() ||
						north != 0 && north != segment.getInterfaceNorth() ||
						south != 0 && south != segment.getInterfaceSouth() ||
						east != 0 && east != segment.getInterfaceEast() ||
						west != 0 && west != segment.getInterfaceWest() 
				) {
					continue;
				}
				return segment;
				
			}
		}
		
		System.out.println("could not find proper segment");
		System.out.println(segmentCollections.size());
		return null;
	}
	
	public static Segment getSegment(SegmentPlaceholder ph) {
		return getSegment(ph.getInterfaceTop(), ph.getInterfaceBottom(), 
				ph.getInterfaceNorth(), ph.getInterfaceSouth(),
				ph.getInterfaceEast(), ph.getInterfaceWest(), ph.getType());
	}
	
	public static void init() {
		segmentCollections = new ArrayList<>();
		loadDataFiles();		
	}
	
	public static void registerSegmentCollection(SegmentCollection collection) {
		segmentCollections.add(collection);
	}
	
	
	private static SegmentCollection generateTestCollection() {
		SegmentCollection collection = new SegmentCollection(0);
		
		return collection;
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
					Collection nbtCollection = nbt.getTags();
					for (Object tag : nbtCollection) {
						NBTTagCompound nbtTag = (NBTTagCompound) tag;
						sCollection.addSegment(new Segment(
												nbtTag.getIntArray("blocks"), 
												nbtTag.getInteger("top"), 
												nbtTag.getInteger("bottom"), 
												nbtTag.getInteger("north"), 
												nbtTag.getInteger("south"), 
												nbtTag.getInteger("east"), 
												nbtTag.getInteger("west"), 
												nbtTag.getInteger("type")));
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
