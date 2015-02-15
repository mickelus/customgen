package se.mickelus.customgen.network;

import java.io.IOException;

import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.newstuff.GenManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class GenRequestPacket extends AbstractPacket {
	
	private String genName;
	private String packName;
	
	public GenRequestPacket() {
		
	}
	
	public GenRequestPacket(String genName, String packName) {
		this();
		
		this.genName = genName;
		this.packName = packName;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {
			writeString(genName, buffer);
			writeString(packName, buffer);
		} catch (IOException e) {
			MLogger.log("Failed to encode gen request.");
		}
		

	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {
			genName = readString(buffer);
			packName = readString(buffer);
		} catch (IOException e) {
			MLogger.log("Failed to decode gen request.");
		}

	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		Gen gen = GenManager.getInstance().getGenByName(genName, packName);
		if(gen != null) {
			PacketBuilder.sendGenResponse(gen, player);
		} else {
			MLogger.log("Invalid gen request received.");
		}
		

	}

}
