package se.mickelus.customgen.proxy;

import se.mickelus.customgen.MLogger;
import se.mickelus.customgen.gui.GuiScreenGenBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ClientProxy extends Proxy {

	private static ClientProxy instance;
	
	public ClientProxy() {
		instance = this;
	}

	@Override
	public void init() {
		new GuiScreenGenBook();
	}

}
