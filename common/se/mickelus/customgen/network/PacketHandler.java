package se.mickelus.customgen.network;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.logging.Logger;

import se.mickelus.customgen.Constants;
import se.mickelus.customgen.Customgen;
import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.gui.GuiScreenGenBook;
import se.mickelus.customgen.newstuff.FileHandler;
import se.mickelus.customgen.newstuff.ForgeGenerator;
import se.mickelus.customgen.newstuff.Gen;
import se.mickelus.customgen.newstuff.GenManager;
import se.mickelus.customgen.newstuff.Utilities;
import se.mickelus.customgen.proxy.ClientProxy;
import se.mickelus.customgen.proxy.Proxy;
import se.mickelus.customgen.segment.Segment;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;


public class PacketHandler{
	
	private static PacketHandler instance;
	
	public PacketHandler() {
		instance = this;
	}
	
	public static PacketHandler getInstance() {
		return instance;
	}
	
	public static void sendGenListRequest() {
		GenListRequestPacket packet = new GenListRequestPacket();
		Customgen.packetPipeline.sendToServer(packet);
	}
	
	public static void sendGenListResponse(EntityPlayer player) {
		// send arrays of gen data to client
		GenManager genManager = GenManager.getInstance();
		int numGens = genManager.getNumGens();
		String[] genNames = new String[numGens];
		String[] packNames = new String[numGens];
		int[] segmentCounts = new int[numGens];
		
		for (int i = 0; i < numGens; i++) {
			Gen gen = genManager.getGenByIndex(i);
			genNames[i] = gen.getName();
			packNames[i] = gen.getResourcePack();
			segmentCounts[i] = gen.getNumSegments() + gen.getNumStartingSegments();
		}
		
		// creates and sends gen list packet
		GenListReponsePacket packet = new GenListReponsePacket(genNames, packNames, segmentCounts);
		Customgen.packetPipeline.sendTo(packet, (EntityPlayerMP)player);
	}
	
	
	/**
	 * Sends a gen request to the server.
	 * @param genName
	 * @param packName
	 */
	public static void sendGenRequest(String genName, String packName) {
		GenRequestPacket packet = new GenRequestPacket(genName, packName);
		Customgen.packetPipeline.sendToServer(packet);
	}
	
	public static void sendGenResponse(Gen gen, EntityPlayer player) {
		
		

		GenResponsePacket packet = new GenResponsePacket(gen);
		Customgen.packetPipeline.sendTo(packet, (EntityPlayerMP) player);
		
	}
	
	private void handleGenResponse(DataInputStream stream) throws IOException {
		Gen gen;
		NBTTagCompound nbt;

		// read nbt from stream
		nbt = CompressedStreamTools.read(stream);
		
		// convert nbt to gen
		gen = Gen.readFromNBT(nbt);
		
		// feed gui with data
		GuiScreenGenBook.getInstance().setGenData(gen);
		
	}
	
	public static void sendAddGen(Gen gen) {
		GenAddRequestPacket packet = new GenAddRequestPacket(gen);
		Customgen.packetPipeline.sendToServer(packet);
	}
	
	private void handleGenAddPacket(DataInputStream stream, Player player) throws IOException {
		Gen gen;
		NBTTagCompound nbt;
		GenManager genManager = GenManager.getInstance();

		// read nbt from stream
		nbt = CompressedStreamTools.read(stream);
		
		// convert nbt to gen
		gen = Gen.readFromNBT(nbt);
		
		// add or update this gen in the GenManager
		genManager.addGen(gen);
		
		// save the new or updated gen
		FileHandler.saveGenToFile(genManager.getGenByName(gen.getName(), gen.getResourcePack()));
		
		
		Gen[] gens = new Gen[genManager.getNumGens()];
		for (int i = 0; i < gens.length; i++) {
			gens[i] = genManager.getGenByIndex(i);
		}
		PacketHandler.sendGenList(gens, (Player)player);
		
	}
	
