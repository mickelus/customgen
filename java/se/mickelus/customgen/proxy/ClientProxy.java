package se.mickelus.customgen.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import se.mickelus.customgen.Constants;
import se.mickelus.customgen.blocks.EmptyBlock;
import se.mickelus.customgen.gui.GuiScreenGenBook;
import se.mickelus.customgen.items.GenBookItem;
import se.mickelus.customgen.items.PlaceholderItem;

public class ClientProxy extends Proxy {

	private static ClientProxy instance;
	
	public ClientProxy() {
		instance = this;
	}

	@Override
	public void init() {
		new GuiScreenGenBook();
		
		// register renderers
		ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		
   		//blocks
		modelMesher.register(
				Item.getItemFromBlock(EmptyBlock.getInstance()),
				0, 
				new ModelResourceLocation(Constants.MOD_ID + ":" + Constants.EMPTY_UNLOC_NAME, "inventory"));
		
		
		//items
		modelMesher.register(
				GenBookItem.getInstance(),
				0,
				new ModelResourceLocation(Constants.MOD_ID + ":" + Constants.BOOKITEM_UNLOC_NAME, "inventory"));
		modelMesher.register(
				PlaceholderItem.getInstance(),
				0,
				new ModelResourceLocation(Constants.MOD_ID + ":" + Constants.PLACEHOLDERITEM_UNLOC_NAME, "inventory"));

	}

}
