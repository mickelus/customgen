package se.mickelus.customgen.newstuff;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import se.mickelus.customgen.blocks.EmptyBlock;
import se.mickelus.customgen.segment.Segment;

public class Utilities {
	
	public static final int CORNER_TEMPLATE = 0;
	public static final int EDGE_TEMPLATE = 1;
	public static final int FACE_TEMPLATE = 2;
	public static final int FILL_TEMPLATE = 3;
	public static final int SURFACE_TEMPLATE = 4;

	private static ArrayList<Segment> templates;
	
	static {
		templates = new ArrayList<Segment>();
		
		Segment corner = new Segment("corner");
		
		EmptyBlock block = EmptyBlock.getInstance();
		
		// bottom corners
		corner.setBlock(0, 0, 1, block, 0);
		corner.setBlock(1, 0, 0, block, 0);
		corner.setBlock(0, 0, 0, block, 0);
		corner.setBlock(0, 1, 0, block, 0);
		
		corner.setBlock(15, 0, 1, block, 0);
		corner.setBlock(14, 0, 0, block, 0);
		corner.setBlock(15, 0, 0, block, 0);
		corner.setBlock(15, 1, 0, block, 0);
		
		corner.setBlock(0, 0, 14, block, 0);
		corner.setBlock(1, 0, 15, block, 0);
		corner.setBlock(0, 0, 15, block, 0);
		corner.setBlock(0, 1, 15, block, 0);
		
		corner.setBlock(15, 0, 14, block, 0);
		corner.setBlock(14, 0, 15, block, 0);
		corner.setBlock(15, 0, 15, block, 0);
		corner.setBlock(15, 1, 15, block, 0);
		
		// top corners
		
		corner.setBlock(0, 15, 1, block, 0);
		corner.setBlock(1, 15, 0, block, 0);
		corner.setBlock(0, 15, 0, block, 0);
		corner.setBlock(0, 14, 0, block, 0);
		
		corner.setBlock(15, 15, 1, block, 0);
		corner.setBlock(14, 15, 0, block, 0);
		corner.setBlock(15, 15, 0, block, 0);
		corner.setBlock(15, 14, 0, block, 0);
		
		corner.setBlock(0, 15, 14, block, 0);
		corner.setBlock(1, 15, 15, block, 0);
		corner.setBlock(0, 15, 15, block, 0);
		corner.setBlock(0, 14, 15, block, 0);
		
		corner.setBlock(15, 15, 14, block, 0);
		corner.setBlock(14, 15, 15, block, 0);
		corner.setBlock(15, 15, 15, block, 0);
		corner.setBlock(15, 14, 15, block, 0);
		
		templates.add(CORNER_TEMPLATE, corner);
		
		
		Segment edge = new Segment("edge");
		for (int i = 0; i < 16; i++) {
			edge.setBlock(0, i, 0, block, 0);
			edge.setBlock(15, i, 0, block, 0);
			edge.setBlock(0, i, 15, block, 0);
			edge.setBlock(15, i, 15, block, 0);
			
			edge.setBlock(i, 0, 0, block, 0);
			edge.setBlock(i, 15, 0, block, 0);
			edge.setBlock(i, 0, 15, block, 0);
			edge.setBlock(i, 15, 15, block, 0);
			
			edge.setBlock(0, 0, i, block, 0);
			edge.setBlock(15, 0, i, block, 0);
			edge.setBlock(0, 15, i, block, 0);
			edge.setBlock(15, 15, i, block, 0);
		}
		
		templates.add(EDGE_TEMPLATE, edge);
		
		
		Segment face = new Segment("face");
		
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				face.setBlock(0, i, j, block, 0);
				face.setBlock(i, 0, j, block, 0);
				face.setBlock(i, j, 0, block, 0);
				
				face.setBlock(15, i, j, block, 0);
				face.setBlock(i, 15, j, block, 0);
				face.setBlock(i, j, 15, block, 0);
			}
		}
		templates.add(FACE_TEMPLATE, face);
		
		Segment fill = new Segment("fill");
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					fill.setBlock(x, y, z, block, 0);
				}
			}
		}
		templates.add(FILL_TEMPLATE, fill);
		
		Segment surface = new Segment("surface");
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 16; z++) {
					surface.setBlock(x, y, z, block, 0);
				}
			}
		}
		templates.add(SURFACE_TEMPLATE, surface);
		
	}
	
	public static void generateTemplate(int chunkX, int chunkZ, int y, World world, int templateID) {
		ForgeGenerator.getInstance().generateSegment(chunkX, chunkZ, y, templates.get(templateID), world, true, new Random());
	}
}
