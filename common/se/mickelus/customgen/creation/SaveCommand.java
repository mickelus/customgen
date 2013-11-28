package se.mickelus.customgen.creation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.parser.Entity;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.segment.SegmentStore;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class SaveCommand implements ICommand{

	public SaveCommand() {
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		return Constants.SAVE_CMD;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return Constants.SAVE_CMD + " <collection name> <name> [<x> <y> <z>]";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		EntityPlayer player;
		int x, y, z;
		
		if(!(icommandsender instanceof EntityPlayer)) {
			System.out.println("Cannot be used in the console.");
			return;
		} 
		
		player = (EntityPlayer) icommandsender;
		
		if(!(((EntityPlayer) icommandsender).capabilities.isCreativeMode)) {
		
			player = (EntityPlayer) icommandsender;
			player.addChatMessage("You need to be in creative mode to do this.");
			return;
		} else if(astring.length < 2){
			player.addChatMessage(getCommandUsage(icommandsender));
			return;
		}
		
		if(astring.length == 5) {
			x = Integer.parseInt(astring[2]);
			y = Integer.parseInt(astring[3]);
			z = Integer.parseInt(astring[4]);
		} else {
			x = player.chunkCoordX * 16;
			y = player.chunkCoordY * 16;
			z = player.chunkCoordZ * 16;
		}
		
		SegmentStore.addSegment(astring[0], astring[1], player.worldObj, x, y, z);
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender,
			String[] astring) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		// TODO Auto-generated method stub
		return false;
	}

}
