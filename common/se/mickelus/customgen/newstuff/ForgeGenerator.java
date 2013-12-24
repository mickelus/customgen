package se.mickelus.customgen.newstuff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
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
		
		int x = chunkX * 16;
		int z = chunkZ * 16;
		MLogger.logf("%d %d %d", x, y, z);
		
		// TODO : we should not have to handle this
		if(segment == null) {
			MLogger.log("Unable to generate segment");
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
	
	private void createPlaceholders(int chunkX, int chunkZ, int y, Segment segment, 
			List<SegmentPlaceholder> placeholderList) {
		
		// if a ph exists for the given coordinates
		boolean exists = false;
		
		// if a ph exists for each side
		boolean [] sideExists = new boolean[6];
		
		// make sure we wont add ph:s for non-interfacing sides
		for (int i = 0; i < sideExists.length; i++) {
			sideExists[i] = segment.getInterface(i) == 0;
		}
		/*	   0 - top
		 *     1 - bottom
		 *     2 - north
		 *     3 - east
		 *     4 - south
		 *     5 - west*/
		// iterate over all placeholders, update those that are next to the given coordinates
		for (SegmentPlaceholder ph : placeholderList) {
			if(ph.getX() == chunkX + 1 
					&& ph.getZ() == chunkZ
					&& ph.getY() == y) {
				sideExists[3] = true;
				ph.setInterface(5, segment.getInterface(3));
				continue;
			} else if(ph.getX() == chunkX - 1
					&& ph.getZ() == chunkZ
					&& ph.getY() == y) {
				sideExists[5] = true;
				ph.setInterface(3, segment.getInterface(5));
				continue;
			} else if(ph.getX() == chunkX
					&& ph.getZ() == chunkZ + 1
					&& ph.getY() == y) {
				sideExists[4] = true;
				ph.setInterface(2, segment.getInterface(4));
				continue;
			} else if(ph.getX() == chunkX
					&& ph.getZ() == chunkZ - 1
					&& ph.getY() == y) {
				sideExists[2] = true;
				ph.setInterface(4, segment.getInterface(2));
				continue;
			} else if(ph.getX() == chunkX
					&& ph.getZ() == chunkZ
					&& ph.getY() == y + 1) {
				sideExists[0] = true;
				ph.setInterface(1, segment.getInterface(0));
				continue;
			} else if(ph.getX() == chunkX
					&& ph.getZ() == chunkZ
					&& ph.getY() == y - 1) {
				sideExists[1] = true;
				ph.setInterface(0, segment.getInterface(1));
				continue;
			} else if(ph.getX() == chunkX
					&& ph.getZ() == chunkZ
					&& ph.getY() == y) {
				exists = true;
				ph.setOccupied(true);
				continue;
			} 
		}
		
		
		// add placeholders to list for unoccupied, interfaced sides
		if(!sideExists[3]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX + 1, y, chunkZ);
			ph.setInterface(5, segment.getInterface(3));
			placeholderList.add(ph);
		}
		if(!sideExists[5]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX - 1, y, chunkZ);
			ph.setInterface(3, segment.getInterface(5));
			placeholderList.add(ph);
		}
		if(!sideExists[2]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX, y, chunkZ - 1);
			ph.setInterface(4, segment.getInterface(2));
			placeholderList.add(ph);
		}
		if(!sideExists[4]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX, y, chunkZ + 1);
			ph.setInterface(2, segment.getInterface(4));
			placeholderList.add(ph);
		}
		if(!sideExists[0]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX, y + 1, chunkZ);
			ph.setInterface(1, segment.getInterface(0));
			placeholderList.add(ph);
		}
		if(!sideExists[1]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX, y - 1, chunkZ);
			ph.setInterface(0, segment.getInterface(1));
			placeholderList.add(ph);
		}
		if(!exists) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX, y, chunkZ);
			for (int i = 0; i < 6; i++) {
				ph.setInterface(i, segment.getInterface(i));
			}
			ph.setOccupied(true);
			placeholderList.add(ph);
		}
		
	}
	
	public void generateGen(int chunkX, int chunkZ, World world, Gen gen, Random random) {
		
		int startY = 0;
		Segment startingSegment = null;
		List<SegmentPlaceholder> placeholderList = new ArrayList<SegmentPlaceholder>();
		
		// calculate generation height
		switch(gen.getLevel()) {
			case Gen.UNDERGROUND_LEVEL:
				startY = 4 + random.nextInt(3)*16;
				break;
			
			case Gen.SURFACE_LEVEL:
				startY = world.getHeightValue(chunkX*16, chunkZ*16);
				break;
				
			case Gen.SEA_FLOOR_LEVEL:
				for (int i = 255; i > 0; i--) {
					if(world.getBlockMaterial(chunkX*16, chunkZ*16, startY).isSolid()) {
						startY = i+1;
						break;
					}
				}
				break;
			
			default:
				MLogger.log("attempt to generate gen with invalid level.");
				return;
		
		}
		
		// get starting segment
		startingSegment = gen.getStartingSegment(random);
		
		// add placeholder for starting segment to placeholder list
		createPlaceholders(chunkX, chunkZ, startY, startingSegment, placeholderList);
		
		// generate starting segment
		generateSegment(chunkX, chunkZ, startY, startingSegment, world, false);
		
		// while there are placeholders
		while(hasUnoccupiedPlaceholders(placeholderList)) {
			// get first placeholder
			SegmentPlaceholder ph = getFirstUnoccupiedPlaceholder(placeholderList);
			
			// get segment matching placeholder
			Segment segment = gen.getMatchingSegment(ph.getInterfaces(), random);
			
			// generate segment
			generateSegment(ph.getX(), ph.getZ(), ph.getY(), segment, world, false);
			
			// update placeholder list based on segment
			createPlaceholders(ph.getX(), ph.getZ(), ph.getY(), segment, placeholderList);
		}
		
	}
	
	private boolean hasUnoccupiedPlaceholders(List<SegmentPlaceholder> placeholderList) {
		for (SegmentPlaceholder placeholder : placeholderList) {
			if(!placeholder.isOccupied()) {
				return true;
			}
		}
		return false;
	}
	
	private SegmentPlaceholder getFirstUnoccupiedPlaceholder(List<SegmentPlaceholder> placeholderList) {
		for (SegmentPlaceholder placeholder : placeholderList) {
			if(!placeholder.isOccupied()) {
				return placeholder;
			}
		}
		return null;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if(random.nextInt(Constants.DUNGEON_CHANCE) == 0) {
			MLogger.log("GENERATE");
			List<Gen> matchingGens = new ArrayList<Gen>();
			GenManager genManager = GenManager.getInstance();
			BiomeGenBase biome = world.getBiomeGenForCoords(chunkX, chunkZ);
			Type[] types = BiomeDictionary.getTypesForBiome(biome);
			
			for (int i = 0; i < genManager.getNumGens(); i++) {
				Gen gen = genManager.getGenByIndex(i);
				for (int j = 0; j < types.length; j++) {
					if(gen.generatesInBiome(types[j])) {
						matchingGens.add(gen);
						break;
					}
				}
			}
			
			if(matchingGens.size() > 0) {
				generateGen(chunkX, chunkZ, world, matchingGens.get(random.nextInt(matchingGens.size())), random);
			} else {
				MLogger.log("Found no matching gens when generating in biome: " + biome.toString());
			}
		}
		
	}
}
