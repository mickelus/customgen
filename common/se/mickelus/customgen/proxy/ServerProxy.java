package se.mickelus.customgen.proxy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ServerProxy extends Proxy {
	
	private static ServerProxy instance;
	
	public ServerProxy() {
		instance = this;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
	}

}
