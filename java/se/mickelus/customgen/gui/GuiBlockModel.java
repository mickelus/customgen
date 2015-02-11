package se.mickelus.customgen.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiBlockModel extends Gui implements Drawable {
	
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private RenderItem itemRenderer;
	private FontRenderer fontRenderer;
	private TextureManager textureManager;
	
	private float x, y, z, scale;
	
	private Block block;
	private int metaData;
	
	public GuiBlockModel(float x, float y, float z, float size, Block block, int metaData) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.block = block;
		this.metaData = metaData;
		
		scale = size;
		
		this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
		textureManager = Minecraft.getMinecraft().getTextureManager();
		
	}

	@Override
	public void draw(int screenWidth, int screenHeight) {
		//GL11.glScalef(2f, 2f, 2f);
        //GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		//RenderHelper.enableGUIStandardItemLighting();
        //itemRenderer.zLevel = 100;
		//itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, textureManager, new ItemStack(Block.stone), (int)x, (int)y);
		//itemRenderer.renderItemIntoGUI(fontRenderer, textureManager, new ItemStack(Block.stone), 40, 40, true);
		
		//RenderHelper.disableStandardItemLighting();
		renderItemIntoGUI(fontRenderer, textureManager, block, metaData, scale,
				(screenWidth-GuiScreenGenBook.bookImageWidth)/2 + x, (screenHeight-GuiScreenGenBook.bookImageHeight)/2 + y, z);
		
	}
	
	public void renderItemIntoGUI(FontRenderer par1FontRenderer, TextureManager par2TextureManager, Block block, int metaData, float scale, float x, float y, float z) {
        float green = 0.2f;
        float blue = 1;
        float red = 1;
        scale = scale * 1.25f;
        ItemStack itemStack = new ItemStack(block);
        
        if (itemStack.getItem() != null && itemRenderer.shouldRenderItemIn3D(itemStack)) {
        	
            par2TextureManager.bindTexture(TextureMap.locationBlocksTexture);
            
            GL11.glPushMatrix();
            
            GL11.glTranslatef((float)(x - 2.0), (float)(y + 3.0), z);
            
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(1.0F, 0, 1.0F);
            
            GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            
            
            itemRenderer.renderItemModel(itemStack);
            GL11.glPopMatrix();
        }

    }
}
