package se.mickelus.customgen.gui;

import net.minecraft.client.gui.Gui;

public class GuiLine extends Gui implements Drawable {

	int left, top;
	int length;
	boolean horizontal;
	
	int color;
	
	public GuiLine(int left, int top, int length, boolean horizontal) {
		this(left, top, length, horizontal, 0xff000000);
		
		
	}
	
	public GuiLine(int left, int top, int length, boolean horizontal, int color) {
		this.left = left;
		this.top = top;
		
		this.length = length;
		
		this.horizontal = horizontal;
		
		this.color = color;
	}
	
	
	@Override
	public void draw(int screenWidth, int screenHeight) {
		if(horizontal) {
			drawHorizontalLine(
					(screenWidth - GuiScreenGenBook.bookImageWidth) / 2 + left, 
					(screenWidth - GuiScreenGenBook.bookImageWidth) / 2 + left+length,
					(screenHeight - GuiScreenGenBook.bookImageHeight) / 2 + top,
					color);
		} else {
			drawVerticalLine(
					(screenWidth - GuiScreenGenBook.bookImageWidth) / 2 + left,
					(screenHeight - GuiScreenGenBook.bookImageHeight) / 2 + top,
					(screenHeight - GuiScreenGenBook.bookImageHeight) / 2 + top + length,
					color);
		}

	}

}
