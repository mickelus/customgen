package se.mickelus.customgen.gui;

import java.util.Observer;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class GuiButtonOutlined extends GuiButton {
	
	private GuiText guiText;
	
	private static final ResourceLocation textures = new ResourceLocation(Constants.MOD_ID, Constants.BOOKGUI_TEXTURE);
	
	private Observer onClickObserver;
	
	public GuiButtonOutlined(int id, int left, int top, String text) {
		this(id, left, top, text, Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) + 10, null);	
	}
	
	public GuiButtonOutlined(int id, int left, int top, String text, int width) {
		this(id, left, top, text, width, null);	
	}
	
	public GuiButtonOutlined(int id, int left, int top, String text, Observer onClickObserver) {
		this(id, left, top, text, Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) + 10, onClickObserver);	
	}
	
	public GuiButtonOutlined(int id, int left, int top, String text, int width, Observer onClickObserver) {
		super(id, left, top, width, 16, text);
		
		guiText = new GuiText(text, left + width/2, top + (height - 8) / 2, GuiText.CENTER_ALIGN);
		
		this.onClickObserver = onClickObserver;
		
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if (this.visible) {
			
			// draw highlight if hovering
			if(mouseX >= xPosition && mouseX <= xPosition+width
					&& mouseY >= yPosition && mouseY <= yPosition+height) {
				drawRect(xPosition+1, yPosition+1, xPosition+width-1, yPosition+height-1, 0x55aaffaa);
			}
			
			minecraft.getTextureManager().bindTexture(textures);
			GlStateManager.color(1f,1f,1f);
			
			// draw left side of outline
			drawTexturedModalRect(xPosition, yPosition, 0, 44, width / 2, height);
			
			// draw right side of outline
            drawTexturedModalRect(xPosition + width / 2, yPosition, 256 - width / 2, 44, width / 2, height);

            // draw text
			guiText.draw(GuiScreenGenBook.bookImageWidth, GuiScreenGenBook.bookImageHeight);
			
			
        }
	}
	
	@Override
	public void mouseReleased(int par1, int par2) {
		if(onClickObserver != null) {
			onClickObserver.update(null, displayString);
		}
	}

}
