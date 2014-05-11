package se.mickelus.customgen.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiButtonGenListItem extends GuiButton {
	
	private GuiText nameText, segmentCountText;
	
	private int index;
	
	public GuiButtonGenListItem(int index, int left, int top, String name, int segmentCount) {
		super(0, left, top, 112, 15, name);
		
		nameText = new GuiText(name, left + 1, top + (height - 8) / 2);
		
		segmentCountText = new GuiText(segmentCount + "", left + 111, top + (height - 8) / 2, GuiText.RIGHT_ALIGN);
		
		this.index = index;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if (this.visible) {
			
			// draw highlight if hovering
			if(mouseX >= xPosition && mouseX <= xPosition+width
					&& mouseY >= yPosition && mouseY <= yPosition+height) {
				drawRect(xPosition-1, yPosition, xPosition+width+1, yPosition+height, 0x55aaffaa);
			}
            
			// ugly fix
			nameText.draw(GuiScreenGenBook.bookImageWidth, GuiScreenGenBook.bookImageHeight);
            segmentCountText.draw(GuiScreenGenBook.bookImageWidth, GuiScreenGenBook.bookImageHeight);
        }
	}
	
	public String getGenName() {
		return displayString;
	}
	
	public int getIndex() {
		return index;
	}
}
