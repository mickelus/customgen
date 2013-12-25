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
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {
	
	public static final int GENLIST_PACKET = 0;
	public static final int GEN_REQUEST_PACKET = 1;
	public static final int GEN_RESPONSE_PACKET = 2;
	public static final int GEN_ADD_PACKET = 3;
	public static final int TEMPLATE_PACKET = 4;
	public static final int SEGMENT_ADD_PACKET = 5;
	public static final int GENLIST_REQUEST_PACKET = 6;
	public static final int SEGMENT_REQUEST_PACKET = 7;
	public static final int SEGMENT_RESPONSE_PACKET = 8;
	public static final int SEGMENT_GENERATE_PACKET = 9;
	public static final int GEN_GENERATE_PACKET = 10;
	
	private static PacketHandler instance;
	
	public PacketHandler() {
		instance = this;
	}
	
	public static PacketHandler getInstance() {
		return instance;
	}

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		
		
		
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		try {
			byte type = stream.readByte();
			
			MLogger.log("got packet, type: " + type);

			switch (type) {
				case GENLIST_PACKET:
					handleGenListPacket(stream);
					break;
					
				case GEN_REQUEST_PACKET:
					handleGenRequest(stream, player);
					break;
					
				case GEN_RESPONSE_PACKET:
					handleGenResponse(stream);
					break;
					
				case GEN_ADD_PACKET:
					handleGenAddPacket(stream, player);
					break;
				case TEMPLATE_PACKET:
					handleTemplateGeneration(stream, player);
					break;
					
				case SEGMENT_ADD_PACKET:
					handleSegmentAddPacket(stream, player);
					break;
				
				case GENLIST_REQUEST_PACKET:
					handleGenListRequestPacket(player);
					break;
					
				case SEGMENT_REQUEST_PACKET:
					handleSegmentRequest(stream, player);
					break;
					
				case SEGMENT_RESPONSE_PACKET:
					handleSegmentResponse(stream, player);
					break;
				
				case SEGMENT_GENERATE_PACKET:
					handleSegmentGenerationRequest(stream, player);
					break;
					
				case GEN_GENERATE_PACKET:
					handleGenGenerationRequest(stream, player);
					break;
					
				default:
					MLogger.logf("Retreived invalid packet %d", type);
					break;
			}
			
			stream.close();
			
		} catch (IOException e) {
			MLogger.log("An error occured when reading a packet.");
			e.printStackTrace();
		}
		
	}
	
	public static void sendGenListRequest() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);

		try {
			dataStream.writeByte(GENLIST_REQUEST_PACKET);
			
			dataStream.close();
			
			PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(Constants.CHANNEL, byteStream.toByteArray()));

		}catch(IOException e) {
			MLogger.log("An error occured when sending the genlist request packet.");
			e.printStackTrace();
		}
	}
	
	private void handleGenListRequestPacket(Player player) {

		GenManager genManager = GenManager.getInstance();
		Gen[] gens = new Gen[genManager.getNumGens()];
		for (int i = 0; i < gens.length; i++) {
			gens[i] = genManager.getGenByIndex(i);
		}
		
		sendGenList(gens, player);
	}
	
	public static void sendGenList(Gen[] gens, Player player) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);

		try {
			dataStream.writeByte(GENLIST_PACKET);
			
			dataStream.writeInt(gens.length);
			
			for (int i = 0; i < gens.length; i++) {
				dataStream.writeChars(gens[i].getName());
				dataStream.writeChar(0);
				dataStream.writeChars(gens[i].getResourcePack());
				dataStream.writeChar(0);
				dataStream.writeInt(gens[i].getNumSegments() + gens[i].getNumStartingSegments());
			}
			
			dataStream.close();
			
			PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket(Constants.CHANNEL, byteStream.toByteArray()), player);

		}catch(IOException e) {
			MLogger.log("An error occured when sending the genlist packet.");
			e.printStackTrace();
		}
	}
	
	private void handleGenListPacket(DataInputStream stream) throws IOException {
		
		// read and store length of list
		int length = stream.readInt();
		
		String[] genNames = new String[length];
		String[] packNames = new String[length];
		int[] segmentCounts = new int[length];

		// for each gen
		for (int i = 0; i < length; i++) {
			
			// read its name
			genNames[i] = readString(stream);
			
			// read the resource pack name
			packNames[i] = readString(stream);
			
			// read the amount of segments in this gen
			segmentCounts[i] = stream.readInt();
			
		}
		
		GuiScreenGenBook.getInstance().setGenListData(genNames, packNames, segmentCounts);
	}
	
	/**
	 * Sends a gen request to the server.
	 * @param genName
	 * @param resourcePack
	 */
	public static void sendGenRequest(String genName, String resourcePack) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);

		try {
			// write packet type
			dataStream.writeByte(GEN_REQUEST_PACKET);

			// write gen name
			dataStream.writeChars(genName);
			dataStream.writeChar(0);
			
			// write resource pack name
			dataStream.writeChars(resourcePack);
			dataStream.writeChar(0);
			
			dataStream.close();
			
			// send packet
			PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(Constants.CHANNEL, byteStream.toByteArray()));
			
		}catch(IOException e) {
			MLogger.log("An error occured when sending the gen request packet.");
			e.printStackTrace();
		}
		
	}
	
	private void handleGenRequest(DataInputStream inStream, Player player) throws IOException {
		String genName;
		String packName;
		
		Gen gen;
		NBTTagCompound nbt;
		
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		DataOutputStream outDataStream = new DataOutputStream(outByteStream);
		
		// read request
		genName = readString(inStream);
		packName = readString(inStream);

		// get requested gen
		gen = GenManager.getInstance().getGenByName(genName, packName);
		
		// convert gen to nbt
		nbt = gen.writeToNBT(false);
		
		
		// write packet type
		outDataStream.writeByte(GEN_RESPONSE_PACKET);
		
		// write nbt to stream
		CompressedStreamTools.write(nbt, outDataStream);
		
		// send response packet
		PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket(Constants.CHANNEL, outByteStream.toByteArray()), player);
		
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
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);
		NBTTagCompound nbt;

		try {
			// convert gen to nbt
			nbt = gen.writeToNBT(true);
			
			// write packet type
			dataStream.writeByte(GEN_ADD_PACKET);
			
			// write nbt to stream
			CompressedStreamTools.write(nbt, dataStream);
			
			// send response packet
			PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(Constants.CHANNEL, byteStream.toByteArray()));
			
		}catch(IOException e) {
			MLogger.log("An error occured when sending the gen request packet.");
			e.printStackTrace();
		}
		
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
						segment, ePlayer.worldObj, load);
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
