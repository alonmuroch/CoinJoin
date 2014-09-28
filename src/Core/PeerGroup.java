package Core;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class PeerGroup {

	ArrayList<Peer> peergroup = new ArrayList<Peer>();
	
	public PeerGroup(){
		
	}
	
	public void addConnected(Peer peer){
		boolean exists = false;
		for (Peer p : peergroup){
			if (Arrays.equals(p.networkAddress.addr, peer.networkAddress.addr)){
				exists = true;
				p.peerID = peer.peerID;
			}
		}
		if (!exists){peergroup.add(peer);}
	}
	
	public void addDisconnected(Peer peer){
		boolean exists = false;
		for (Peer p : peergroup){
			if (p.networkAddress.addr == peer.networkAddress.addr){
				exists = true;
				p.peerID = new byte[]{00};
			}
		}
		if (!exists){peergroup.add(peer);}
	}
	
	public void disconnectPeer(byte[] peerID){
		for (Peer p : peergroup){
			if (Arrays.equals(p.peerID, peerID)){p.peerID = new byte[]{00};}
		}
	}
	
	public ArrayList<Peer> getConnectedPeers(){
		ArrayList<Peer> connected = new ArrayList<Peer>();
		for (Peer p : peergroup){
			if (Arrays.equals(p.peerID, new byte[]{00})){
				connected.add(p);
			}
		}
		return connected;
	}
	
}
