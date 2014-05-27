package se.mickelus.customgen.newstuff;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraftforge.common.BiomeDictionary.Type;

import se.mickelus.customgen.MLogger;

public class GenManager {
	
	private List<Gen> genList;
	
	private static GenManager instance;
	
	public GenManager() {
		genList = new ArrayList<Gen>();
		
		instance = this;
	}
	
	public static GenManager getInstance() {
		return instance;
	}
	
	public void addGen(Gen gen) {
		int index = indexOfGen(gen.getName(), gen.getResourcePack());
		if(index == -1) {
			genList.add(gen);
		} else {
			Gen tempGen = genList.get(index);
			tempGen.setBiomes(gen.getBiomes());
			tempGen.setLevel(gen.getLevel());
			tempGen.setVillageGen(gen.isVillageGen());
			
		}
		
	}
	
	public int indexOfGen(String name, String resourcePack) {
		for (int i=0;i<genList.size();i++) {
			if(genList.get(i).getName().equals(name) && genList.get(i).getResourcePack().equals(resourcePack)) {
				return i;
			}
		}
		return -1;
	}
	
	public Gen getRandomGen(Type biome, Random random) {
		ArrayList<Gen> matchingGens = new ArrayList<Gen>();
		
		// get all gens that generate in the given biome
		for (Gen gen : genList) {
			if(gen.generatesInBiome(biome)) {
				matchingGens.add(gen);
			}
		}
		
		// return a random gen from the matching ones
		if(matchingGens.size()>0) {
			return matchingGens.get(random.nextInt(matchingGens.size()));
		}
		
		
		// return null if there are no matching gens
		return null;
	}
	
	public Gen getGenByName(String name, String resourcePack) {
		for (Gen gen : genList) {
			if(gen.getName().equals(name) && gen.getResourcePack().equals(resourcePack)) {
				return gen;
			}
		}
		return null;
	}
	
	public int getNumGens() {
		return genList.size();
	}
	
	public Gen getGenByIndex(int index) {
		return genList.get(index);
	}
	
}
