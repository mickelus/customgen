package se.mickelus.customgen.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class GenListRequestPacket extends AbstractPacket {
	
	public GenListRequestPacket() {
		
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// empty packet
		
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// empty packet
		
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// empty packet
		
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// empty packet
		
	}

}
