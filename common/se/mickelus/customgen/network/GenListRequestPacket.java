package se.mickelus.customgen.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class GenListRequestPacket extends AbstractPacket {
	
	// empty constructor used on receiving end
	public GenListRequestPacket() {
		
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// this request has no body

	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// no body to decode

	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// clients should not receive this

	}

	-@Override
	public void handleServerSide(EntityPlayer player) {
		// send gen list to requesting player

	}

}
