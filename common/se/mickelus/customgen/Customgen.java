package se.mickelus.customgen;


import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import se.mickelus.customgen.blocks.EmptyBlock;
import se.mickelus.customgen.blocks.InterfaceBlock;
import se.mickelus.customgen.items.GenBookItem;
import se.mickelus.customgen.items.PlaceholderItem;
import se.mickelus.customgen.network.GenAddRequestPacket;
import se.mickelus.customgen.network.GenListReponsePacket;
import se.mickelus.customgen.network.GenListRequestPacket;
import se.mickelus.customgen.network.GenRequestPacket;
import se.mickelus.customgen.network.GenResponsePacket;
import se.mickelus.customgen.network.PacketHandler;
import se.mickelus.customgen.network.PacketPipeline;
import se.mickelus.customgen.newstuff.FileHandler;
import se.mickelus.customgen.newstuff.ForgeGenerator;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.newstuff.GenManager;
import se.mickelus.customgen.proxy.Proxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
//import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod (modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION)
//@NetworkMod (channels = {Constants.CHANNEL}, serverSideRequired = true, packetHandler = PacketHandler.class)
public class Customgen {
	
	@Instance(Constants.MOD_ID)
    public static Customgen instance;
	
	@SidedProxy(clientSide = "se.mickelus.customgen.proxy.ClientProxy", serverSide = "se.mickelus.customgen.proxy.ServerProxy")
	public static Proxy proxy;
	
	public static final PacketPipeline packetPipeline = new PacketPipeline();
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        
        new CustomgenCreativeTabs();
        setupBlocks();
        setupItems();
                  
    }
	
	@EventHandler
    public void init(FMLInitializationEvent event) {  
		packetPipeline.initialize();
		packetPipeline.registerPacket(GenListRequestPacket.class);
		packetPipeline.registerPacket(GenListReponsePacket.class);
		
		packetPipeline.registerPacket(GenAddRequestPacket.class);
		
		packetPipeline.registerPacket(GenRequestPacket.class);
		packetPipeline.registerPacket(GenResponsePacket.class);
        
        proxy.init();
    }
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		packetPipeline.postInitialize();
		
	}
	
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event){
		System.out.println("SERVER START");
		
		if(ForgeGenerator.getInstance() == null) {
			new ForgeGenerator();
		}

		/* when playing singleplayer this lets us update Gens by restarting the world
		 * instead of restarting the game */
		GenManager genManager;
		//if(GenManager.getInstance() == null) {
			genManager = new GenManager();
		/*} else {
			genManager = GenManager.getInstance();
		}*/
        Gen[] gens = FileHandler.parseAllGens();
        if(gens != null) {
        	for (int i = 0; i < gens.length; i++) {
    			genManager.addGen(gens[i]);
    		}
        }
        
	}
	
	private void setupBlocks() {

		
		EmptyBlock empty = new EmptyBlock();
		GameRegistry.registerBlock(empty, empty.getUnlocalizedName().substring(5));
		System.out.println("registered block: " + GameRegistry.findUniqueIdentifierFor(empty).toString());

		InterfaceBlock interfaceBlock = new InterfaceBlock();
		GameRegistry.registerBlock(interfaceBlock, interfaceBlock.getUnlocalizedName());

	}
	
	private void setupItems() {
		PlaceholderItem placeholder = new PlaceholderItem();
		GameRegistry.registerItem(placeholder, placeholder.getUnlocalizedName());
		
		GenBookItem bookItem = new GenBookItem();
		GameRegistry.registerItem(bookItem, bookItem.getUnlocalizedName());

	}

}
