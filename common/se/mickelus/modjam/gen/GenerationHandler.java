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
		
		boolean generated = false;
		// is segment generation pending in this chunk?
		for (SegmentPlaceholder	segment : pendingSegments) {
			if(segment.getX() == chunkX && segment.getZ() == chunkZ) {
				generateSegment(segment, world);
				generated = true;
			}
		}
		
		// generate dungeon start
		if(!generated && random.nextInt(Constants.DUNGEON_CHANCE) == 0) {
			int y = random.nextInt(4)+1;
			System.out.println("GENERATED START AT :" + chunkX + ", " + y + ", " + chunkZ);
			generateSegment(new SegmentPlaceholder(chunkX, y, chunkZ, 0, 0, 0, 0, 0, 0, Segment.TYPE_START), world);		
		}	
		
	}
	
	
	private void generateSegment(SegmentPlaceholder placeholder, World world) {
		Segment segment = SegmentStore.getSegment(placeholder);
		int x = placeholder.getX() * 16;
		int y = placeholder.getY() * 16;
		int z = placeholder.getZ() * 16;
		
		if(segment == null) {
			System.out.println("Unable to generate segment");
			return;
		}
		
		System.out.println("GENERATING SEGMENT AT :" + x + ", " + y + ", " + z);
		
		for(int sx = 0; sx < 16; sx++) {
			for(int sy = 0; sy < 16; sy++) {
				for(int sz = 0; sz < 16; sz++) {
					world.setBlock(x+sx, y+sy, z+sz, segment.getBlockID(sx, sy, sz));
				}
			}
		}
		
	}
	
	private boolean isPending(int chunkX, int chunkZ) {
		boolean result = false;
		
		
		
		return result;
	}

}
