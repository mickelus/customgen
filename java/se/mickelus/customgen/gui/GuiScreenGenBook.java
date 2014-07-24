package se.mickelus.customgen.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.network.PacketBuilder;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.newstuff.Utilities;
import se.mickelus.customgen.segment.Segment;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.BiomeDictionary.Type;

@SideOnly(Side.CLIENT)
public class GuiScreenGenBook extends GuiScreen {
	
	private static GuiScreenGenBook instance;
	
	private static final ResourceLocation vanillaBookTexture = new ResourceLocation("textures/gui/book.png");
	private static final ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, Constants.BOOKGUI_TEXTURE);
	

    /** Update ticks since the gui was opened */
    private int updateCount;
    
    
    public static final int bookImageWidth = 192;
    public static final int bookImageHeight = 192;
    
    private List<Drawable> drawList;
    
    private String genNames[];
    private String packNames[];
    private int genSegmentCounts[];
    private Map<String, Segment[]> segmentMap;
    
    private GuiButtonTextInput activeInput;
    
    // current gui view state
    private int state;
    
    // gen list state
    private int stateGenListOffset = 0;
    
    // gen view states
    private int stateViewGenIndex = 0;
    private Gen stateViewGen;
    
    // gen add state
    private Gen stateAddGen;
    private int stateGenAddViewPage = 0;
    
    // gen add elements
    private GuiButtonCheckBox undergroundCheckBox;
    private GuiButtonCheckBox surfaceCheckBox;
    private GuiButtonCheckBox seaCheckBox;
    private List<GuiButtonCheckBox> biomeCheckBoxes;
    
    // segment view states
    private int stateViewSegmentOffset = 0;
    private Segment stateviewSegment;
    private boolean stateViewSegmentIsStart;
    
    // segment add states
	private String stateAddViewGen = "";
	private String stateAddViewPack = "";
	private Segment stateAddViewSegment;
	private boolean stateAddViewIsStart = false;
	private int stateSegmentAddViewPage = 0;
	
	// tutorial states
	private int stateHelpOffset = 0;
    
    static final int GEN_LIST_STATE = 1;
    static final int GEN_ADD_STATE = 2;
    static final int GEN_VIEW_STATE = 3;
    static final int SEGMENT_VIEW_STATE = 4;
    static final int SEGMENT_ADD_STATE = 5;
    static final int UTILITY_STATE = 6;
    static final int HELP_STATE = 7;
    
    private static final int GEN_LIST_MAX_LENGTH = 7;
    private static final int GEN_CREATE_BIOME_LIST_MAX_LENGTH = 12;
    private static final int GEN_BIOME_STRING_MAX_LENGTH = 7;
    private static final int GEN_BIOME_LIST_MAX_LENGTH = 22;
    private static final int GEN_SEGMENT_LIST_MAX_LENGTH = 6;
    
    private static final int SEGMENT_PAGES_COUNT = 2;
    private static final int HELP_PAGES_COUNT = 7;
    
    private EntityPlayer player;

	

	public GuiScreenGenBook() {
		super();
		
		drawList = new ArrayList<Drawable>();
		
		state = 7;
		
		instance = this;
		
		stateAddGen = new Gen("", "");
		
		stateAddViewSegment = new Segment("");
		
		stateviewSegment = new Segment("");	
		
		biomeCheckBoxes = new ArrayList<GuiButtonCheckBox>(Type.values().length);
		
	}
	
	public void SetPlayer(EntityPlayer player) {
		this.player = player;
	}
    
    private void showView(int state) {
    	
    	// clear lists
    	buttonList.clear();
    	drawList.clear();
    	
    	
    	
    	// add tabs
    	buttonList.add(new GuiButtonTab(GEN_LIST_STATE,
         		width / 2 + 61,
         		(height - bookImageHeight) / 2 + 15,
         		"gen list",
         		1, 0.7f, 0.7f));
         
         buttonList.add(new GuiButtonTab(SEGMENT_ADD_STATE,
         		width / 2 + 61,
         		(height - bookImageHeight) / 2 + 36,
         		"segment",
         		0.7f, 1, 0.7f));
         
         buttonList.add(new GuiButtonTab(UTILITY_STATE,
         		width / 2 + 61,
         		(height - bookImageHeight) / 2 + 57,
         		"utilities",
         		0.7f, 0.7f, 1));
         
//         buttonList.add(new GuiButtonTab(HELP_STATE,
//          		width / 2 + 61,
//          		(height - bookImageHeight) / 2 + 78,
//          		"tutorial",
//          		1f, 0.7f, 1));
    	
    	// show the view that matches the current state
    	switch(state) {
	    	case GEN_LIST_STATE:
	    		showGenListView(stateGenListOffset);
	    		break;
	    	case GEN_ADD_STATE:
	    		showGenCreateView(stateGenAddViewPage);
	    		break;
	    		
	    	case GEN_VIEW_STATE:

	    		showGenView(stateViewGen, stateViewGenIndex);

	    		break;
	    		
	    	case SEGMENT_VIEW_STATE:
	    		showSegmentView(stateviewSegment, stateViewSegmentIsStart);
	    		break;

	    	case SEGMENT_ADD_STATE:
	    		showSegmentAddView(stateAddViewGen, stateAddViewPack);
	    		break;
	    		
	    	case UTILITY_STATE:
	    		showUtilityView();
	    		break;
	    		
	    	case HELP_STATE:
	    		showHelpView(stateHelpOffset);
	    		break;
	    	
    	}
    }
    
    /**
     * Show a view listing all available gens.
     */
    private void showGenListView(int offset) {
    	int listLength = genNames.length;
    	int listOffset = 0;
    	
    	// if there is more than one page of gens
    	if(listLength > GEN_LIST_MAX_LENGTH) {
    		
    		// show next button
    		if((offset + 1) * GEN_LIST_MAX_LENGTH < listLength) {
    			buttonList.add(new GuiButtonChangePage(0, offset + 1,
					(width - bookImageWidth) / 2 + 134,
					(height - bookImageHeight) / 2 + 153,
					true));
    		}
    		
    		// back button
    		if(offset>0) {
    			buttonList.add(new GuiButtonChangePage(0, offset - 1,
					(width - bookImageWidth) / 2 + 95,
					(height - bookImageHeight) / 2 + 153,
					false));
    		}
    		
    		// current page number
    		drawList.add(new GuiText((offset + 1) + "/" + ((listLength-1)/GEN_LIST_MAX_LENGTH + 1), 122, 155, GuiText.CENTER_ALIGN));
    		
    		// set item offset and list length
    		listOffset = offset * GEN_LIST_MAX_LENGTH;
    		if(listOffset + GEN_LIST_MAX_LENGTH > listLength) {
    			listLength = listLength - listOffset;
    		} else {
    			listLength = GEN_LIST_MAX_LENGTH;
    		}
    	}
    	
    	// new button
    	buttonList.add(new GuiButtonOutlined(GEN_ADD_STATE,
			(width - bookImageWidth) / 2 + 38,
			(height - bookImageHeight) / 2 + 151,
			"new gen"));
    	
    	// title
    	drawList.add(new GuiText("Available gens", 93, 17, GuiText.CENTER_ALIGN));
    	
    	// list buttons based on offset
    	for (int i = 0; i < listLength; i++) {
			buttonList.add(new GuiButtonGenListItem(listOffset + i, 
				(width - bookImageWidth) / 2 + 37,
         		(height - bookImageHeight) / 2 + 36 + i*16,
				genNames[listOffset + i], genSegmentCounts[listOffset + i]));
		}
    	
    	// list headers
    	drawList.add(new GuiText("name", 38, 26));
    	drawList.add(new GuiText("segments", 148, 26, GuiText.RIGHT_ALIGN));
    	
    	// item separators
    	for (int i = 0; i < listLength; i++) {
			drawList.add(GuiTexture.createDashedLineTexture(37, 35 + i*16));
		}
    }
    
    private void showGenCreateView(int offset) {
    	// title
    	drawList.add(new GuiText("Create gen", 93, 17, GuiText.CENTER_ALIGN));
    	
    	// name input
    	drawList.add(new GuiText("name:", 38, 27));
    	buttonList.add(new GuiButtonTextInput(0,
    		(width - bookImageWidth) / 2 + 65,
    		(height - bookImageHeight) / 2 + 27
    		, 80, stateAddGen.getName(), new Observer() {
				
				@Override
				public void update(Observable o, Object arg) {
					stateAddGen.setName((String)arg);
					
				}
			}));
    	
    	// resourcepack input
    	drawList.add(new GuiText("rp:", 50, 39));
    	buttonList.add(new GuiButtonTextInput(0,
        		(width - bookImageWidth) / 2 + 65,
        		(height - bookImageHeight) / 2 + 39
        		, 80, stateAddGen.getResourcePack(), new Observer() {
					
					@Override
					public void update(Observable o, Object arg) {
						stateAddGen.setResourcePack((String)arg);
						
					}
				}));
    	
    	
    	
    	// level checkboxes
    	drawList.add(new GuiText("level:", 38, 51));
    	buttonList.add(undergroundCheckBox = new GuiButtonCheckBox(0,
    			(width - bookImageWidth) / 2 + 65,
        		(height - bookImageHeight) / 2 + 51,
        		"underground", stateAddGen.getLevel() == Gen.UNDERGROUND_LEVEL, new Observer() {
			
			@Override
			public void update(Observable o, Object arg) {
				disableLevelCheckBoxes();
				if((Boolean)arg) {
					undergroundCheckBox.setChecked(true);
					stateAddGen.setLevel(Gen.UNDERGROUND_LEVEL);
				}			
			}
		}));
    	
    	buttonList.add(surfaceCheckBox = new GuiButtonCheckBox(0,
    			(width - bookImageWidth) / 2 + 38,
        		(height - bookImageHeight) / 2 + 61,
        		"surface", stateAddGen.getLevel() == Gen.SURFACE_LEVEL, new Observer() {
			
			@Override
			public void update(Observable o, Object arg) {
				disableLevelCheckBoxes();
				if((Boolean)arg) {
					surfaceCheckBox.setChecked(true);
					stateAddGen.setLevel(Gen.SURFACE_LEVEL);
				}
			}
		}));
    	
    	buttonList.add(seaCheckBox = new GuiButtonCheckBox(0,
    			(width - bookImageWidth) / 2 + 92,
        		(height - bookImageHeight) / 2 + 61,
        		"sea floor", stateAddGen.getLevel() == Gen.SEA_FLOOR_LEVEL, new Observer() {
			
			@Override
			public void update(Observable o, Object arg) {
				disableLevelCheckBoxes();
				if((Boolean)arg) {
					seaCheckBox.setChecked(true);
					stateAddGen.setLevel(Gen.SEA_FLOOR_LEVEL);
				}
			}
		}));
    	
    	// village checkbox
//    	drawList.add(new GuiText("village", 38, 155));
//    	buttonList.add(new GuiButtonCheckBox(0,
//    			(width - bookImageWidth) / 2 + 72,
//        		(height - bookImageHeight) / 2 + 155,
//        		"", stateAddGen.isVillageGen(), new Observer() {
//			
//			@Override
//			public void update(Observable o, Object arg) {
//				stateAddGen.setVillageGen((Boolean)arg);
//			}
//		}));
    	
    	// if there is more than one page of biomes
    	Type[] types = Type.values();
    	int listLength = types.length;
    	int listOffset = 0;
    	if(types.length > GEN_CREATE_BIOME_LIST_MAX_LENGTH) {
    		
    		// show next button
    		if((offset + 1) * GEN_CREATE_BIOME_LIST_MAX_LENGTH < listLength) {
    			buttonList.add(new GuiButtonChangePage(4, offset + 1,
					(width - bookImageWidth) / 2 + 134,
					(height - bookImageHeight) / 2 + 153,
					true));
    		}
    		
    		// back button
    		if(offset>0) {
    			buttonList.add(new GuiButtonChangePage(4, offset - 1,
					(width - bookImageWidth) / 2 + 95,
					(height - bookImageHeight) / 2 + 153,
					false));
    		}
    		
    		// current page number
    		drawList.add(new GuiText((offset + 1) + "/" + ((listLength-1)/GEN_CREATE_BIOME_LIST_MAX_LENGTH + 1), 122, 155, GuiText.CENTER_ALIGN));
    		
    		// set item offset and list length
    		listOffset = offset * GEN_CREATE_BIOME_LIST_MAX_LENGTH;
    		if(listOffset + GEN_CREATE_BIOME_LIST_MAX_LENGTH > listLength) {
    			listLength = listLength - listOffset;
    		} else {
    			listLength = GEN_CREATE_BIOME_LIST_MAX_LENGTH;
    		}
    	}
    	
    	// biome checkboxes
    	drawList.add(new GuiText("biomes:", 38, 71));
    	
    	biomeCheckBoxes.clear();
    	GuiButtonCheckBox button;
    	for (int i = 0; i < listLength; i++) {
    		int left = (width - bookImageWidth) / 2;
    		int top = (height - bookImageHeight) / 2;
    		Type biomeType = types[i + listOffset];
    		if(i>listLength/2-1) {
    			left += 92;
    			top += (i-listLength/2)*10 + 81;
    		} else {
    			left += 38;
    			top += i*10 + 81;
    		}
    		
    		button = new GuiButtonCheckBox(0, left, top,
    				biomeType.toString().toLowerCase(), 
            		stateAddGen.getNumBiomes() > 0 && stateAddGen.generatesInBiome(biomeType),
            		new Observer() {
						
						@Override
						public void update(Observable arg0, Object arg1) {
							String[] typeValues;
					        ArrayList<String> valueList = new ArrayList<String>();
					        
					        for (GuiButtonCheckBox button : biomeCheckBoxes) {
								if(button.isChecked()) {
									stateAddGen.addBiome(button.displayString.toUpperCase());
								} else {
									stateAddGen.removeBiome(button.displayString.toUpperCase());
								}
							}							
						}
					});
    		biomeCheckBoxes.add(button);    		
    		buttonList.add(button);
		}
    	
    	// add button
    	buttonList.add(new GuiButtonOutlined(0,
			(width - bookImageWidth) / 2 + 38,
			(height - bookImageHeight) / 2 + 151,
			"add", new Observer() {
				
				@Override
				public void update(Observable o, Object arg) {
					if(!stateAddGen.getName().equals("") && !stateAddGen.getResourcePack().equals("")
							&& stateAddGen.getLevel() != -1) {
						
						PacketBuilder.sendAddGen(stateAddGen);
						stateAddGen = new Gen("", "");
						state = GEN_LIST_STATE;

						
					}
					
				}
			}));
    }
    
    private void disableLevelCheckBoxes() {
    	if(undergroundCheckBox != null) {
    		undergroundCheckBox.setChecked(false);
    	}
    	if(surfaceCheckBox != null) {
    		surfaceCheckBox.setChecked(false);
    	}
    	if(seaCheckBox != null) {
    		seaCheckBox.setChecked(false);
    	}
    }
    
    private void showGenView(final Gen gen, int offset) {
    	
    	String levelText;
    	String villageText;

    	int numBiomePages = (Type.values().length-1 + GEN_BIOME_LIST_MAX_LENGTH)/GEN_BIOME_LIST_MAX_LENGTH;
    	int numSegmentPages = ((gen.getNumSegments()+gen.getNumStartingSegments()-1 + GEN_SEGMENT_LIST_MAX_LENGTH)/GEN_SEGMENT_LIST_MAX_LENGTH);
    	
    	// make place for empty state message, empty state for biomes should not happen
    	if(numSegmentPages == 0) {
    		numSegmentPages = 1;
    	}
    	
    	MLogger.logf("types: %d biome: %d segm: %d", Type.values().length, numBiomePages, numSegmentPages);
    	
    	// title
    	drawList.add(new GuiText("Gen: " + gen.getName(), 38, 17));
    	
    	// title separator
    	drawList.add(GuiTexture.createDashedLineTexture(37, 26));
    	
    	// on the first page we draw some basic info
    	if(offset == 0) {
    		
    		// resource pack
    		drawList.add(new GuiText("rp: " + gen.getResourcePack(), 38, 30));
        	
        	// level
        	levelText = "level: ";
        	switch(gen.getLevel()) {
        	
        		case Gen.UNDERGROUND_LEVEL:
        			levelText += "underground";
        			break;
        		
        		case Gen.SEA_FLOOR_LEVEL:
        			levelText += "sea floor";
        			break;
        			
        		case Gen.SURFACE_LEVEL:
        			levelText += "surface";
        			break;
        	}
        	drawList.add(new GuiText(levelText, 38, 40));
        	
        	// village gen
//        	villageText = "village gen: ";
//        	if(gen.isVillageGen()) {
//        		villageText += "yes";
//        	} else {
//        		villageText += "no";
//        	}
//        	drawList.add(new GuiText(villageText, 38, 50));
        	
        	if(gen.getNumBiomes() == 0) {
        		drawList.add(new GuiText("biomes: all", 38, 50));
        	} else {
        		drawList.add(new GuiText("biomes: " + gen.getNumBiomes(), 38, 50));
        	}
        	
        	drawList.add(new GuiText("normal segments: " + gen.getNumSegments(), 38, 60));
        	drawList.add(new GuiText("starting segments: " + gen.getNumStartingSegments(), 38, 70));
        	
        	
        	
    	} else if(offset > 0 && offset < numSegmentPages + 1) {
    		
    		int listOffset = (offset-1) * GEN_SEGMENT_LIST_MAX_LENGTH;
    		int listLength = gen.getNumSegments() + gen.getNumStartingSegments();
    		MLogger.logf("o:%d l1:%d", listOffset, listLength);
    		if(listOffset + GEN_SEGMENT_LIST_MAX_LENGTH > listLength) {
    			listLength = listLength - listOffset;
    		} else {
    			listLength = GEN_SEGMENT_LIST_MAX_LENGTH;
    		}
    		//MLogger.logf("l2:%d", listLength);
    		
    		drawList.add(new GuiText("segments", 38, 30));
    		drawList.add(new GuiText("name", 38, 40));
    		drawList.add(new GuiText("#E", 116, 40, GuiText.RIGHT_ALIGN));
    		drawList.add(new GuiText("#TE", 138, 40, GuiText.RIGHT_ALIGN));
    		drawList.add(new GuiText("S", 148, 40, GuiText.RIGHT_ALIGN));
    		
    		if(listLength > 0) {
    			// list buttons based on offset
            	for (int i = 0; i < listLength; i++) {
            		Segment segment;
            		boolean isStart;
            		if(i + listOffset<gen.getNumStartingSegments()) {
            			segment = gen.getStartingSegment(i + listOffset);
            			isStart = true;
            		} else {
            			segment = gen.getSegment(i + listOffset - gen.getNumStartingSegments());
            			isStart = false;
            		}
            		
            		// separator
                	drawList.add(GuiTexture.createDashedLineTexture(37, 49 + i*16));
            		
                	// button
        			buttonList.add(new GuiButtonSegmentListItem(listOffset + i, 
        				(width - bookImageWidth) / 2 + 37,
                 		(height - bookImageHeight) / 2 + 50 + i*16,
        				segment.getName(), segment.getNumTileEntities(), segment.getNumTileEntities(), isStart));
        		}
    		} else {
    			// separator
            	drawList.add(GuiTexture.createDashedLineTexture(37, 49));
        		
            	// button
            	drawList.add(new GuiText("No segments yet", 38, 52));
    		}
    		
        	
    	} else { // on the other pages we draw the biomes
    		int listOffset = (offset-1-numSegmentPages) * GEN_BIOME_LIST_MAX_LENGTH;
    		
    		Type[] types = Type.values();
    		int listLength = types.length;
    		
    		// set item offset and list length
    		if(listOffset + GEN_BIOME_LIST_MAX_LENGTH > listLength) {
    			listLength = listLength - listOffset;
    		} else {
    			listLength = GEN_BIOME_LIST_MAX_LENGTH;
    		}
    		
    		drawList.add(new GuiText("biomes:", 38, 30));
    		
    		for (int i = 0; i < listLength; i++) {
        		int left = 0;
        		int top = 0;
        		Type biomeType = types[i + listOffset];
        		String biomeName = biomeType.toString().toLowerCase();
        		int color =  0x55000000;
        		
        		if(biomeName.length() > GEN_BIOME_STRING_MAX_LENGTH) {
        			biomeName = biomeName.substring(0, GEN_BIOME_STRING_MAX_LENGTH) + "...";
        		}
        		
        		if(i>listLength/2-1) {
        			left = 102;
        			top = (i-listLength/2)*10 + 40;
        		} else {
        			left = 48;
        			top = i*10 + 40;
        		}
        		
        		if(gen.generatesInBiome(biomeType) || gen.getNumBiomes() == 0) {
        			color = 0xff000000;
        			drawList.add(new GuiTexture(texture, left - 9, top + 1, 8, 8, 16, 60, 0, 0, 0));
        		}
        		
        		drawList.add(new GuiText(biomeName, left, top, color, GuiText.LEFT_ALIGN));
    		}

    	}
    	
    	// gen button
    	buttonList.add(new GuiButtonOutlined(0,
			(width - bookImageWidth) / 2 + 38,
			(height - bookImageHeight) / 2 + 151,
			"generate", new Observer() {
				
				@Override
				public void update(Observable o, Object arg) {
					MLogger.logf("generate: %s", gen.getName());
					PacketBuilder.getInstance().sendGenGenerationRequest(gen.getName(), gen.getResourcePack());
				}
			}));

    	// show next button
		if(offset < numBiomePages + numSegmentPages) {
			buttonList.add(new GuiButtonChangePage(2, offset + 1,
				(width - bookImageWidth) / 2 + 134,
				(height - bookImageHeight) / 2 + 153,
				true));
		}
		
		// back button
		if(offset>0) {
			buttonList.add(new GuiButtonChangePage(2, offset - 1,
				(width - bookImageWidth) / 2 + 95,
				(height - bookImageHeight) / 2 + 153,
				false));
		}
		
		// current page number
		drawList.add(new GuiText((offset + 1) + "/" + (numBiomePages + numSegmentPages + 1), 122, 155, GuiText.CENTER_ALIGN));
    }
    
    private void showSegmentView(final Segment segment, boolean isStart) {
    	
    	// title
    	drawList.add(new GuiText("Segment: " + segment.getName(), 38, 17));
    	
    	// title separator
    	drawList.add(GuiTexture.createDashedLineTexture(37, 26));
    	
    	drawList.add(new GuiText("gen: " + stateViewGen.getName(), 38, 30));
    	
    	drawList.add(new GuiText("pack: " + stateViewGen.getResourcePack(), 38, 40));
    	
    	drawList.add(new GuiText("start: " + (isStart ? "yes" : "no"), 38, 50));
    
    	buttonList.add(new GuiButtonOutlined(0,
    			(width - bookImageWidth) / 2 + 38,
    			(height - bookImageHeight) / 2 + 151,
    			"generate", 50, new Observer() {
    				
    				@Override
    				public void update(Observable o, Object arg) {
    					MLogger.logf("generate segment: %s", segment.getName());
    					PacketBuilder.sendSegmentGenerationRequest(segment.getName(), 
    							stateViewGen.getName(), stateViewGen.getResourcePack(), false);
    				}
    			}));
    	
    	buttonList.add(new GuiButtonOutlined(0,
    			(width - bookImageWidth) / 2 + 98,
    			(height - bookImageHeight) / 2 + 151,
    			"load", 50, new Observer() {
    				
    				@Override
    				public void update(Observable o, Object arg) {
    					MLogger.logf("load segment: %s", segment.getName());
    					PacketBuilder.sendSegmentGenerationRequest(segment.getName(), 
    							stateViewGen.getName(), stateViewGen.getResourcePack(), true);
    				}
    			}));
    }
    
    private void showSegmentAddView(String genName, String packName) {

    	
		Vec3 position = player.getPosition(0);
		int chunkX = (int) (position.xCoord)/16;
		int y = ((int) (position.yCoord)/16)*16;
		int chunkZ = (int) (position.zCoord)/16;
		
		// due to the division, negative coordinates end up being offset by one, this fixes that
		if(position.xCoord<0) {
			chunkX--;
		}
		if(position.zCoord<0) {
			chunkZ--;
		}
    	
    	stateAddViewSegment.parseFromWorld(player.worldObj, chunkX, y, chunkZ);
    	
    	if(stateSegmentAddViewPage == 0) {
    		drawList.add(new GuiText("Add Segment: data", 93, 17, GuiText.CENTER_ALIGN));
    		// name input
        	drawList.add(new GuiText("name:", 38, 27));
        	buttonList.add(new GuiButtonTextInput(0,
    			(width - bookImageWidth) / 2 + 65,
    			(height - bookImageHeight) / 2 + 27
    			, 80, stateAddViewSegment.getName(), new Observer() {
    				
    				@Override
    				public void update(Observable o, Object arg) {
    					stateAddViewSegment.setName((String)arg);
    					
    				}
    			}));
        	
        	// gen input
        	drawList.add(new GuiText("gen:", 44, 39));
        	buttonList.add(new GuiButtonTextInput(0,
        		(width - bookImageWidth) / 2 + 65,
        		(height - bookImageHeight) / 2 + 39
        		, 80, stateAddViewGen, new Observer() {
    				
    				@Override
    				public void update(Observable o, Object arg) {
    					stateAddViewGen = (String)arg;
    					
    				}
    			}));
        	
        	// resource pack input
        	drawList.add(new GuiText("rp:", 50, 51));
        	buttonList.add(new GuiButtonTextInput(0,
        		(width - bookImageWidth) / 2 + 65,
        		(height - bookImageHeight) / 2 + 51
        		, 80, stateAddViewPack, new Observer() {
    				
    				@Override
    				public void update(Observable o, Object arg) {
    					stateAddViewPack = (String)arg;
    					
    				}
    			}));
        	
        	// start checkbox
        	drawList.add(new GuiText("start:", 36, 63));
        	buttonList.add(new GuiButtonCheckBox(0,
        			(width - bookImageWidth) / 2 + 65,
            		(height - bookImageHeight) / 2 + 63,
            		"", stateAddViewIsStart, new Observer() {
    			
    			@Override
    			public void update(Observable o, Object arg) {
    				stateAddViewIsStart = (Boolean)arg;
    			}
    		}));
    	} else if(stateSegmentAddViewPage == 1) {
    		
    		drawList.add(new GuiText("Add Segment: interfaces", 93, 17, GuiText.CENTER_ALIGN));
    		
    		drawList.add(new GuiText("top: " + stateAddViewSegment.getInterface(0), 90, 27, GuiText.CENTER_ALIGN));
    		
    		drawList.add(new GuiText("bottom: " + stateAddViewSegment.getInterface(1), 90, 140, GuiText.CENTER_ALIGN));
    		
    		drawList.add(new GuiText("S: " + stateAddViewSegment.getInterface(4), 70, 41, GuiText.RIGHT_ALIGN));
    		
    		drawList.add(new GuiText("E: " + stateAddViewSegment.getInterface(3), 112, 41));
    		
    		drawList.add(new GuiText("N: " + stateAddViewSegment.getInterface(2), 112, 128));
    		
    		drawList.add(new GuiText("W: " + stateAddViewSegment.getInterface(5), 65, 128, GuiText.RIGHT_ALIGN));
    		
    		
    		
    		// block display
        	showSegment(stateAddViewSegment, 45, 110);
    	}
    	
    	// add segment button
    	buttonList.add(new GuiButtonOutlined(0,
			(width - bookImageWidth) / 2 + 38,
			(height - bookImageHeight) / 2 + 151,
			"add", new Observer() {
				
				@Override
				public void update(Observable o, Object arg) {
					if(!stateAddViewSegment.getName().equals("") && !stateAddViewGen.equals("")
							&& !stateAddViewPack.equals("")) {
						
						state = SEGMENT_VIEW_STATE;
						
						PacketBuilder.sendAddSegment(stateAddViewSegment.getName(),
							stateAddViewGen, stateAddViewPack, stateAddViewIsStart);
					}
					
				}
			}));
    	
    	// show next button
		if(stateSegmentAddViewPage < SEGMENT_PAGES_COUNT - 1) {
			buttonList.add(new GuiButtonChangePage(1, stateSegmentAddViewPage + 1,
				(width - bookImageWidth) / 2 + 134,
				(height - bookImageHeight) / 2 + 153,
				true));
		}
		
		// back button
		if(stateSegmentAddViewPage>0) {
			buttonList.add(new GuiButtonChangePage(1, stateSegmentAddViewPage - 1,
				(width - bookImageWidth) / 2 + 95,
				(height - bookImageHeight) / 2 + 153,
				false));
		}
		
		// current page number
		drawList.add(new GuiText((stateSegmentAddViewPage + 1) + "/" + SEGMENT_PAGES_COUNT, 122, 155, GuiText.CENTER_ALIGN));
    	
    }
    
    private void showUtilityView() {
    	drawList.add(new GuiText("Utility functions", 93, 17, GuiText.CENTER_ALIGN));
    	drawList.add(new GuiText("Generate templates:", 38, 30));
    	buttonList.add(new GuiButtonOutlined(0,
    			(width - bookImageWidth) / 2 + 38,
    			(height - bookImageHeight) / 2 + 40,
    			"corners", 50, new Observer() {
			
			@Override
			public void update(Observable arg0, Object arg1) {
				PacketBuilder.sendTemplateGeneration(Utilities.CORNER_TEMPLATE);
			}
		}));
    	
    	buttonList.add(new GuiButtonOutlined(0,
    			(width - bookImageWidth) / 2 + 98,
    			(height - bookImageHeight) / 2 + 40,
    			"edges", 50, new Observer() {
			
			@Override
			public void update(Observable arg0, Object arg1) {
				PacketBuilder.sendTemplateGeneration(Utilities.EDGE_TEMPLATE);
			}
		}));
    	
    	buttonList.add(new GuiButtonOutlined(0,
    			(width - bookImageWidth) / 2 + 38,
    			(height - bookImageHeight) / 2 + 65,
    			"faces", 50, new Observer() {
			
			@Override
			public void update(Observable arg0, Object arg1) {
				PacketBuilder.sendTemplateGeneration(Utilities.FACE_TEMPLATE);
			}
		}));
    	
    	buttonList.add(new GuiButtonOutlined(0,
    			(width - bookImageWidth) / 2 + 98,
    			(height - bookImageHeight) / 2 + 65,
    			"fill", 50, new Observer() {
			
			@Override
			public void update(Observable arg0, Object arg1) {
				PacketBuilder.sendTemplateGeneration(Utilities.FILL_TEMPLATE);
			}
		}));
    }
    
    private void showHelpView(int offset) {
    	drawList.add(new GuiText("Tutorial", 93, 17, GuiText.CENTER_ALIGN));
    	
    	switch(offset) {
	    	case 0:
	    		drawList.add(new GuiSplitText(Constants.TUTORIAL_INTRO, 36, 30, 116));
	    		break;
	    	case 1:
	    		drawList.add(new GuiText("Gens", 36, 30));
	    		drawList.add(new GuiLine(35, 38, fontRendererObj.getStringWidth("Gens"), true));
	    		
	    		drawList.add(new GuiSplitText(Constants.TUTORIAL_GEN1, 36, 40, 116));
	    		break;
	    	case 2:
	    		drawList.add(new GuiText("Gens", 36, 30));
	    		drawList.add(new GuiLine(35, 38, fontRendererObj.getStringWidth("Gens"), true));
	    		
	    		drawList.add(new GuiSplitText(Constants.TUTORIAL_GEN2, 36, 40, 116));
	    		break;
	    	case 3:
	    		drawList.add(new GuiText("Gens", 36, 30));
	    		drawList.add(new GuiLine(35, 38, fontRendererObj.getStringWidth("Gens"), true));
	    		
	    		drawList.add(new GuiSplitText(Constants.TUTORIAL_GEN3, 36, 40, 116));
	    		break;
    	}
    	
    	// show next button
		if(offset < HELP_PAGES_COUNT - 1) {
			buttonList.add(new GuiButtonChangePage(3, offset + 1,
				(width - bookImageWidth) / 2 + 134,
				(height - bookImageHeight) / 2 + 153,
				true));
		}
		
		// back button
		if(offset>0) {
			buttonList.add(new GuiButtonChangePage(3, offset - 1,
				(width - bookImageWidth) / 2 + 95,
				(height - bookImageHeight) / 2 + 153,
				false));
		}
		
		// current page number
		drawList.add(new GuiText((offset + 1) + "/" + HELP_PAGES_COUNT, 122, 155, GuiText.CENTER_ALIGN));
    }
    
    private void showSegment(Segment segment, int xOffset, int yOffset) {
    	
    	final ArrayList<Drawable> blockList = new ArrayList<Drawable>();
    	
    	Drawable blockDrawer = new Drawable() {
			
			@Override
			public void draw(int screenWidth, int screenHeight) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
				
				GL11.glPushMatrix();
		        GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
		        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
		        RenderHelper.enableStandardItemLighting();
		        GL11.glPopMatrix();
				
				for (Drawable drawable : blockList) {
					drawable.draw(screenWidth, screenHeight);
				}
				RenderHelper.disableStandardItemLighting();
			}
		};
    	blockList.clear();
    	//drawList.add(new GuiBlockModel(29, 80f, 0, 102, 1, 0));
    	for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					Block block = segment.getBlock(x, y, z);
					
					/*if(blockID == 146 || blockID == 54) {
						blockID = 0;
					} else if(!(z < 15-y || x > y)) {
						//blockID = 0;
					}*/
					
					blockList.add(new GuiBlockModel(
						xOffset + 2.8f * x + 2.8f * z,
						yOffset - 3.5f * y - 1.4f * x + 1.4f * z,
						(- x + z + y)*5+100,
						6.4f, block, segment.getBlockMetadata(x, y, z)));
					
				}
			}
		}
    	
    	// this drawable will draw all blocks
    	drawList.add(blockDrawer);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton pressedButton) {
    	if(pressedButton.id == GEN_LIST_STATE) {
    		PacketBuilder.sendGenListRequest();
    	}
    	if(pressedButton.id > 0) {
    		if(state != pressedButton.id) {
        		state = pressedButton.id;
        		showView(state);
        	}
    	} else {
    		if(pressedButton instanceof GuiButtonGenListItem) {
    			int index = ((GuiButtonGenListItem) pressedButton).getIndex();

    			// request gen data
    			PacketBuilder.sendGenRequest(genNames[index], packNames[index]);
    			
    			state = GEN_VIEW_STATE;
    				
    			// show the gen view if we have the data
    			if(stateViewGen != null && stateViewGen.getName().equals(genNames[index]) && stateViewGen.getName().equals(genNames[index])) {
    				showView(state);
    			}
    		} else if (pressedButton instanceof GuiButtonSegmentListItem) {
    			
    			// get segment name
    			String segmentName = ((GuiButtonSegmentListItem) pressedButton).getSegmentName();
    			
    			// get gen name
    			String genName = stateViewGen.getName();
    			
    			// get rp name
    			String packName = stateViewGen.getResourcePack();
    			
    			// set state
    			state = SEGMENT_VIEW_STATE;
    			
    			// send segment request
    			PacketBuilder.sendSegmentRequest(segmentName, genName, packName);
    			
    		} else if (pressedButton instanceof GuiButtonChangePage) {
    			GuiButtonChangePage button = (GuiButtonChangePage) pressedButton;
    			if(button.getPageID() == 0) {
    				stateGenListOffset = button.getOffset();
        			showView(state);
    			} else if(button.getPageID() == 1) {
    				stateSegmentAddViewPage = button.getOffset();
    				showView(state);
    			} else if(button.getPageID() == 2) {
    				stateViewGenIndex = button.getOffset();
    				showView(state);
    			} else if(button.getPageID() == 3) {
    				stateHelpOffset = button.getOffset();
    				showView(state);
    			} else if(button.getPageID() == 4) {
    				stateGenAddViewPage = button.getOffset();
    				showView(state);
    			}
    			
    		} else if(pressedButton instanceof GuiButtonTextInput) {
    			activeInput = (GuiButtonTextInput)pressedButton;
    			activeInput.setFocus(true);
    		}
    	}
    	
	}
    
    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
    	if(activeInput != null) {
			activeInput.setFocus(false);
			activeInput = null;
		}
    	super.mouseClicked(par1, par2, par3);
    }


    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(vanillaBookTexture);

        // draw background
        drawTexturedModalRect(
        		(width - bookImageWidth) / 2, // left
        		(height - bookImageHeight) / 2, // top
        		0, 0, this.bookImageWidth, this.bookImageHeight);
        
        // draw textures
        for (Drawable drawable : drawList) {
        	drawable.draw(width, height);
		}

        super.drawScreen(par1, par2, par3);
    }
    
    
    public void setGenListData(String names[], String[] packNames, int segmentCounts[]) {
    	genNames = names;
    	this.packNames = packNames;
    	genSegmentCounts = segmentCounts;
    	
    	if(Minecraft.getMinecraft().currentScreen != this) {
    		// TODO : can this bug out?
        	Minecraft.getMinecraft().displayGuiScreen(this);
    	} else if(state == GEN_LIST_STATE) {
    		showView(state);
    	}
    	
    }
    
    public void setGenData(Gen gen) {
    	stateViewGen = gen;
    	
    	if(state == GEN_VIEW_STATE) {
    		stateViewGenIndex = 0;
    		showView(GEN_VIEW_STATE);
    	}
    }
    
    public void setSegmentData(Segment segment, boolean isStart) {
    	stateviewSegment = segment;
    	stateViewSegmentIsStart = isStart;
    	
    	if(state == SEGMENT_VIEW_STATE) {
    		showView(SEGMENT_VIEW_STATE);
    	}
    }
    
    public static GuiScreenGenBook getInstance() {
		return instance;
	}
    
    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    protected void keyTyped(char character, int value) {
        super.keyTyped(character, value);
        if(activeInput != null) {
        	
        	
        	if(value == 14) { // remove a character if the user pressed backspace
        		String string = activeInput.getString();
        		if(string.length()>0) {
        			string = string.substring(0, string.length()-1);
            		activeInput.setString(string);
        		}
        	} else if(isCharacterAllowed(character)) {
        		activeInput.setString(activeInput.getString() + character);
        	}
        }
    }
	
	/**
     * Called from the main game loop to update the screen.
     */
	@Override
    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
	@Override
    public void initGui() {
    	showView(state); 
    	Keyboard.enableRepeatEvents(true);
    }
	
	/**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        activeInput = null;
        
    }
    
    
    
    @Override
    public boolean doesGuiPauseGame() {
    	return false;
    }
    
    public static boolean isCharacterAllowed(char c) {
    	return c >= '0' && c <= '9'
    		|| c >= 'A' && c <= 'Z'
    		|| c >= 'a' && c <= 'z';
    }

}
