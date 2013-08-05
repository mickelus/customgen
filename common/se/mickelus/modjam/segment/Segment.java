package se.mickelus.modjam.segment;

public class Segment {
	
	public final static int TYPE_NORMAL = 0;
	public final static int TYPE_START = 1;
	public final static int TYPE_LOOT = 2;
	
	
	
	private int[] blocks;
	private int[] data;
	
	public int interfaceTop;
	public int interfaceBottom;
	
	public int interfaceNorth;
	public int interfaceSouth;
	
	public int interfaceEast;
	public int interfaceWest;
	
	int type;
	
	
	public Segment(int[] blocks, int[] data, int top, int bottom, int north, int south, int east, int west, int type) {
		this.blocks = blocks;
		this.data = data;
		
		interfaceTop = top;
		interfaceBottom = bottom;
		
		interfaceNorth = north;
		interfaceSouth = south;
		
		interfaceEast = east;
		interfaceWest = west;
		
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
	
	public int getType() {
		return type;
	}
	
	public int getBlockID(int x, int y, int z) {
		return blocks[x+z*16+y*256];
	}
	
	public int getBlockData(int x, int y, int z) {
		return data[x+z*16+y*256];
	}


}
