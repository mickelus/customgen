package se.mickelus.customgen;


import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import se.mickelus.customgen.creation.CreateBlockCommand;
import se.mickelus.customgen.creation.EmptyBlock;
import se.mickelus.customgen.creation.GenerateStartCommand;
import se.mickelus.customgen.creation.InterfaceBlock;
import se.mickelus.customgen.creation.LoadCommand;
import se.mickelus.customgen.creation.PlaceholderItem;
import se.mickelus.customgen.creation.SaveCommand;
import se.mickelus.customgen.generation.GenerationHandler;
import se.mickelus.customgen.segment.SegmentStore;
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
public class Customgen {
	
	@Instance(Constants.MOD_NAME)
    public static Customgen instance;
	
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        ConfigHandler.init(event.getSuggestedConfigurationFile());
    }
	
	@EventHandler
    public void init(FMLInitializationEvent event) {  
		
        setupBlocks();
        setupItems();
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
		serverCommand.registerCommand(new GenerateStartCommand());
		new GenerationHandler();
        
        SegmentStore.init();
	}
	
	private void setupBlocks() {
		System.out.println(Constants.EMPTY_ID);
		System.out.println(Constants.INTERFACEBLOCK_ID);
		EmptyBlock empty = new EmptyBlock(Constants.EMPTY_ID);
		GameRegistry.registerBlock(empty, Constants.EMPTY_UNLOC_NAME);
		LanguageRegistry.addName(empty, Constants.EMPTY_NAME);
		
		InterfaceBlock interfaceBlock = new InterfaceBlock(Constants.INTERFACEBLOCK_ID);
		GameRegistry.registerBlock(interfaceBlock, Constants.INTERFACEBLOCK_UNLOC_NAME);
		LanguageRegistry.addName(interfaceBlock, Constants.INTERFACEBLOCK_NAME);
	}
	
	private void setupItems() {
		PlaceholderItem placeholder = new PlaceholderItem(Constants.PLACEHOLDERITEM_ID);
		LanguageRegistry.addName(placeholder, Constants.PLACEHOLDERITEM_NAME);
	}

}
