package se.mickelus.customgen.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import se.mickelus.customgen.gui.GuiScreenGenBook;
import se.mickelus.customgen.newstuff.Gen;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class GenResponsePacket extends AbstractPacket {
	
	private Gen gen;
	
	public GenResponsePacket() {
		
	}
	
	public GenResponsePacket(Gen gen) {
		this();
		this.gen = gen;
	}
	

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		NBTTagCompound nbt = gen.writeToNBT(false);
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);
			
		try {
			// write nbt to stream
			CompressedStreamTools.write(nbt, dataStream);
			byte[] bytes = byteStream.toByteArray();
			buffer.writeBytes(bytes);
			dataStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {

		byte[] bytes = new byte[buffer.readableBytes()];
		buffer.readBytes(bytes);
		
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		DataInputStream dataStream = new DataInputStream(byteStream);
		
		NBTTagCompound nbt;
		
		try {
			nbt = CompressedStreamTools.read(dataStream);
			gen = Gen.readFromNBT(nbt);
		} catch (IOException e) {
			
		}

	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		GuiScreenGenBook.getInstance().setGenData(gen);

	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// server will not receive this packet

	}

}
