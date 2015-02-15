package se.mickelus.customgen.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.gui.GuiScreenGenBook;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.segment.Segment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class SegmentResponsePacket extends AbstractPacket {
	
	private Segment segment;
	private boolean isStart;
	
	// empty constructor used on receiving end
		public SegmentResponsePacket() {

		}

		public SegmentResponsePacket(Segment segment, boolean isStart) {
			this.segment = segment;
			this.isStart = isStart;
		}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {

		NBTTagCompound nbt;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);
			
		try {
			nbt = segment.writeToNBT(false);
			buffer.writeBoolean(isStart);
			
			// write nbt to stream
			CompressedStreamTools.write(nbt, dataStream);
			byte[] bytes = byteStream.toByteArray();
			buffer.writeBytes(bytes);
			dataStream.close();
			
		} catch (IOException e) {
			MLogger.logf("Failed to encode response for segment %s", segment.getName());
			e.printStackTrace();
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		
		isStart = buffer.readBoolean();
		
		byte[] bytes = new byte[buffer.readableBytes()];
		buffer.readBytes(bytes);
		
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		DataInputStream dataStream = new DataInputStream(byteStream);
		
		NBTTagCompound nbt;
		
		try {
			nbt = CompressedStreamTools.read(dataStream);
			segment = Segment.readFromNBT(nbt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		GuiScreenGenBook.getInstance().setSegmentData(segment, isStart);
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

}
