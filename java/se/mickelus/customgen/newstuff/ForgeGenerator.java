package se.mickelus.customgen.newstuff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.ChestGenHooks;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.blocks.EmptyBlock;
import se.mickelus.customgen.blocks.InterfaceBlock;
import se.mickelus.customgen.items.PlaceholderItem;
import se.mickelus.customgen.segment.Segment;
import se.mickelus.customgen.segment.SegmentPlaceholder;
import sun.rmi.log.ReliableLog.LogFile;

public class ForgeGenerator implements IWorldGenerator  {
	
	private static ForgeGenerator instance;
	
	
	public ForgeGenerator() {

		GameRegistry.registerWorldGenerator(this, Constants.GENERATION_WEIGHT);
		
		instance = this;
	}
	
	public static ForgeGenerator getInstance() {
		return instance;
	}

	public void generateSegment(int chunkX, int chunkZ, int y,
			Segment segment, World world, boolean generatePlaceholders, Random random) {
		int x = chunkX * 16;
		int z = chunkZ * 16;
		
		// TODO : we should not have to handle this
		if(segment == null) {
			MLogger.log("Unable to generate segment");
			return;
		}

		// generate blocks
		for(int sy = 0; sy < 16; sy++) {
			for(int sz = 0; sz < 16; sz++) {
				for(int sx = 0; sx < 16; sx++) {
					Block block = segment.getBlock(sx, sy, sz);
					if(!generatePlaceholders && (block.equals(EmptyBlock.getInstance()) || block.equals(InterfaceBlock.getInstance()) )) {
						continue;
					}
					world.setBlock(x+sx, y+sy, z+sz, block);
					// setting metadata and block at the same time seem to overwrite the metadata if the block if a tileentity is spawned
					world.setBlockMetadataWithNotify(x+sx, y+sy, z+sz, segment.getBlockMetadata(sx, sy, sz), 2);
				}
			}
		}
		
		
		// spawn tile entities
		for (int i = 0; i < segment.getNumTileEntities(); i++) {
			
			NBTTagCompound tag = segment.getTileEntityNBT(i);
			tag = updateTileEntityNBT(tag, chunkX*16, y, chunkZ*16);
			TileEntity tileEntity = TileEntity.createAndLoadEntity(tag);
			
			if (tileEntity != null) {
				
				// this is dangerous!
				try {
				
					if(tileEntity instanceof IInventory) {
						IInventory inventory = (IInventory) tileEntity;
						
						for (int j = 0; j < inventory.getSizeInventory(); j++) {
							if(!generatePlaceholders && inventory.getStackInSlot(j) != null
									&& inventory.getStackInSlot(j).getItem().equals(PlaceholderItem.getInstance())) {
								ItemStack itemStack = ChestGenHooks.getOneItem(ChestGenHooks.DUNGEON_CHEST, random);
								if(inventory.getInventoryStackLimit() < itemStack.stackSize) {
									itemStack.stackSize = inventory.getInventoryStackLimit();
								}
								if(inventory.isItemValidForSlot(j, itemStack)) {
									inventory.setInventorySlotContents(j, itemStack);
								}
							}
						}
					}
				} catch(Exception e) {
					System.out.println("Something broke when generating loot in a tile entity.");
					e.printStackTrace();
				}
                world.getChunkFromChunkCoords(chunkX, chunkZ).addTileEntity(tileEntity);
            }
		}
	}
	
	private NBTTagCompound updateTileEntityNBT(NBTTagCompound tileEntityNBT, int x, int y, int z) {
        tileEntityNBT.setInteger("x", tileEntityNBT.getInteger("x") + x);
        tileEntityNBT.setInteger("y", tileEntityNBT.getInteger("y") + y);
        tileEntityNBT.setInteger("z", tileEntityNBT.getInteger("z") + z);
        
        return tileEntityNBT;
}
	
