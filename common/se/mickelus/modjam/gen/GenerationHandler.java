package se.mickelus.modjam.gen;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

public class GenerationHandler implements IWorldGenerator {
	
	ArrayList<int[]> pendingSegments;
	
	
	public GenerationHandler() {
		GameRegistry.registerWorldGenerator(this);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		world.setBlock(chunkX*16+1, 120, chunkZ*16+1, 1);
		
		System.out.println("GEN: " + (chunkX) + ", " + (chunkZ));
	}
	
	
	private void generateStart() {
		
	}

}
