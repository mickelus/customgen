package se.mickelus.modjam.segment;

import java.util.ArrayList;

public class SegmentCollection {

	private int offset;
	
	private ArrayList<Segment> segments;
	
	public SegmentCollection(int offset) {
		this.segments = new ArrayList<Segment>();
		this.offset = offset;
	}
	
	public void addSegment(Segment segment) {
		segments.add(segment);
	}
	
	public int getNumSegments() {
		return segments.size();
	}
	
	public Segment getSegment(int index) {
		return segments.get(index);
	}
}
