package se.mickelus.customgen;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MLogger {
	
	private static Logger logger;
	
	static {
		logger = Logger.getLogger(Constants.MOD_ID);
	}
	
	public static void log(Object message) {
		if(message != null) {
			logger.log(Level .INFO, message.toString());
		} else {
			logger.log(Level .INFO, "null");
		}
	}
	
	public static void logf(String format, Object... args) {
		logger.log(Level .INFO, String.format(format, args));
	}
}
