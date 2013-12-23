package se.mickelus.customgen.gui;

import se.mickelus.customgen.MLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class GuiButtonSegmentListItem extends GuiButton {
	
	private GuiText nameText, entityCountText, tileEntityCountText, isStartText;
	
	private int index;
	
	public GuiButtonSegmentListItem(int index, int left, int top, String name, int entityCount, int tileEntityCount, boolean isStart) {
		super(0, left, top, 112, 15, name);
		
		nameText = new GuiText(name, left + 1, top + (height - 8) / 2);
		
		entityCountText = new GuiText(entityCount + "", left + 79, top + (height - 8) / 2, GuiText.RIGHT_ALIGN);
		tileEntityCountText = new GuiText(tileEntityCount + "", left + 101, top + (height - 8) / 2, GuiText.RIGHT_ALIGN);
		if(isStart) {
			isStartText = new GuiText("S", left + 111, top + (height - 8) / 2, GuiText.RIGHT_ALIGN);
		}
		
		
		this.index = index;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if (this.drawButton) {
			
			// draw highlight if hovering
			if(mouseX >= xPosition && mouseX <= xPosition+width
					&& mouseY >= yPosition && mouseY <= yPosition+height) {
				drawRect(xPosition-1, yPosition, xPosition+width+1, yPosition+height, 0x55aaffaa);
			}
            
			// ugly fix
			nameText.draw(GuiScreenGenBook.bookImageWidth, GuiScreenGenBook.bookImageHeight);
			entityCountText.draw(GuiScreenGenBook.bookImageWidth, GuiScreenGenBook.bookImageHeight);
			tileEntityCountText.draw(GuiScreenGenBook.bookImageWidth, GuiScreenGenBook.bookImageHeight);
			if(isStartText!=null) {
				isStartText.draw(GuiScreenGenBook.bookImageWidth, GuiScreenGenBook.bookImageHeight);
			}
        }
	}
	
	public String getSegmentName() {
		return displayString;
	}
	
	public int getIndex() {
		return index;
	}
}