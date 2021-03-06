package se.mickelus.customgen.network;

import se.mickelus.customgen.Customgen;
import se.mickelus.customgen.models.Gen;
import se.mickelus.customgen.models.GenManager;
import se.mickelus.customgen.models.Segment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;


public class PacketBuilder{
	
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
	
	public static void sendAddGen(Gen gen) {
		GenAddRequestPacket packet = new GenAddRequestPacket(gen);
		Customgen.packetPipeline.sendToServer(packet);
	}
	
	public static void sendAddSegment(String segmentName, String genName, String packName, boolean isStart) {
		SegmentAddRequestPacket packet = new SegmentAddRequestPacket(segmentName, genName, packName, isStart);
		Customgen.packetPipeline.sendToServer(packet);
		GenListRequestPacket hpacket = new GenListRequestPacket();
		Customgen.packetPipeline.sendToServer(hpacket);
	}

	
	public static void sendTemplateGeneration(int templateID) {
		TemplateGenerationRequestPacket packet = new TemplateGenerationRequestPacket(templateID);
		Customgen.packetPipeline.sendToServer(packet);	
	}
	
	public static void sendSegmentRequest(String segmentName, String genName, String packName) {
		SegmentRequestPacket packet = new SegmentRequestPacket(segmentName, genName, packName);
		Customgen.packetPipeline.sendToServer(packet);	
	}
	
	
	public static void sendSegmentResponse(EntityPlayer player, Segment segment, boolean isStart) {
		SegmentResponsePacket packet = new SegmentResponsePacket(segment, isStart);
		Customgen.packetPipeline.sendTo(packet, (EntityPlayerMP) player);
	}
	
	public static void sendSegmentGenerationRequest(String segmentName, String genName, String packName, boolean load) {
		SegmentGenerationRequestPacket packet = new SegmentGenerationRequestPacket(segmentName, genName, packName, load);
		Customgen.packetPipeline.sendToServer(packet);	
	}
	
	public static void sendGenGenerationRequest(String genName, String packName) {
		GenGenerationRequestPacket packet = new GenGenerationRequestPacket(genName, packName);
		Customgen.packetPipeline.sendToServer(packet);	
	}
}
