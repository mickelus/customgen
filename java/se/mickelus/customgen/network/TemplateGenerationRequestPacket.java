package se.mickelus.customgen.network;

import se.mickelus.customgen.newstuff.Utilities;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class TemplateGenerationRequestPacket extends AbstractPacket {
	
	private int templateID;
	
	public TemplateGenerationRequestPacket() {
		
	}
	
	public TemplateGenerationRequestPacket(int templateID) {
		this.templateID = templateID;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeInt(templateID);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		templateID = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
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
		
		Utilities.generateTemplate(chunkX, chunkZ, y, player.getEntityWorld(), templateID);
	}

}
