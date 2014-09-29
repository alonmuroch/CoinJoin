package Core;

import java.security.SecureRandom;

public class Ping {
	byte[] nonce = new byte[8];
	
	public Ping(){
		SecureRandom sr = new SecureRandom();
		nonce = new byte[8];
		sr.nextBytes(nonce);
	}
	
	public void parse(byte[] payload){
		this.nonce = payload;
	}
	
	public byte[] serialize(){
		return nonce;
	}
	
	public void printPing(){
		System.out.println(Utils.bytesToHex(nonce));
	}
}
