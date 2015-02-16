package se.mickelus.customgen.network;

import java.io.IOException;

import se.mickelus.customgen.FileHandler;
import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.models.Gen;
import se.mickelus.customgen.models.GenManager;
import se.mickelus.customgen.models.Segment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class SegmentAddRequestPacket extends AbstractPacket {

	private String segmentName;
	private String genName;
	private String packName;
	private boolean isStart;

	// empty constructor used on receiving end
	public SegmentAddRequestPacket() {

	}

	public SegmentAddRequestPacket(String segmentName, String genName,
			String packName, boolean isStart) {
		this.segmentName = segmentName;
		this.genName = genName;
		this.packName = packName;
		this.isStart = isStart;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {
			// write segment name
			writeString(segmentName, buffer);
			
			// write gen name
			writeString(genName, buffer);
				
			// write pack name
			writeString(packName, buffer);
			
			buffer.writeBoolean(isStart);
		} catch (IOException e) {
			MLogger.log("An error occured when sending the segment add request.");
			e.printStackTrace();
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {
			// write segment name
			segmentName = readString(buffer);
			
			// write gen name
			genName = readString(buffer);
				
			// write pack name
			packName = readString(buffer);
			
			isStart = buffer.readBoolean();
		} catch (IOException e) {
			MLogger.log("An error occured when reading the segment add request.");
			e.printStackTrace();
		}
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// nothing to handle on the clientside

	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		GenManager genManager = GenManager.getInstance();
		Segment segment;
		Gen gen;
		World world = player.worldObj;

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
		
		segment = new Segment(segmentName);
		gen = genManager.getGenByName(genName, packName);
		if(gen != null) {
			segment.parseFromWorld(world, chunkX, y, chunkZ);
			
			gen.addSegment(segment, isStart);
			FileHandler.saveGenToFile(gen);
			
			// send segment add response with this segment
			PacketBuilder.sendGenResponse(gen, player);
			PacketBuilder.sendSegmentResponse(player, segment, isStart);
			PacketBuilder.sendGenListResponse(player);
		}
	}

}
