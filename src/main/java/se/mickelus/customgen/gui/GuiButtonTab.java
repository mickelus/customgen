package se.mickelus.customgen.gui;

import se.mickelus.customgen.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonTab extends GuiButton {
	
	private static final ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "textures/gui/book.png");
		
	private float red, green, blue;
	
	
	public GuiButtonTab(int id, int left, int top, String text, float red, float green, float blue) {
		super(id, left, top, 64, 16, text);
		

		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if (this.visible)
        {
			
			
			minecraft.getTextureManager().bindTexture(texture);
			
			// draw background
			GlStateManager.color(red, green, blue);

            this.drawTexturedModalRect(xPosition, yPosition, 0, 0, 64, 16);
            
            // draw text
            minecraft.fontRendererObj.drawString(displayString, xPosition + 8, yPosition + (height - 8) / 2, 0);
        }
	}

}
