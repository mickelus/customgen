package se.mickelus.customgen.newstuff;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.segment.Segment;
import se.mickelus.customgen.segment.SegmentPlaceholder;

public class ForgeGenerator implements IWorldGenerator  {
	
	private static ForgeGenerator instance;
	
	List<SegmentPlaceholder> pendingSegments;
	
	
	public ForgeGenerator() {
		
		pendingSegments = new ArrayList<SegmentPlaceholder>();
		GameRegistry.registerWorldGenerator(this);
		
		instance = this;
	}
	
	public static ForgeGenerator getInstance() {
		return instance;
	}

	public void generateSegment(int chunkX, int chunkZ, int y, Segment segment, World world, boolean generatePlaceholders) {
		
		MLogger.log("generating segment");
		MLogger.logf("gph %b", generatePlaceholders);
		
		int x = chunkX * 16;
		int z = chunkZ * 16;
		MLogger.logf("%d %d %d", x, y, z);
		
		// TODO : we should not have to handle this
		if(segment == null) {
			System.out.println("Unable to generate segment");
			return;
		}

		// generate blocks
		for(int sy = 0; sy < 16; sy++) {
			for(int sz = 0; sz < 16; sz++) {
				for(int sx = 0; sx < 16; sx++) {
					int blockID = segment.getBlockID(sx, sy, sz);
					
					if(generatePlaceholders) {
						switch(blockID) {
							case -1:
								blockID = Constants.EMPTY_ID;
								break;
							case -2:
								blockID = Constants.INTERFACEBLOCK_ID;
								break;
									
							case -3:
								
								break;
						}
					}
					
					if(blockID >= 0) {
						world.setBlock(x+sx, y+sy, z+sz, blockID, segment.getBlockData(sx, sy, sz), 2);
					}
						
						
				}
			}
		}
		
		// spawn tile entities
		/*for (int i = 0; i < segment.getNumTileEntities(); i++) {
			NBTTagCompound tag = updateTileEntityNBT(segment.getTileEntityNBT(i), x, y, z);
			TileEntity tileEntity = TileEntity.createAndLoadEntity(tag);
			
			if (tileEntity != null) {
                world.getChunkFromBlockCoords(x, z).addTileEntity(tileEntity);
            }
		}*/
	}
	
	private void createPlaceholders(int chunkX, int chunkZ, int y, Segment segment) {
		
	}
	
	public void generateGen(int chunkX, int chunkZ, int y, Gen gen) {
		
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		// TODO Auto-generated method stub
		
	}
}
