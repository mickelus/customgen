package se.mickelus.customgen.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class GuiSplitText implements Drawable {
	
	private String text;
	private int left, top;
	private int width;
	private int color;
	
	public static final int LEFT_ALIGN = 0;
	public static final int CENTER_ALIGN = 1;
	public static final int RIGHT_ALIGN = 2;
	
	public GuiSplitText(String text, int left, int top, int width) {
		this.text = text;
		this.left = left;		
		this.top = top;
		this.width = width;
		this.color = 0;
	}
	
	
	public void draw(int screenWidth, int screenHeight) {
		FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;

		renderer.drawSplitString(text, 
			(screenWidth - GuiScreenGenBook.bookImageWidth) / 2 + left,
    		(screenHeight - GuiScreenGenBook.bookImageHeight) / 2 + top,
    		width,
    		color);

		
	}
}
