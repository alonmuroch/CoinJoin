package Core;

public class Pong {
	byte[] nonce = new byte[8];

	public Pong (byte[] pingnonce){
		this.nonce = pingnonce;
	}
	
	public byte[] serialize(){
		return nonce;
	}
	
	public void printPong(){
		System.out.println(Utils.bytesToHex(nonce));
	}
}
