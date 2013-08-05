package se.mickelus.modjam;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class ConfigHandler {

	
	public static void init(File file) {
		Configuration config = new Configuration(file);
		config.load();
		
		Constants.EMPTY_ID = config.getBlock(Constants.EMPTY_UNLOC_NAME, Constants.EMPTY_ID).getInt();
		
	}
}
