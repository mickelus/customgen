package se.mickelus.customgen;

public class Constants {
	
	public static final String MOD_ID = "customgen";
	public static final String MOD_NAME = "Customgen";
	public static final String VERSION = "R5";
	
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
	
	public static final String EMPTY_UNLOC_NAME = "emptyblock";
	
	public static final String INTERFACEBLOCK_UNLOC_NAME = "interfaceblock";
	
	public static final String PLACEHOLDERITEM_UNLOC_NAME = "placeholderitem";
	
	public static final String BOOKITEM_UNLOC_NAME = "customgenerationbook";
}
