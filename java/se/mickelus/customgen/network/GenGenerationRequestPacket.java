package se.mickelus.customgen.network;

import java.io.IOException;
import java.util.Random;

import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.newstuff.ForgeGenerator;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.newstuff.GenManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class GenGenerationRequestPacket extends AbstractPacket {
	
	private String genName;
	private String packName;
	
	public GenGenerationRequestPacket() {

	}

	public GenGenerationRequestPacket(String genName, String packName) {
		this.genName = genName;
		this.packName = packName;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {			
			// write gen name
			writeString(genName, buffer);
				
			// write pack name
			writeString(packName, buffer);
		} catch (IOException e) {
			MLogger.log("An error occured when sending the segment request.");
			e.printStackTrace();
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {			
			// write gen name
			genName = readString(buffer);
				
			// write pack name
			packName = readString(buffer);
			
		} catch (IOException e) {
			MLogger.log("An error occured when reading the segment request.");
			e.printStackTrace();
		}
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		
		Gen gen = GenManager.getInstance().getGenByName(genName, packName);
		
		int chunkX = (int) (player.posX)/16;
		int y = ((int) (player.posY)/16)*16;
		int chunkZ = (int) (player.posZ)/16;
		
		// due to the division, negative coordinates end up being offset by one, this fixes that
		if(player.posX<0) {
			chunkX--;
		}
		if(player.posZ<0) {
			chunkZ--;
		}	
		
		if(gen != null) {
			ForgeGenerator.getInstance().generateGen(chunkX, chunkZ, player.worldObj, gen, new Random());
		} else {
			MLogger.logf("Found no matching gen for gen: %s, pack: %s.", genName, packName);
		}
	}

}
