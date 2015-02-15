package se.mickelus.customgen.gui;

import se.mickelus.customgen.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiTexture extends Gui implements Drawable {

	private static final ResourceLocation textureRef = new ResourceLocation(Constants.MOD_ID, Constants.BOOKGUI_TEXTURE);
	private ResourceLocation texture;
	private int left, top;
	private int width, height;
	private int rectLeft, rectTop;
	private float red, green, blue, alpha;
	
		
	public GuiTexture(ResourceLocation texture, int left, int top, int width, int height,
			int rectLeft, int rectTop, float red, float green, float blue, float alpha) {
		super();
		this.texture = texture;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.rectLeft = rectLeft;
		this.rectTop = rectTop;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public GuiTexture(ResourceLocation texture, int left, int top, int width, int height,
			int rectLeft, int rectTop, float red, float green, float blue) {
		this(texture, left, top, width, height, rectLeft, rectTop, red, green, blue, 1f);
	}
	
	public GuiTexture(ResourceLocation texture, int left, int top, int width, int height,
			int rectLeft, int rectTop) {
		this(texture, left, top, width, height, rectLeft, rectTop, 1f, 1f, 1f);
	}


	@Override
	public void draw(int screenWidth, int screenHeight) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.color(red, green, blue, alpha);
        drawTexturedModalRect(
        		(screenWidth - GuiScreenGenBook.bookImageWidth) / 2 + left,
        		(screenHeight - GuiScreenGenBook.bookImageHeight) / 2 + top,
        		rectLeft, rectTop, width, height);
	}
	
	public static GuiTexture createDashedLineTexture(int left, int top) {
		return new GuiTexture(textureRef, left, top, 112, 1, 0, 32);
	}
	
	public static GuiTexture createDashedLineTexture(int left, int top, int width) {
		return new GuiTexture(textureRef, left, top, width, 1, 0, 32);
	}
}
