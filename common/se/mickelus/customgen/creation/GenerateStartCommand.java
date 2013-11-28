package se.mickelus.customgen.creation;

import java.util.List;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.generation.GenerationHandler;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class GenerateStartCommand implements ICommand {

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		return Constants.GEN_CMD;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return Constants.GEN_CMD + " [<chunkx> <y> <chunkz>]";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		int x = 0, y = 0, z = 0;
		EntityPlayer player = null;
		
		if(!(icommandsender instanceof EntityPlayer)) {
			return;
		}
		
		player = (EntityPlayer) icommandsender;
		
		if (!player.capabilities.isCreativeMode) {
			player.addChatMessage("You need to be in creative mode to do this.");
			return;
		}
		
		if(astring.length != 4) {
			x = player.chunkCoordX;
			y = player.chunkCoordY;
			z = player.chunkCoordZ;
		}
		else {
			try {
				x = Integer.parseInt(astring[0]);
				y = Integer.parseInt(astring[1]);
				z = Integer.parseInt(astring[2]);
			} catch(NumberFormatException e) {
					player.addChatMessage("Only numbers allowed");
					return;
			}
		}
		
		GenerationHandler.getInstance().generateStart(x, y, z, player.worldObj);
			
		

	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
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
