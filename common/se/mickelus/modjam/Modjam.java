package se.mickelus.modjam;


import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import se.mickelus.modjam.creation.CreateBlockCommand;
import se.mickelus.modjam.creation.EmptyBlock;
import se.mickelus.modjam.creation.LoadCommand;
import se.mickelus.modjam.creation.SaveCommand;
import se.mickelus.modjam.gen.GenerationHandler;
import se.mickelus.modjam.segment.SegmentStore;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod (modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION)
public class Modjam {
	
	@Instance(Constants.MOD_NAME)
    public static Modjam instance;
	
	@EventHandler
    public void init(FMLPreInitializationEvent event) {

        ConfigHandler.init(event.getSuggestedConfigurationFile());
    }
	
	@EventHandler
    public void init(FMLInitializationEvent event) {  
		
        setupBlock();
    }
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event){
		System.out.println("SERVER START");
		MinecraftServer server = MinecraftServer.getServer();
		ICommandManager command = server.getCommandManager();
		ServerCommandManager serverCommand = (ServerCommandManager) command;
		serverCommand.registerCommand(new SaveCommand());
		serverCommand.registerCommand(new CreateBlockCommand());
		serverCommand.registerCommand(new LoadCommand());
		new GenerationHandler();
        
        SegmentStore.init();
	}
	
	private void setupBlock() {
		EmptyBlock empty = new EmptyBlock(Constants.EMPTY_ID);
		GameRegistry.registerBlock(empty, Constants.EMPTY_UNLOC_NAME);
		
		LanguageRegistry.addName(empty, Constants.EMPTY_NAME);
	}

}
