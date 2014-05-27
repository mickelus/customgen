package se.mickelus.customgen.network;

import java.io.IOException;
import java.util.Random;

import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.newstuff.ForgeGenerator;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.newstuff.GenManager;
import se.mickelus.customgen.segment.Segment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class SegmentGenerationRequestPacket extends AbstractPacket {
	
	private String segmentName;
	private String genName;
	private String packName;
	private boolean load;
	
	public SegmentGenerationRequestPacket() {
		
	}
	
	public SegmentGenerationRequestPacket(String segmentName, String genName, String packName, boolean load) {
		this.segmentName = segmentName;
		this.genName = genName;
		this.packName = packName;
		this.load = load;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {
			writeString(segmentName, buffer);
			
			writeString(genName, buffer);
			
			writeString(packName, buffer);
			
			buffer.writeBoolean(load);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {
			segmentName = readString(buffer);
			
			genName = readString(buffer);
			
			packName = readString(buffer);
			
			load = buffer.readBoolean();
		} catch (IOException e) {

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
		Segment segment;
		
		EntityPlayer ePlayer = (EntityPlayer) player;
		Vec3 position = ePlayer.getPosition(0);
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
		
		if(gen != null) {
			segment = gen.getSegmentByName(segmentName);
			
			if(segment != null) {
				ForgeGenerator.getInstance().generateSegment(chunkX, chunkZ, y,
						segment, ePlayer.worldObj, load, new Random());
			} else {
				MLogger.log("Found no matching segment.");
			}
		} else {
			MLogger.logf("Found no matching gen for gen: %s, pack: %s.", genName, packName);
		}
	}
}
