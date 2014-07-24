package se.mickelus.customgen;

public class Constants {
	
	public static final String MOD_ID = "customgen";
	public static final String MOD_NAME = "Customgen";
	public static final String VERSION = "R1";
	
	public static final String PACKS_PATH = "resourcepacks";
	public static final String GENS_PATH = PACKS_PATH + "/%s/assets/" + MOD_ID;
	public static final String GENS_REL_PATH = "assets/" + MOD_ID;
	public static final String FILE_PATH = GENS_PATH + "/%s";
	public static final String FILE_EXT = ".nbt";
	
	public static final int SEGMENT_SIZE = 16;
	public static final int DUNGEON_CHANCE = 300;
	public static final int SEGMENT_LIMIT = 50;
	
	public static final int GENERATION_WEIGHT = 10;
	
	public static final String CHANNEL = MOD_ID;
	
	
	public static final String TEXTURE_LOCATION = MOD_ID;
	public static final String BOOKGUI_TEXTURE = "textures/gui/book.png";
	
	public static final String EMPTY_TEXTURE = "empty";
	public static final String EMPTY_UNLOC_NAME = "emptyblock";
	
	public static final String INTERFACEBLOCK_TEXTURE = "interface";
	public static final String INTERFACEBLOCK_UNLOC_NAME = "interfaceblock";
	
	public static final String PLACEHOLDERITEM_TEXTURE = "phitem";
	public static final String PLACEHOLDERITEM_UNLOC_NAME = "placeholderitem";
	
	public static final String BOOKITEM_TEXTURE = "book";
	public static final String BOOKITEM_UNLOC_NAME = "customgenerationbook";
	
	public static final String TUTORIAL_INTRO = 
			  "This is a tutorial on how to create new terrain generation features "
			+ "for the " + MOD_NAME + " mod. This is done using two components: "
			+ "Gens and Components, Gens contains segments and a set of rules, "
			+ "Components consists of blocks and some more rules.";
	
	public static final String TUTORIAL_GEN1 = 
			  "A gen consists of:\n"
			+ "- Name: The gen has to have a name.\n"
			+ "- Resource pack (rp): The name of the resource pack this gen should be in.\n"
			+ "- Level: Decides what y level this gen will generate at. \"underground\" will"
			+ " cause the starting";
	
	public static final String TUTORIAL_GEN2 =
			  "segment to generate at level 4, 20, or 36. \"surface\" will cause it to generate "
			+ "on the surface. \"sea floor\" works similar surface but will generate at the bottom "
			+ "of lakes, rivers and seas.\n"
			+ "- biomes: The biome types this gen can generate in, specifying";
	
	public static final String TUTORIAL_GEN3 =
			  "specifying zero biomes will cause it to generate in all biomes.\n"
			+ "- Segments: One or more segments. There has to be at least one starting "
			+ "segment.";
	
			  

}
