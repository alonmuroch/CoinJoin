package Core;
import java.io.IOException;

import Core.DNSSeeds.Seed;
import Core.NetworkAddress.NetworkType;
import Core.PeerEventListener.PeerEvent;
import Core.Reject.ccode;

public class Peer {
	
	NioClient client;
	NetworkAddress networkAddress;
	Seed seed = null;
	PeerEventListener listener = new PeerEventListener();
	Thread pingpong;
	
	public Peer (NetworkAddress addr){
		this.networkAddress = addr;
	}
	
	public Peer (Seed dnsSeed){
		this.seed = dnsSeed;
	}
	
	public boolean connect(){
		try{
			if (this.seed == null){
				client = new NioClient(networkAddress.getAddressAsString(), networkAddress.getPort());
				client.run(listener);
			}
			else {
				client = new NioClient(seed.domain, seed.port);
				client.run(listener);
			}
			try {send(Command.VERSION);} catch (IOException e) {return false;}
			try{listener.doWait();} catch (PeerTimeoutException e) {return false;}
			try{listener.doWait();} catch (PeerTimeoutException e) {return false;}
			if (listener.event.connected){
				try {send(Command.VERACK);} catch (IOException e) {return false;}
				checkKnownPeers();
				startPingPong();
				return true;
			}
			else {return false;}
	
		}
		catch (Exception e){
			return false;
		}
	}
	
	public void startPingPong(){
		pingpong = new Thread(new Runnable() {
		    public void run() {
		    	while(true){
		    		try {Thread.sleep(10000);} catch (InterruptedException e) {pingpong.start();}
		    		try {send(Command.PING);} catch (IOException e) {disconnect();}
		    		try {listener.doWait();} catch (PeerTimeoutException e) {disconnect();}
		    	}
		    }
		});
		pingpong.start();
	}
	
	public void checkKnownPeers(){
		new Thread(new Runnable() {
		    public void run() {
		    	if (Main.coinjoin.peergroup.knownPeers.size()<1000){
		    		try {send(Command.GETADDR);} catch (IOException e) {disconnect();}
					try {listener.doWait();} catch (PeerTimeoutException e) {disconnect();}
		    	}
		    }
		}).start();
	}
	
	public void disconnect(){
		System.out.println("Disconnected from peer");
		try {client.close();}
		catch (Exception e){}
		Main.coinjoin.peergroup.removePeer(this);
	}
	
	
	public void send(Command cmd) throws IOException {
		
		Message message = null;
		switch(cmd){
		case VERSION:
			NetworkAddress n = new NetworkAddress(NetworkType.IPv4, new byte[]{00,00,00,00}, System.currentTimeMillis() / 1000L);
			Version ver = new Version(Main.coinjoin.nonce, n);
			message = new Message(Command.VERSION, ver.serialize());
			System.out.println("Sending VERSION message...");
			System.out.println("");
			break;
			
		case VERACK:
			System.out.println("");
			System.out.println("Sending VERACK message...");
			System.out.println("");
			message = new Message(Command.VERACK, new byte[0]);
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
