package se.mickelus.customgen.network;

import java.io.IOException;
import java.util.Random;

import se.mickelus.customgen.ForgeGenerator;
import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.models.Gen;
import se.mickelus.customgen.models.GenManager;
import se.mickelus.customgen.models.Segment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

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
			segment = gen.getSegmentByName(segmentName);
			
			if(segment != null) {
				ForgeGenerator.getInstance().generateSegment(chunkX, chunkZ, y,
						segment, player.worldObj, load, new Random());
			} else {
				MLogger.log("Found no matching segment.");
			}
		} else {
			MLogger.logf("Found no matching gen for gen: %s, pack: %s.", genName, packName);
		}
	}
}
