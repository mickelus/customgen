package se.mickelus.customgen.network;

import se.mickelus.customgen.newstuff.Utilities;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

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
		
		Utilities.generateTemplate(chunkX, chunkZ, y, player.getEntityWorld(), templateID);
	}

}
