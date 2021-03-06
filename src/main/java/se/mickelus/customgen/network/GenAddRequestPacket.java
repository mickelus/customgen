package se.mickelus.customgen.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import se.mickelus.customgen.FileHandler;
import se.mickelus.customgen.models.Gen;
import se.mickelus.customgen.models.GenManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class GenAddRequestPacket extends AbstractPacket {
	
	private Gen gen;
	
	public GenAddRequestPacket() {
		
	}
	
	public GenAddRequestPacket(Gen gen) {
		this();
		this.gen = gen;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		NBTTagCompound nbt = gen.writeToNBT(true);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void handleServerSide(EntityPlayer player) {

		GenManager genManager = GenManager.getInstance();
		
		// add or update this gen in the GenManager
		genManager.addGen(gen);
		
		// save the new or updated gen
		FileHandler.saveGenToFile(genManager.getGenByName(gen.getName(), gen.getResourcePack()));
		
		// send list packet to player
		PacketBuilder.sendGenListResponse(player);
		
	}

}
