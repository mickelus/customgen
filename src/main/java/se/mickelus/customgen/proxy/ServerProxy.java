package se.mickelus.customgen.proxy;


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
