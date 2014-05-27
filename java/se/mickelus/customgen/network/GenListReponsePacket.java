package se.mickelus.customgen.network;

import java.io.IOException;

import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.gui.GuiScreenGenBook;
import se.mickelus.customgen.newstuff.Gen;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class GenListReponsePacket extends AbstractPacket {
	
	private String[] genNames;
	private String[] packNames;
	private int[] segmentCounts;
	
	// empty constructor used on receiving end
	public GenListReponsePacket() {

	}
	
	public GenListReponsePacket(String[] genNames , String[] packNames, int[] segmentCounts) {
		this.genNames = genNames;
		this.packNames = packNames;
		this.segmentCounts = segmentCounts;
	}


	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		
		int length = genNames.length;
		buffer.writeInt(length);

		try {
			// for each gen
			for (int i = 0; i < length; i++) {

				// write its name
				writeString(genNames[i], buffer);
				
				// write resource pack name
				writeString(packNames[i], buffer);
				
				// write the amount of segments in this gen
				buffer.writeInt(segmentCounts[i]);
			}
		} catch (IOException e) {
			MLogger.log("Failed to encode genlist packet.");
			buffer.clear();
			buffer.writeInt(0);
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// read and store length of list
		int length = buffer.readInt();
		
		genNames = new String[length];
		packNames = new String[length];
		segmentCounts = new int[length];

		try {
			// for each gen
			for (int i = 0; i < length; i++) {
				
				// read its name
				genNames[i] = readString(buffer);
				
				// read the resource pack name
				packNames[i] = readString(buffer);
				
				// read the amount of segments in this gen
				segmentCounts[i] = buffer.readInt();
			}
		} catch (IOException e) {
			MLogger.log("Failed to decode genlist packet.");
			genNames = new String[0];
			packNames = new String[0];
			segmentCounts = new int[0];
		}
		
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		GuiScreenGenBook.getInstance().setGenListData(genNames, packNames, segmentCounts);
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// this should never be called on the server
		
	}

}
