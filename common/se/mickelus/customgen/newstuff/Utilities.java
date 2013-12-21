package se.mickelus.customgen.newstuff;

import java.util.ArrayList;

import net.minecraft.world.World;

import se.mickelus.customgen.segment.Segment;

public class Utilities {
	
	public static final int CORNER_TEMPLATE = 0;
	public static final int EDGE_TEMPLATE = 1;
	public static final int FACE_TEMPLATE = 2;
	public static final int FILL_TEMPLATE = 3;

	private static ArrayList<Segment> templates;
	
	static {
		templates = new ArrayList<Segment>();
		
		Segment corner = new Segment("corner");
		
		// bottom corners
		corner.setBlock(0, 0, 1, -1, 0);
		corner.setBlock(1, 0, 0, -1, 0);
		corner.setBlock(0, 0, 0, -1, 0);
		corner.setBlock(0, 1, 0, -1, 0);
		
		corner.setBlock(15, 0, 1, -1, 0);
		corner.setBlock(14, 0, 0, -1, 0);
		corner.setBlock(15, 0, 0, -1, 0);
		corner.setBlock(15, 1, 0, -1, 0);
		
		corner.setBlock(0, 0, 14, -1, 0);
		corner.setBlock(1, 0, 15, -1, 0);
		corner.setBlock(0, 0, 15, -1, 0);
		corner.setBlock(0, 1, 15, -1, 0);
		
		corner.setBlock(15, 0, 14, -1, 0);
		corner.setBlock(14, 0, 15, -1, 0);
		corner.setBlock(15, 0, 15, -1, 0);
		corner.setBlock(15, 1, 15, -1, 0);
		
		// top corners
		
		corner.setBlock(0, 15, 1, -1, 0);
		corner.setBlock(1, 15, 0, -1, 0);
		corner.setBlock(0, 15, 0, -1, 0);
		corner.setBlock(0, 14, 0, -1, 0);
		
		corner.setBlock(15, 15, 1, -1, 0);
		corner.setBlock(14, 15, 0, -1, 0);
		corner.setBlock(15, 15, 0, -1, 0);
		corner.setBlock(15, 14, 0, -1, 0);
		
		corner.setBlock(0, 15, 14, -1, 0);
		corner.setBlock(1, 15, 15, -1, 0);
		corner.setBlock(0, 15, 15, -1, 0);
		corner.setBlock(0, 14, 15, -1, 0);
		
		corner.setBlock(15, 15, 14, -1, 0);
		corner.setBlock(14, 15, 15, -1, 0);
		corner.setBlock(15, 15, 15, -1, 0);
		corner.setBlock(15, 14, 15, -1, 0);
		
		templates.add(CORNER_TEMPLATE, corner);
		
		
		Segment edge = new Segment("edge");
		for (int i = 0; i < 16; i++) {
			edge.setBlock(0, i, 0, -1, 0);
			edge.setBlock(15, i, 0, -1, 0);
			edge.setBlock(0, i, 15, -1, 0);
			edge.setBlock(15, i, 15, -1, 0);
			
			edge.setBlock(i, 0, 0, -1, 0);
			edge.setBlock(i, 15, 0, -1, 0);
			edge.setBlock(i, 0, 15, -1, 0);
			edge.setBlock(i, 15, 15, -1, 0);
			
			edge.setBlock(0, 0, i, -1, 0);
			edge.setBlock(15, 0, i, -1, 0);
			edge.setBlock(0, 15, i, -1, 0);
			edge.setBlock(15, 15, i, -1, 0);
		}
		
		templates.add(EDGE_TEMPLATE, edge);
		
		
		Segment face = new Segment("face");
		
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				face.setBlock(0, i, j, -1, 0);
				face.setBlock(i, 0, j, -1, 0);
				face.setBlock(i, j, 0, -1, 0);
				
				face.setBlock(15, i, j, -1, 0);
				face.setBlock(i, 15, j, -1, 0);
				face.setBlock(i, j, 15, -1, 0);
			}
		}
		templates.add(FACE_TEMPLATE, face);
		
		Segment fill = new Segment("fill");
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					fill.setBlock(x, y, z, -1, 0);
				}
			}
		}
		templates.add(FILL_TEMPLATE, fill);
		
	}
	
	public static void generateTemplate(int chunkX, int chunkZ, int y, World world, int templateID) {
		ForgeGenerator.getInstance().generateSegment(chunkX, chunkZ, y, templates.get(templateID), world, true);
	}
}
