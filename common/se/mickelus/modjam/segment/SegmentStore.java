package se.mickelus.modjam.segment;

import java.util.ArrayList;

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
}
