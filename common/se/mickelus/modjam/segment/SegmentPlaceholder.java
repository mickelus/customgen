package se.mickelus.modjam.segment;

public class SegmentPlaceholder {

	
	// yes, this is ugly
	private int interfaceTop;
	private int interfaceBottom;
	
	private int interfaceNorth;
	private int interfaceSouth;
	
	private int interfaceEast;
	private int interfaceWest;
	
	private int segmentX;
	private int segmentY;
	private int segmentZ;
	
	private int type;
	
	
	public SegmentPlaceholder(int x, int y, int z, int top, int bottom, int north, int south, int east, int west, int type) {
		interfaceTop = top;
		interfaceBottom = bottom;
		
		interfaceNorth = north;
		interfaceSouth = west;
		
		interfaceEast = east;
		interfaceWest = west;
		
		segmentX = x;
		segmentY = y;
		segmentZ = z;
		
		this.type = type;
	}


	public int getInterfaceTop() {
		return interfaceTop;
	}


	public int getInterfaceBottom() {
		return interfaceBottom;
	}


	public int getInterfaceNorth() {
		return interfaceNorth;
	}


	public int getInterfaceSouth() {
		return interfaceSouth;
	}


	public int getInterfaceEast() {
		return interfaceEast;
	}


	public int getInterfaceWest() {
		return interfaceWest;
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


	public int getType() {
		return type;
	}
	
	
	
}
