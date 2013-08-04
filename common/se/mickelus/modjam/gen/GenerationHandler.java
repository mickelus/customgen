package se.mickelus.modjam.gen;

import java.util.ArrayList;
import java.util.Random;

import se.mickelus.modjam.Constants;
import se.mickelus.modjam.segment.Segment;
import se.mickelus.modjam.segment.SegmentPlaceholder;
import se.mickelus.modjam.segment.SegmentStore;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

public class GenerationHandler implements IWorldGenerator {
	
	ArrayList<SegmentPlaceholder> pendingSegments;
	
	
	public GenerationHandler() {
		
		pendingSegments = new ArrayList<SegmentPlaceholder>();
		GameRegistry.registerWorldGenerator(this);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		//world.setBlock(chunkX*16+1, 120, chunkZ*16+1, 1);
		
		boolean pendingGeneration;
		
		// randomly spawn a start segment
		if(random.nextInt(Constants.DUNGEON_CHANCE) == 0) {
			
			// randomly select a height
			int y = random.nextInt(3);
			System.out.println("GENERATED START AT :" + chunkX + ", " + y + ", " + chunkZ);
			
			// add placeholder to pending-list
			pendingSegments.add(new SegmentPlaceholder(chunkX, y, chunkZ, 0, 0, 0, 0, 0, 0, Segment.TYPE_START));	
		}
		
		
		// while there are placeholders for un-generated chunks
		do {
			pendingGeneration = false;
			// iterate over all placeholders
			for (SegmentPlaceholder ph : pendingSegments) {
				// if placeholder is in generated chunk
				if(chunkProvider.chunkExists(ph.getX(), ph.getY())) {
					pendingGeneration = true;
					// generate segment based on placeholder
					generateSegment(ph, world);
				}
			}
				
		} while(pendingGeneration);	
			
		
		
		boolean generated = false;
		// is segment generation pending in this chunk?
		for (SegmentPlaceholder	segment : pendingSegments) {
			if(segment.getX() == chunkX && segment.getZ() == chunkZ) {
				generateSegment(segment, world);
				generated = true;
			}
		}
		
		
		
	}
	
	
	private void generateSegment(SegmentPlaceholder placeholder, World world) {
		// ask the segmentstore for a segment matching the placeholder
		Segment segment = SegmentStore.getSegment(placeholder);
		
		// get segment position
		int x = placeholder.getX() * 16;
		int y = placeholder.getY() * 16;
		int z = placeholder.getZ() * 16;
		
		
		// TODO : we should not have to handle this
		if(segment == null) {
			System.out.println("Unable to generate segment");
			return;
		}
		System.out.println("GENERATING SEGMENT AT :" + x + ", " + y + ", " + z);
		
		// generate blocks
		for(int sx = 0; sx < 16; sx++) {
			for(int sy = 0; sy < 16; sy++) {
				for(int sz = 0; sz < 16; sz++) {
					world.setBlock(x+sx, y+sy+4, z+sz, segment.getBlockID(sx, sy, sz));
				}
			}
		}
		
		// spawn tile entities
		
		
		// spawn entities
		
		
		// updated pending segments based on this segments interfaces
		updatePendingSegments(placeholder);
		
		
	}
	
	/**
	 * Check if the chunk at the given chunk coordinates has pending segments
	 * @param chunkX
	 * 		Chunk x coordinate
	 * @param chunkZ
	 * 		Chunk y coordinate
	 * @return
	 * 		True if there are pending segments in this chunk, otherwise false.
	 */
	private boolean chunkHasPending(int chunkX, int chunkZ) {
		for (SegmentPlaceholder ph : pendingSegments) {
			if(ph.getX() == chunkX && ph.getZ() == chunkZ) {
				return true;
			}
		}
		
		return false;
	}
	
	private void updatePendingSegments(int x, int y, int z, int top, int bottom, int north, int south, int east, int west) {
		getPlaceholder(x, y+1, z).setInterfaceBottom(top);
		getPlaceholder(x, y-1, z).setInterfaceTop(bottom);
		getPlaceholder(x, y, z-1).setInterfaceSouth(north);
		getPlaceholder(x, y+1, z+1).setInterfaceNorth(south);
		getPlaceholder(x+1, y+1, z).setInterfaceWest(east);
		getPlaceholder(x-1, y+1, z).setInterfaceEast(west);
	}
	
	private void updatePendingSegments(SegmentPlaceholder ph) {
		updatePendingSegments(ph.getX(), ph.getY(), ph.getZ(), 
				ph.getInterfaceTop(), ph.getInterfaceBottom(), 
				ph.getInterfaceNorth(), ph.getInterfaceSouth(),
				ph.getInterfaceEast(), ph.getInterfaceWest());
	}
	
	private SegmentPlaceholder getPlaceholder(int x, int y, int z) {		
		for (SegmentPlaceholder ph : pendingSegments) {
			if(ph.getX() == x && ph.getY() == y && ph.getX() == z) {
				return ph;
			}
		}
		
		SegmentPlaceholder ph = new SegmentPlaceholder(x, y, z, 0, 0, 0, 0, 0, 0, 0);
		pendingSegments.add(ph);
		
		return ph;
	}

	
	
	
	
	
	
	
	
}
