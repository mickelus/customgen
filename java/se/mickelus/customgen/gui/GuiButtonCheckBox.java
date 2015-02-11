package se.mickelus.customgen.gui;

import java.util.Observer;

import org.lwjgl.opengl.GL11;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiButtonCheckBox extends GuiButton {
	
	private static final ResourceLocation textures = new ResourceLocation(Constants.MOD_ID, Constants.BOOKGUI_TEXTURE);
	
	private boolean checked;
	
	private Observer onClickObserver;

	public GuiButtonCheckBox(int id, int left, int top,
			String text, boolean checked, Observer onClickObserver) {
		super(id, left, top, 10 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(text), 10, text);

		this.checked = checked;
		
		this.onClickObserver = onClickObserver;
		
		enabled = false;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		
		// TODO ugly fix
		if(!enabled) {
			enabled = true;
		}
		
		// draw highlight if hovering
		if(mouseX >= xPosition && mouseX <= xPosition+width
				&& mouseY >= yPosition && mouseY <= yPosition+height) {
			drawRect(xPosition+1, yPosition+1, xPosition+7, yPosition+7, 0x55aaffaa);
		}
		
		// draw checkbox
		minecraft.getTextureManager().bindTexture(textures);
		GL11.glColor3f(1f,1f,1f);
		if(checked) {
			drawTexturedModalRect(xPosition, yPosition, 8, 60, 8, 8);
		} else {
			drawTexturedModalRect(xPosition, yPosition, 0, 60, 8, 8);
		}
		
		// draw string
		if(displayString.length() != 0) {
			minecraft.fontRendererObj.drawString(displayString, xPosition + 11, yPosition, 0x000000);
		}
		
	}
	
	@Override
	public void mouseReleased(int par1, int par2) {
		checked = !checked;
		if(onClickObserver!= null) {
			onClickObserver.update(null, checked);
		}
	}
	
	public void setChecked(boolean isChecked) {
		this.checked = isChecked;
	}
	
	public boolean isChecked() {
		return checked;
	}

}
