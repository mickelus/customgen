package se.mickelus.customgen.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;

@SideOnly(Side.CLIENT)
public class GuiButtonChangePage extends GuiButton {
    /**
     * True for pointing right (next page), false for pointing left (previous page).
     */
    private boolean shouldIncrement;
    private int offset;
    int test;
    
    private static final ResourceLocation textures = new ResourceLocation(Constants.MOD_ID, Constants.BOOKGUI_TEXTURE);

    public GuiButtonChangePage( int id, int offset, int left, int top, boolean shouldIncrement) {
        super(0, left, top, 14, 11, "");
        this.shouldIncrement = shouldIncrement;
        this.offset = offset;
        
        this.test = id;
        
        // TODO : ugly fix to avoid doubleclicks, fix the fix
        enabled = false;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            int rectLeft = 0;	
            
            // show hover texture if mouse is hovering over button
            if (mouseX >= xPosition && mouseX < xPosition + width &&
            		mouseY >= yPosition &&  mouseY < yPosition + height) {
            	rectLeft = 28;
            }

            // show the right arrow if we should decrement
            if (!shouldIncrement) {
            	rectLeft += 14;
            }
            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(textures);
            this.drawTexturedModalRect(xPosition, yPosition,rectLeft, 33, width, height);
        }
        if(!enabled){
        	enabled = true;
        }
    }
    
    /**
     * Returns the page offset that this button should take the user to.
     * @return an Integer value above 0
     */
    public int getOffset() {
    	return offset;
    }
    
    public int getPageID() {
    	return test;
    }
}