	private void createPlaceholders(int chunkX, int chunkZ, int y, Segment segment, 
			List<SegmentPlaceholder> placeholderList) {
		
		// if a ph exists for the given coordinates
		boolean exists = false;
		
		// if a ph exists for each side
		boolean [] addSide = new boolean[6];
		
		// make sure we wont add ph:s for non-interfacing sides
		for (int i = 0; i < addSide.length; i++) {
			/*if(segment.getInterface(i) != 0) {
				MLogger.logf("side exists: %d", i);
			}*/
			addSide[i] = segment.getInterface(i) != 0;
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
				addSide[3] = false;
				ph.setInterface(5, segment.getInterface(3));
				continue;
			} else if(ph.getX() == chunkX - 1
					&& ph.getZ() == chunkZ
					&& ph.getY() == y) {
				addSide[5] = false;
				ph.setInterface(3, segment.getInterface(5));
				continue;
			} else if(ph.getX() == chunkX
					&& ph.getZ() == chunkZ + 1
					&& ph.getY() == y) {
				addSide[4] = false;
				ph.setInterface(2, segment.getInterface(4));
				continue;
			} else if(ph.getX() == chunkX
					&& ph.getZ() == chunkZ - 1
					&& ph.getY() == y) {
				addSide[2] = false;
				ph.setInterface(4, segment.getInterface(2));
				continue;
			} else if(ph.getX() == chunkX
					&& ph.getZ() == chunkZ
					&& ph.getY() == y + 16) {
				addSide[0] = false;
				ph.setInterface(1, segment.getInterface(0));
				continue;
			} else if(ph.getX() == chunkX
					&& ph.getZ() == chunkZ
					&& ph.getY() == y - 16) {
				addSide[1] = false;
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
		
		/*for (int i = 0; i < addSide.length; i++) {
			if(addSide[i]) {
				MLogger.logf("add side: %d", i);
			}
		}*/
		
		/*	   0 - top		+y
		 *     1 - bottom	-y
		 *     2 - north	-z
		 *     3 - east 	+x
		 *     4 - south	+z
		 *     5 - west 	-x
		 */
		
		// add placeholders to list for unoccupied, interfaced sides
		if(addSide[3]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX + 1, y, chunkZ);
			ph.setInterface(5, segment.getInterface(3));
			placeholderList.add(ph);
		}
		if(addSide[5]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX - 1, y, chunkZ);
			ph.setInterface(3, segment.getInterface(5));
			placeholderList.add(ph);
		}
		if(addSide[2]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX, y, chunkZ - 1);
			ph.setInterface(4, segment.getInterface(2));
			placeholderList.add(ph);
		}
		if(addSide[4]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX, y, chunkZ + 1);
			ph.setInterface(2, segment.getInterface(4));
			placeholderList.add(ph);
		}
		if(addSide[0]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX, y + 16, chunkZ);
			ph.setInterface(1, segment.getInterface(0));
			placeholderList.add(ph);
		}
		if(addSide[1]) {
			SegmentPlaceholder ph = new SegmentPlaceholder(chunkX, y - 16, chunkZ);
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
		int count = 0;
		Segment startingSegment = null;
		List<SegmentPlaceholder> placeholderList = new ArrayList<SegmentPlaceholder>();
		
		MLogger.logf("Generating start at: %d %d\n", chunkX*16, chunkZ*16);
		
		// calculate generation height
		switch(gen.getLevel()) {
			case Gen.UNDERGROUND_LEVEL:
				startY = 4 + random.nextInt(3)*16;
				break;
			
			case Gen.SURFACE_LEVEL:
				int average = 0;
				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 16; j++) {
						average += world.getHeightValue(chunkX*16+i, chunkZ*16+j);
					}
				}
				
				// we generate surface gens 3 levels below, it's already one below based on world.getHeightValue
				startY = average / 256 - 2;
				break;
				
			case Gen.SEA_FLOOR_LEVEL:
				for (startY = 255; startY > 0; startY--) {
					if(world.getBlock(chunkX*16, startY, chunkZ*16).getMaterial().isSolid()) {
						break;
					}
				}
				break;
			
			default:
				MLogger.log("attempt to generate gen with invalid level.");
				return;
		}
		
		MLogger.logf("Generating start at: %d %d %d\n", chunkX*16, chunkZ*16, startY);
		
		// get starting segment
		startingSegment = gen.getStartingSegment(random);
		

		if(startingSegment != null) {
			
			// add placeholder for starting segment to placeholder list
			createPlaceholders(chunkX, chunkZ, startY, startingSegment, placeholderList);
			
			// generate starting segment
			generateSegment(chunkX, chunkZ, startY, startingSegment, world, false, random);
			
			//MLogger.log("   [+y,-y,-z,+x,+z,-x]");
			
			/*for (SegmentPlaceholder ph : placeholderList) {
				MLogger.log(ph);
			}*/
		} else {
			MLogger.logf("Failed to find starting segment when generating gen: %s", gen.getName());
		}
		
		
		// while there are placeholders
		while(hasUsablePlaceholders(placeholderList) && count < Constants.SEGMENT_LIMIT) {
			
			
			// get first placeholder
			SegmentPlaceholder ph = getFirstUsablePlaceholder(placeholderList);
			
			// get segment matching placeholder
			Segment segment = gen.getMatchingSegment(ph.getInterfaces(), random);
			
			if(segment != null) {
				// generate segment
				generateSegment(ph.getX(), ph.getZ(), ph.getY(), segment, world, false, random);
				
				// update placeholder list based on segment
				createPlaceholders(ph.getX(), ph.getZ(), ph.getY(), segment, placeholderList);
			} else {
				MLogger.logf("Failed to find matching segment when generating gen: %s", gen.getName());
				break;
			}
			
			count++;
		}
		
		//MLogger.logf("DONE GENERATING %s, E: %b, C: %d", gen.getName(), hasUsablePlaceholders(placeholderList), count);
		
	}
	
	private boolean hasUsablePlaceholders(List<SegmentPlaceholder> placeholderList) {
		for (SegmentPlaceholder placeholder : placeholderList) {
			if(!placeholder.isOccupied() && placeholder.hasProperInterfaces()) {
				return true;
			}
		}
		return false;
	}
	
	private SegmentPlaceholder getFirstUsablePlaceholder(List<SegmentPlaceholder> placeholderList) {
		for (SegmentPlaceholder placeholder : placeholderList) {
			if(!placeholder.isOccupied() && placeholder.hasProperInterfaces()) {
				return placeholder;
			}
		}
		return null;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		
		if(random.nextInt(Constants.DUNGEON_CHANCE) == 0) {
			//MLogger.logf("GENERATE %d %d", chunkX, chunkZ);
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
