package se.mickelus.customgen.gui;

import java.util.Observer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonTextInput extends GuiButton {
	
	private boolean focus = false;
	
	private boolean showCursor = true;
	private int drawCounter = 0;
	
	private String string = "";
	
	private Observer onCharObserver;
	
	public GuiButtonTextInput(int id, int left, int top, int width, String text, Observer onCharObserver) {
		super(id, left, top, width, 10, "");
		
		string = text;
		
		this.onCharObserver = onCharObserver;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if (this.visible) {
			
			drawCounter++;
			if(drawCounter>30) {
				showCursor = !showCursor;
				drawCounter = 0;
			}
			
			// draw text
			minecraft.fontRendererObj.drawString(string, xPosition+1, yPosition, 0);
            
			// draw underline
			drawHorizontalLine(xPosition, xPosition+width, yPosition+9, 0xff000000);
			
			// draw cursor if input has focus
			if(focus && showCursor) {
				drawVerticalLine(xPosition + minecraft.fontRendererObj.getStringWidth(string) + 1, yPosition-2, yPosition+8, 0xff000000);
			}
        }
	}
	
	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
		return super.mousePressed(par1Minecraft, par2, par3);
	}
	
	public void setFocus(boolean hasFocus) {
		focus = hasFocus;
	}
	
	public boolean hasFocus() {
		return focus;
	}
	
	public void setString(String string) {
		if(Minecraft.getMinecraft().fontRendererObj.getStringWidth(string)<=width-2) {
			this.string = string;
			onCharObserver.update(null, this.string);
		}
		
	}
	
	public String getString() {
		return string;
	}
}
