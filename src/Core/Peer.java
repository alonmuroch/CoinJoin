package Core;
import java.io.IOException;

import Core.DNSSeeds.Seed;
import Core.NetworkAddress.NetworkType;
import Core.Reject.ccode;

public class Peer {
	
	NioClient client;
	NetworkAddress networkAddress;
	int version;
	Seed seed = null;
	int peerID;
	
	public Peer (NetworkAddress addr, int peerID){
		this.networkAddress = addr;
		this.peerID = peerID;
	}
	
	public Peer (Seed dnsSeed, int peerID){
		this.seed = dnsSeed;
		this.peerID = peerID;
	}
	
	public void connect(){
		try{
			if (this.seed == null){
				client = new NioClient(networkAddress.getAddressAsString(), networkAddress.getPort());
				client.run(peerID);
			}
			else {
				client = new NioClient(seed.domain, seed.port);
				client.run(peerID);
			}
			try {send(Command.VERSION);} catch (IOException e) {e.printStackTrace();}
	
		}
		catch (Exception e){
		}
	}
	
	public void completeConnection(boolean completed){
		if (!completed){
			Main.coinjoin.peergroup.removePeer(peerID);
			try {client.close();}
			catch (Exception e){}
		}
		else {
			try {send(Command.GETADDR);} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	public void disconnect(){
		try {client.close();}
		catch (Exception e){}
	}
	
	
	public void send(Command cmd) throws IOException{
		
		Message message = null;
		switch(cmd){
		case VERSION:
			NetworkAddress n = new NetworkAddress(NetworkType.IPv4, new byte[]{00,00,00,00}, System.currentTimeMillis() / 1000L);
			Version ver = new Version(Main.coinjoin.nonce, n);
			message = new Message(Command.VERSION, ver.serialize());
			System.out.println("Sending VERSION message...");
			System.out.println("");
			break;
		
		case PING:
			Ping ping = new Ping();
			message = new Message(Command.PING, ping.serialize());
			System.out.println("Sending PING message...");
			System.out.println("");
			break;
		
		case REJECT:
			Reject r = new Reject(Command.VERSION, ccode.REJECT_MALFORMED);
			message = new Message(Command.REJECT, r.serialize());
			System.out.println("Sending REJECT message...");
			System.out.println("");
			System.out.print(">>> ");
			break;
			
		case GETADDR:
			message = new Message(Command.GETADDR, new byte[0]);
			System.out.println("Sending GETADDR message...");
			System.out.println("");
			break;
		}
		client.send(message);
	}
	
}
