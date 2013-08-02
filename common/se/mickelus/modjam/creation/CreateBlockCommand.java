package se.mickelus.modjam.creation;

import java.util.List;

import se.mickelus.modjam.Constants;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CreateBlockCommand implements ICommand {

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		return Constants.CREATE_BLOCK_CMD;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return Constants.CREATE_BLOCK_CMD + " <x> <y> <z>";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		if(astring.length != 4) {
			if(icommandsender instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) icommandsender;
				player.addChatMessage(getCommandUsage(icommandsender));
			}
			return;
		}
		else {
			try {
				int x = Integer.parseInt(astring[0]);
				int y = Integer.parseInt(astring[1]);
				int z = Integer.parseInt(astring[2]);
				int blockID = Integer.parseInt(astring[3]);

				int[][][] shape = new int[16][16][16];
				for(int sx = 0; sx < shape.length; sx++) {
					for(int sy = 0; sy < shape[sx].length; sy++){
						for(int sz = 0; sz < shape[sx][sy].length; sz++) {
							shape[sx][sy][sz] = blockID;
						}
					}
				}

				for(int sx = 0; sx < shape.length; sx++) {
					for(int sy = 0; sy < shape[sx].length; sy++){
						for(int sz = 0; sz < shape[sx][sy].length; sz++) {
							MinecraftServer.getServer().getConfigurationManager().
							getPlayerForUsername(icommandsender.getCommandSenderName()).
							worldObj.setBlock(x+sx, y+sy, z+sz, shape[sx][sy][sz]);
						}
					}
				}
			}
			catch(NumberFormatException e) {
				if(icommandsender instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) icommandsender;
					player.addChatMessage("Only numbers allowed");
				}
			}

		}

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
