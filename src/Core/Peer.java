package Core;

import java.net.SocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Peer {
	
	byte[] peerID;
	NetworkAddress networkAddress;
	int version;
	
	public Peer (NetworkAddress addr, int version){
		this.version = version;
		this.networkAddress = addr;
		MessageDigest md = null;
	    try {md = MessageDigest.getInstance("SHA-1");}
	    catch(NoSuchAlgorithmException e) {e.printStackTrace();} 
	    peerID = md.digest(addr.addr);
	}
	
	public static byte[] getPeerID(SocketAddress addr){
		String ip = addr.toString().substring(1, addr.toString().indexOf(":"));
		MessageDigest md = null;
	    try {md = MessageDigest.getInstance("SHA-1");}
	    catch(NoSuchAlgorithmException e) {e.printStackTrace();} 
		return md.digest(Utils.ipStringToBytes(ip));
	}
	
	public void printPeer(){
		System.out.println("Peer ID: " + Utils.bytesToHex(peerID));
		System.out.println("NetworkAddress:");
		networkAddress.printNetworkAddress();
		System.out.println("Version: " + version);
	}
	
}
