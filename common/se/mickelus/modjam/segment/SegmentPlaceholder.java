package se.mickelus.modjam.segment;

public class SegmentPlaceholder {

	
	// yes, this is ugly
	public int interfaceTop;
	public int interfaceBottom;
	
	public int interfaceNorth;
	public int interfaceSouth;
	
	public int interfaceEast;
	public int interfaceWest;
	
	public int segmentX;
	public int segmentY;
	public int segmentZ;
	
	public int type;
	
	
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


	public int getSegmentX() {
		return segmentX;
	}


	public int getSegmentY() {
		return segmentY;
	}


	public int getSegmentZ() {
		return segmentZ;
	}


	public int getType() {
		return type;
	}
	
	
	
}
