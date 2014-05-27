package se.mickelus.customgen;

import java.io.File;


import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	
	public static void init(File file) {
		Configuration config = new Configuration(file);
		config.load();
		
		/*Constants.EMPTY_ID = config.getBlock(Constants.EMPTY_UNLOC_NAME, Constants.EMPTY_ID).getInt();
		Constants.INTERFACEBLOCK_ID = config.getBlock(Constants.INTERFACEBLOCK_UNLOC_NAME, Constants.INTERFACEBLOCK_ID).getInt();
		
		
		Constants.PLACEHOLDERITEM_ID = config.getItem(Constants.PLACEHOLDERITEM_UNLOC_NAME, Constants.PLACEHOLDERITEM_ID).getInt() - 256;
		*/
	}
}
