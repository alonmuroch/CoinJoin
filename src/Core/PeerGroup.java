package Core;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PeerGroup {

	public static Map<Integer, Peer> peers = new HashMap<Integer,Peer>();
	public static ArrayList<NetworkAddress> knownPeers = new ArrayList<NetworkAddress>();
	
	public PeerGroup(){
		addPeers();
	}
	
	public void addPeers(){
		DNSSeeds seeds = new DNSSeeds();
		for (int i=0; i<peers.size()+1; i++){
			if (!peers.containsKey(i)){
				Peer peer = new Peer(seeds.seeds.get(0), i);
				peer.connect();
				peers.put(i, peer);
				break;
			}
		}
	}
	
	public void removePeer(int peerID){
		peers.remove(peerID);
	}
}
