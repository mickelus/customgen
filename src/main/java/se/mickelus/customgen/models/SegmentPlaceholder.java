package se.mickelus.customgen.models;

import java.util.Arrays;

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
		interfaces = new int[6];
		
		Arrays.fill(interfaces, -1);
		
		segmentX = x;
		segmentY = y;
		segmentZ = z;
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
	
	public boolean hasProperInterfaces() {
		
		for (int i = 0; i < interfaces.length; i++) {
			if(interfaces[i] > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String string = "PH:[";
		for (int i = 0; i < interfaces.length; i++) {
			if(interfaces[i] != -1) {
				string += String.format("%2d", interfaces[i]);
			} else {
				string += "  ";
			}
			
			if(i != interfaces.length-1) {
				string += ",";
			}
		}
		string += "] [" + getX() + "," + getY() + "," + getZ() + "] ";
		
		string += isOccupied() ? "occupied" : "unoccupied";
		return string;
	}
	
}
