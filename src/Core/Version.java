package Core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base32;

public class Version {

	int version = 10000;
	byte[] services = {00,00,00,00,00,00,00,00};
	long timestamp = System.currentTimeMillis() / 1000L;
	byte[] onion = new byte[10];
	byte[] nonce = new byte[8];
	
	public Version() {
		SecureRandom sr = new SecureRandom();
		nonce = new byte[8];
		sr.nextBytes(nonce);
		byte[] zeros = {00,00,00,00,00,00,00,00,00,00};
		this.onion = zeros;
	}
	
	public Version(String onion) {
		//Onion is base32 representation of the Tor .onion address; 
		//Encode using:  String base32String = Base32.encode(rawDataBytes);
		Base32 base32 = new Base32();
		this.onion = base32.decode(onion);
		SecureRandom sr = new SecureRandom();
		nonce = new byte[8];
		sr.nextBytes(nonce);
	}
	
	public Version(byte[] payload){
		byte[] ver = new byte[4];
		for (int i=0; i<4; i++){ver[i]=payload[i];}
		ByteBuffer wrapped = ByteBuffer.wrap(ver);
		version = wrapped.getInt();
		int a = 0;
		for (int i=4; i<12; i++){
			services[a]=payload[i];
			a++;
		}
		byte[] ts = new byte[8];
		a = 0;
		for (int i=12; i<20; i++){
			ts[a]=payload[i];
			a++;
		}
		wrapped = ByteBuffer.wrap(ts);
		timestamp = wrapped.getLong();
		a = 0;
		for (int i=20; i<30; i++){
			onion[a]=payload[i];
			a++;
		}
		a = 0;
		for (int i=30; i<38; i++){
			nonce[a]=payload[i];
			a++;
		}
	}
	
	public byte[] serialize() throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(ByteBuffer.allocate(4).putInt(version).array());
		outputStream.write(services);
		outputStream.write(ByteBuffer.allocate(8).putLong(timestamp).array());
		outputStream.write(onion);
		outputStream.write(nonce);
		byte output[] = outputStream.toByteArray();
		return output;
	}
	
	public void printVersion(){
		System.out.println("Version: " + version);
		System.out.println("Services: " + Utils.bytesToHex(services));
		System.out.println("Timestamp: " + timestamp);
		System.out.println("Onion: " + Utils.bytesToHex(onion));
		System.out.println("Nonce: " + Utils.bytesToHex(nonce));
	}
	
}
