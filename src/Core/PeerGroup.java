package Core;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PeerGroup {

	public static ArrayList<Peer> peers = new ArrayList<Peer>();
	public static ArrayList<NetworkAddress> knownPeers = new ArrayList<NetworkAddress>();
	
	public PeerGroup(){
		addPeers();
	}
	
	public void addPeers(){
		DNSSeeds seeds = new DNSSeeds();
		Peer p = new Peer(seeds.getSeeds().get(0));
		if (p.connect()){peers.add(p);}
	}
	
	public void removePeer(Peer peer){
		peers.remove(peer);
	}
}
