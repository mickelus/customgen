package se.mickelus.customgen.newstuff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.BiomeDictionary.Type;
import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;
public class FileHandler {
	
	private static Gen[] parseAllZippedGens() {
		ArrayList<Gen> genList = new ArrayList<Gen>();
		
		File dir = new File(Constants.PACKS_PATH);
		String[] packNames = dir.list();
		
		if(packNames == null) {
			return new Gen[0];
		}
		for (int i = 0; i < packNames.length; i++) {
			
			File file = new File(String.format("%s/%s", Constants.PACKS_PATH, packNames[i]));
			
		
			if(file.exists()
					&& file.canRead()  
					&& !file.isHidden()
					&& file.getName().endsWith(".zip")) {
				try {
					
					ZipFile zip = new ZipFile(file);
					
					Enumeration<? extends ZipEntry> entries = zip.entries();
					
					while(entries.hasMoreElements()) {
						ZipEntry current = entries.nextElement();
						String name = current.getName();
						if(name.contains(Constants.GENS_REL_PATH)
								&& name.endsWith(Constants.FILE_EXT)) {
							Gen gen = parseZippedGen(zip, current);
							
							if(gen!= null) {
								genList.add(gen);
							} else {
								MLogger.logf("Failed to read gen \"%s\" from \"%s\"",
									name.substring(name.lastIndexOf('/')+1), packNames[i]);
							}
						}
						
					}
					
					zip.close();
				} catch (Exception e) {
					MLogger.logf("An error occured when reading gens from %s", packNames[i]);
				}
			}
			
			
			
		}
		
		return genList.toArray(new Gen[genList.size()]);
	}
	
	
	/**
	 * Parses a gen from a zip file
	 * @param file
	 * @param entry
	 * @return
	 */
	private static Gen parseZippedGen(ZipFile file, ZipEntry entry) {
		Gen gen;
		try {
			gen = Gen.readFromNBT(CompressedStreamTools.readCompressed(file.getInputStream(entry)));
			return gen;
		} catch (IOException e) {
			return null;
		}
	}
	
	private static Gen[] parseAllDirectoryGens() {
		List<Gen> genList = new LinkedList<Gen>();
		Gen[] genArray;
		File packsFolder = new File(Constants.PACKS_PATH);
		
		if(!packsFolder.isDirectory()) {
			MLogger.log("Unable to parse gens: resourcepacks/assets directory missing.");
			return new Gen[0];
		}
		
		String[] packNames = new File(Constants.PACKS_PATH).list();
		
		for (int i = 0; i < packNames.length; i++) {
			File genDir = new File(String.format(Constants.GENS_PATH, packNames[i]));
			if(genDir.isDirectory()) {
				String[] genNames = genDir.list();
				for (int j = 0; j < genNames.length; j++) {
					Gen gen = parseDirectoryGen(genNames[j], packNames[i]);
					if(gen != null) {
						genList.add(gen);
					}
					
				}
			}
			
		}
		
		return genList.toArray(new Gen[genList.size()]);
	}

	/**
	 * Parse a gen file from a directory, returning a Gen object.
	 * @param name the name of the gen
	 * @return
	 */
	private static Gen parseDirectoryGen(String name, String resourcePack) {
		NBTTagCompound nbt;
		FileInputStream stream;
		File file = new File(String.format(Constants.FILE_PATH, resourcePack, name));
		
		Gen gen = null;
				
		
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			MLogger.logf("Unable to load file \"%s\" in pack \"%s\".", name, resourcePack);
			return null;
		}
		
		try {
			nbt = CompressedStreamTools.readCompressed(stream);
		} catch (IOException e) {
			MLogger.logf("Unable to parse nbt from file \"%s\" in pack \"%s\".", name, resourcePack);
			return null;
		}
		
		if(nbt.getString(Gen.NAME_KEY) != name.substring(0, name.lastIndexOf('.'))) {
			nbt.setString(Gen.NAME_KEY, name.substring(0, name.lastIndexOf('.')));
		}
		if(nbt.getString(Gen.RESOURCEPACK_KEY) != resourcePack) {
			nbt.setString(Gen.RESOURCEPACK_KEY, resourcePack);
		}
		
		gen = Gen.readFromNBT(nbt);
		
		
		
		return gen;
	}
	
	/**
	 * Parse all gen files in all resource packs and returns them in an array.
	 * @return An array of gens from all resource packs
	 */
	public static Gen[] parseAllGens() {

		Gen[] zippedGens = parseAllZippedGens();
		ArrayList<Gen> allGens = new ArrayList<Gen>(Arrays.asList(parseAllDirectoryGens()));
		
		for (int i = 0; i < zippedGens.length; i++) {
			boolean exists = false;
			for (Gen gen : allGens) {
				if(gen.getName().equals(zippedGens[i].getName())
						&& gen.getResourcePack().equals(zippedGens[i].getResourcePack())) {
					exists = true;
					break;
					

				}
			}
			if(!exists) {
				allGens.add(zippedGens[i]);
			}
		}
		
		return allGens.toArray(new Gen[allGens.size()]);
	}
	
	/**
	 * Save a gen to a file
	 * @return
	 */
	public static boolean saveGenToFile(Gen gen) {	
		File file = new File(String.format(Constants.FILE_PATH, gen.getResourcePack(), gen.getName()+Constants.FILE_EXT));
		FileOutputStream stream;
		NBTTagCompound nbt;
		
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				MLogger.logf("Unable to create file for gen \"%s\" in pack \"%s\".", gen.getName(), gen.getResourcePack());
				return false;
			}
		}
		
		try {
			stream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			MLogger.logf("Unable to open file for gen \"%s\" in pack \"%s\".", gen.getName(), gen.getResourcePack());
			return false;
		}
		
		nbt = gen.writeToNBT(true);
		
		try {
			CompressedStreamTools.writeCompressed(nbt, stream);
		} catch (IOException e) {
			MLogger.logf("Unable to write to file for gen \"%s\" in pack \"%s\".", gen.getName(), gen.getResourcePack());
			return false;
		}
		
		try {
			stream.close();
		} catch (IOException e) {
			MLogger.logf("Unable to close stream after writing gen \"%s\" in pack \"%s\".", gen.getName(), gen.getResourcePack());
		}
		
		return true;
	}
}
