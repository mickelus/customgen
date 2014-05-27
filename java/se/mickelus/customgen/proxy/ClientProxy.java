package se.mickelus.customgen.proxy;

import se.mickelus.customgen.gui.GuiScreenGenBook;

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
