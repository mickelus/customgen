package se.mickelus.customgen.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.newstuff.FileHandler;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.newstuff.GenManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;

public class GenAddRequestPacket extends AbstractPacket {
	
	private Gen gen;
	
	public GenAddRequestPacket() {
		
	}
	
	public GenAddRequestPacket(Gen gen) {
		this();
		this.gen = gen;
		System.out.println("BIOMES REQ: " + gen.getNumBiomes());
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		System.out.println("ENCODING ADD REQUEST");
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
		System.out.println("DECODING ADD REQUEST");
		
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
		System.out.println("got gen: " + gen);
		System.out.println("BIOMES GOT: " + gen.getNumBiomes());

		GenManager genManager = GenManager.getInstance();
		
		// add or update this gen in the GenManager
		genManager.addGen(gen);
		
		// save the new or updated gen
		FileHandler.saveGenToFile(genManager.getGenByName(gen.getName(), gen.getResourcePack()));
		
		// send list packet to player
		PacketHandler.getInstance().sendGenListResponse(player);
		
	}

}
