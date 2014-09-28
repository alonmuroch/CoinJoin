package Core;

import java.net.SocketAddress;
import java.util.ArrayList;

public class PeerGroup {

	ArrayList<Peer> peergroup = new ArrayList<Peer>();
	
	public PeerGroup(){
		
	}
	
	public void addConnected(Peer peer){
		boolean exists = false;
		for (Peer p : peergroup){
			if (p.addr == peer.addr){
				exists = true;
				p.connected = true;
			}
		}
		if (!exists){peergroup.add(peer);}
	}
	
	public void addDisconnected(Peer peer){
		boolean exists = false;
		for (Peer p : peergroup){
			if (p.addr == peer.addr){
				exists = true;
				p.connected = false;
			}
		}
		if (!exists){peergroup.add(peer);}
	}
	
	public void disconnectPeer(SocketAddress addr){
		for (Peer p : peergroup){
			if (p.addr == addr){p.connected = false;}
		}
	}
	
	public ArrayList<Peer> getConnectedPeers(){
		ArrayList<Peer> connected = new ArrayList<Peer>();
		for (Peer p : peergroup){
			if (p.connected){
				connected.add(p);
			}
		}
		return connected;
	}
	
}
