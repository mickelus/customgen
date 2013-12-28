package se.mickelus.customgen;

public class Constants {
	
	public static final String MOD_ID = "cuge";
	public static final String MOD_NAME = "customgen";
	public static final String VERSION = "0.1";
	
	public static final String PACKS_PATH = "resourcepacks/assets";
	public static final String GENS_PATH = PACKS_PATH + "/%s/" + MOD_NAME;
	public static final String FILE_PATH = GENS_PATH + "/%s";
	public static final String FILE_EXT = ".nbt";
	
	public static final int SEGMENT_SIZE = 16;
	public static final int DUNGEON_CHANCE = 100;
	public static final int SEGMENT_LIMIT = 50;
	
	public static final String CHANNEL = MOD_ID;
	
	public static final String TEXTURE_LOCATION = MOD_NAME;
	public static final String BOOKGUI_TEXTURE = "textures/gui/book.png";
	
	public static int EMPTY_ID = 2000;
	public static final String EMPTY_TEXTURE = "empty";
	public static final String EMPTY_UNLOC_NAME = "EmptyBlock";
	public static final String EMPTY_NAME = "Empty Block";
	
	public static int INTERFACEBLOCK_ID = 2001;
	public static final String INTERFACEBLOCK_TEXTURE = "interface";
	public static final String INTERFACEBLOCK_UNLOC_NAME = "InterfaceBlock";
	public static final String INTERFACEBLOCK_NAME = "Interface Block";
	
	public static int PLACEHOLDERITEM_ID = 4000;
	public static final String PLACEHOLDERITEM_TEXTURE = "phitem";
	public static final String PLACEHOLDERITEM_UNLOC_NAME = "PlaceholderItem";
	public static final String PLACEHOLDERITEM_NAME = "Loot placeholder";
	
	public static int BOOKITEM_ID = 4001;
	public static final String BOOKITEM_TEXTURE = "book";
	public static final String BOOKITEM_UNLOC_NAME = "CustomGenerationBook";
	public static final String BOOKITEM_NAME = "Book of Custom Generation";	
	
	

}
