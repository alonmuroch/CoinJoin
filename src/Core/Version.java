package Core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class Version {

	int version = 10000;
	byte[] services = {00,00,00,00,00,00,00,00};
	long timestamp = System.currentTimeMillis() / 1000L;
	byte[] nonce = new byte[8];
	
	public Version() {
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
		for (int i=20; i<28; i++){
			nonce[a]=payload[i];
			a++;
		}
	}
	
	public byte[] serialize() throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(ByteBuffer.allocate(4).putInt(version).array());
		outputStream.write(services);
		outputStream.write(ByteBuffer.allocate(8).putLong(timestamp).array());
		outputStream.write(nonce);
		byte output[] = outputStream.toByteArray();
		return output;
	}
	
	public void printVersion(){
		System.out.println("Version: " + version);
		System.out.println("Services: " + Utils.bytesToHex(services));
		System.out.println("Timestamp: " + timestamp);
		System.out.println("Nonce: " + Utils.bytesToHex(nonce));
	}
	
}
