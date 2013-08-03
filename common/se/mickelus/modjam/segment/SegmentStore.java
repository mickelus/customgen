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
	
	static {
		segmentCollections = new ArrayList<>();
	}

	public static Segment getSegment(int top, int bottom, int north, int south, int east, int west, int type) {
		Segment result = null;
		
		for (SegmentCollection segmentCollection : segmentCollections) {
			for (int i = 0; i < segmentCollection.getNumSegments(); i++) {
				Segment segment = segmentCollection.getSegment(i);
				
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
				
			}
		}
		
		
		return result;
	}
	
	public static void registerSegmentCollection(SegmentCollection collection) {
		segmentCollections.add(collection);
	}
	
	
	private static SegmentCollection generateTestCollection() {
		SegmentCollection collection = new SegmentCollection(0);
		
		return collection;
	}
	
	public static void loadDataFiles(){
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
