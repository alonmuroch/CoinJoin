package Core;

import java.net.SocketAddress;

public class Peer {

	SocketAddress addr;
	int version;
	byte[] onion;
	long timestamp = System.currentTimeMillis() / 1000L;
	boolean connected;
	
	public Peer (SocketAddress addr, int version, byte[] onion, boolean connected){
		this.addr = addr;
		this.version = version;
		this.onion = onion;
		this.connected = connected;
	}
	
	public void printPeer(){
		System.out.println("SocketAddress:" + addr);
		System.out.println("Version: " + version);
		System.out.println("Onion: " + Utils.bytesToHex(onion));
		System.out.println("Timestamp: " + timestamp);
		System.out.println("Connected: " + connected);
	}
	
}
