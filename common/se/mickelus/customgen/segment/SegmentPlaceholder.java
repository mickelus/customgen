package se.mickelus.customgen.segment;

public class SegmentPlaceholder {

	
	// yes, this is ugly
	private int[] interfaces;
	
	private int segmentX;
	private int segmentY;
	private int segmentZ;
	
	private boolean occupied = false;
		
	public SegmentPlaceholder(int x, int y, int z, int[] interfaces) {
		this.interfaces = interfaces; 
		
		segmentX = x;
		segmentY = y;
		segmentZ = z;
	}
	
	public SegmentPlaceholder(int x, int y, int z) {
		this(x, y, z, new int[6]);
	}


	public int getInterface(int side) {
		return interfaces[side];
	}
	
	public int[] getInterfaces() {
		return interfaces.clone();
	}

	public void setInterface(int side, int value) {
		interfaces[side] = value;
	}


	public int getX() {
		return segmentX;
	}


	public int getY() {
		return segmentY;
	}


	public int getZ() {
		return segmentZ;
	}
	
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
	
	public boolean isOccupied() {
		return occupied;
	}
	
}