	public static void sendAddSegment(String segmentName, String genName, String packName, boolean isStart) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);

		try {
			// write packet type
			dataStream.writeByte(SEGMENT_ADD_PACKET);

			// write segment name
			dataStream.writeChars(segmentName);
			dataStream.writeChar(0);
			
			// write gen name
			dataStream.writeChars(genName);
			dataStream.writeChar(0);
						
			// write pack name
			dataStream.writeChars(packName);
			dataStream.writeChar(0);
			
			dataStream.writeBoolean(isStart);
			
			dataStream.close();
			
			// send packet
			PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(Constants.CHANNEL, byteStream.toByteArray()));
			
		}catch(IOException e) {
			MLogger.log("An error occured when sending the gen request packet.");
			e.printStackTrace();
		}
		
	}
	
	private void handleSegmentAddPacket(DataInputStream stream, Player player) throws IOException {
		GenManager genManager = GenManager.getInstance();
		Segment segment;
		Gen gen;
		boolean isStart;
		World world = ((EntityPlayer) player).worldObj;
		Vec3 position = ((EntityPlayer) player).getPosition(0);
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
		
		segment = new Segment(readString(stream));
		gen = genManager.getGenByName(readString(stream), readString(stream));
		isStart = stream.readBoolean();
		if(gen != null) {
			segment.parseFromWorld(world, chunkX, y, chunkZ);
			
			gen.addSegment(segment, isStart);
			FileHandler.saveGenToFile(gen);
			
			// send segment request response with this segment
		}
		
	}
	
	public static void sendTemplateGeneration(int templateID) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);

		try {
			// write packet type
			dataStream.writeByte(TEMPLATE_PACKET);

			// write template id
			dataStream.writeInt(templateID);
			
			dataStream.close();
			
			// send packet
			PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(Constants.CHANNEL, byteStream.toByteArray()));
			
		}catch(IOException e) {
			MLogger.log("An error occured when sending the gen request packet.");
			e.printStackTrace();
		}
		
	}
	
	private void handleTemplateGeneration(DataInputStream stream, Player player) throws IOException {
		// read template id
		int templateID = stream.readInt();
		
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
				
		Utilities.generateTemplate(chunkX, chunkZ, y, ePlayer.getEntityWorld(), templateID);
	}
	
	public static void sendSegmentRequest(String segmentName, String genName, String packName) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);

		try {
			// write packet type
			dataStream.writeByte(SEGMENT_REQUEST_PACKET);

			// write strings
			dataStream.writeChars(segmentName);
			dataStream.writeChar(0);
			
			dataStream.writeChars(genName);
			dataStream.writeChar(0);
			
			dataStream.writeChars(packName);
			dataStream.writeChar(0);
			
			
			dataStream.close();
			
			// send packet
			PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(Constants.CHANNEL, byteStream.toByteArray()));
			
		}catch(IOException e) {
			MLogger.log("An error occured when sending the gen request packet.");
			e.printStackTrace();
		}
	}
	
	private void handleSegmentRequest(DataInputStream stream, Player player) throws IOException {
		// read strings
		String segmentName = readString(stream);
		String genName = readString(stream);
		String packName = readString(stream);
		boolean isStart = false;
		
		// get gen
		Gen gen = GenManager.getInstance().getGenByName(genName, packName);
		
		Segment segment = null;
		
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
				sendSegmentResponse(player, segment, isStart);
			} else {
				MLogger.log("Found no matching segment.");
			}
		} else {
			MLogger.logf("Found no matching gen for gen: %s, pack: %s.", genName, packName);
		}
		
		
	}
	
	private void sendSegmentResponse(Player player, Segment segment, boolean isStart) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);
		NBTTagCompound nbt = new NBTTagCompound();

		try {
			// write packet type
			dataStream.writeByte(SEGMENT_RESPONSE_PACKET);
			
			dataStream.writeBoolean(isStart);

			// convert segment to nbt
			nbt = segment.writeToNBT(false);
			
			// write nbt to stream
			CompressedStreamTools.write(nbt, dataStream);
			
			
			dataStream.close();
			
			// send packet
			PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket(Constants.CHANNEL, byteStream.toByteArray()), player);
			
		}catch(IOException e) {
			MLogger.log("An error occured when sending the gen request packet.");
			e.printStackTrace();
		}
	}
	
	private void handleSegmentResponse(DataInputStream stream, Player player) throws IOException {
		NBTTagCompound nbt;
		boolean isStart;
		Segment segment;
		
		
		// read start boolean
		isStart = stream.readBoolean();
		
		// read segment nbt
		nbt = CompressedStreamTools.read(stream);
		
		// create segment
		segment = Segment.readFromNBT(nbt);
		
		// notify gui
		GuiScreenGenBook.getInstance().setSegmentData(segment, isStart);
	}
	
	public static void sendSegmentGenerationRequest(String segmentName, String genName, String packName, boolean load) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);

		try {
			// write packet type
			dataStream.writeByte(SEGMENT_GENERATE_PACKET);

			// write strings
			dataStream.writeChars(segmentName);
			dataStream.writeChar(0);
			
			dataStream.writeChars(genName);
			dataStream.writeChar(0);
			
			dataStream.writeChars(packName);
			dataStream.writeChar(0);
			
			dataStream.writeBoolean(load);
			
			dataStream.close();
			
			// send packet
			PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(Constants.CHANNEL, byteStream.toByteArray()));
			
		}catch(IOException e) {
			MLogger.log("An error occured when sending the gen request packet.");
			e.printStackTrace();
		}
	}
	
	private void handleSegmentGenerationRequest(DataInputStream stream, Player player) throws IOException {
		String segmentName = readString(stream);
		String genName = readString(stream);
		String packName = readString(stream);
		boolean load = stream.readBoolean();
		
		MLogger.logf("load %b", load);
		
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
	
	public static void sendGenGenerationRequest(String genName, String packName) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);

		try {
			// write packet type
			dataStream.writeByte(GEN_GENERATE_PACKET);

			// write strings
			dataStream.writeChars(genName);
			dataStream.writeChar(0);
			
			dataStream.writeChars(packName);
			dataStream.writeChar(0);

			dataStream.close();
			
			// send packet
			PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(Constants.CHANNEL, byteStream.toByteArray()));
			
		}catch(IOException e) {
			MLogger.log("An error occured when sending the gen request packet.");
			e.printStackTrace();
		}
	}
	
	private void handleGenGenerationRequest(DataInputStream stream, Player player) throws IOException {
		String genName = readString(stream);
		String packName = readString(stream);
		
		Gen gen = GenManager.getInstance().getGenByName(genName, packName);
		
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
			ForgeGenerator.getInstance().generateGen(chunkX, chunkZ, ePlayer.worldObj, gen, new Random());
		} else {
			MLogger.logf("Found no matching gen for gen: %s, pack: %s.", genName, packName);
		}
	}
	
	private String readString(DataInput input) throws IOException{
		String string = "";
		char c = input.readChar();
		
		while(c != '\0') {
			string += c;
			c = input.readChar();
		}
		
		return string;
	}

}
