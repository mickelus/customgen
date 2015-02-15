package se.mickelus.customgen.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class GuiText implements Drawable {
	
	private String text;
	private int left, top;
	private int color;
	private int alignment;
	
	public static final int LEFT_ALIGN = 0;
	public static final int CENTER_ALIGN = 1;
	public static final int RIGHT_ALIGN = 2;
	
	public GuiText(String text, int left, int top, int color, int alignment) {
		this.text = text;
		this.left = left;
		this.top = top;
		this.color = color;
		this.alignment = alignment;
	}
	
	public GuiText(String text, int left, int top, int alignment) {
		this(text, left, top, 0, alignment);
	}
	
	public GuiText(String text, int left, int top) {
		this(text, left, top, 0, LEFT_ALIGN);
	}
	
	@Override
	public void draw(int screenWidth, int screenHeight) {
		FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;
		switch(alignment) {
			case LEFT_ALIGN:
				renderer.drawString(text, 
					(screenWidth - GuiScreenGenBook.bookImageWidth) / 2 + left,
	        		(screenHeight - GuiScreenGenBook.bookImageHeight) / 2 + top,
	        		color, false);
				break;
			case CENTER_ALIGN:
				renderer.drawString(text, 
						(screenWidth - GuiScreenGenBook.bookImageWidth - renderer.getStringWidth(text)) / 2 + left,
		        		(screenHeight - GuiScreenGenBook.bookImageHeight) / 2 + top,
		        		color, false);
				break;
			case RIGHT_ALIGN:
				renderer.drawString(text, 
						(screenWidth - GuiScreenGenBook.bookImageWidth) / 2 - renderer.getStringWidth(text) + left,
		        		(screenHeight - GuiScreenGenBook.bookImageHeight) / 2 + top,
		        		color, false);
				break;
		}
	}
}
