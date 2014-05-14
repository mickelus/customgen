package se.mickelus.customgen.network;

import java.io.IOException;

import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.newstuff.GenManager;
import se.mickelus.customgen.segment.Segment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class SegmentRequestPacket extends AbstractPacket {

	private String segmentName;
	private String genName;
	private String packName;
	
	// empty constructor used on receiving end
	public SegmentRequestPacket() {

	}

	public SegmentRequestPacket(String segmentName, String genName,
			String packName) {
		this.segmentName = segmentName;
		this.genName = genName;
		this.packName = packName;
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
		} catch (IOException e) {
			MLogger.log("An error occured when sending the segment request.");
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
		// get gen
		Gen gen = GenManager.getInstance().getGenByName(genName, packName);
		Segment segment = null;
		boolean isStart = false;
		
		if(gen != null) {
			for (int i = 0; i < gen.getNumSegments(); i++) {
				if(gen.getSegment(i).getName().equals(segmentName)) {
					segment = gen.getSegment(i);
					break;
				}
			}
			
			for (int i = 0; i < gen.getNumStartingSegments(); i++) {
				if(gen.getStartingSegment(i).getName().equals(segmentName)) {
					segment = gen.getStartingSegment(i);
					isStart = true;
					break;
				}
			}
			
			if(segment!= null) {
				PacketBuilder.sendSegmentResponse(player, segment, isStart);
			} else {
				MLogger.log("Found no matching segment.");
			}
		} else {
			MLogger.logf("Found no matching gen for gen: %s, pack: %s.", genName, packName);
		}
	}

}
