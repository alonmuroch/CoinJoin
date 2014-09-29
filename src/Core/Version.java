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
	NetworkAddress addr;
	byte[] nonce;
	
	public Version(){
		
	}
	
	public Version(byte[] nonce, NetworkAddress addr) {
		this.nonce = nonce;
		this.addr = addr;
	}
	
	public void parse(byte[] payload){
		byte[] ver = new byte[4];
		for (int i=0; i<4; i++){ver[i]=payload[i];}
		ByteBuffer wrapped = ByteBuffer.wrap(ver);
		version = wrapped.getInt();
		int a = 0;
		for (int i=4; i<12; i++){
			services[a]=payload[i];
			a++;
		}
		byte[] na = new byte[27];
		a = 0;
		for (int i=12; i<39; i++){
			na[a]=payload[i];
			a++;
		}
		addr = new NetworkAddress();
		try {addr.parse(na);} 
		catch (IOException e) {e.printStackTrace();}
		a = 0;
		nonce = new byte[8];
		for (int i=39; i<47; i++){
			nonce[a]=payload[i];
			a++;
		}
	}
	
	public byte[] serialize() throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(ByteBuffer.allocate(4).putInt(version).array());
		outputStream.write(services);
		outputStream.write(addr.serialize());
		outputStream.write(nonce);
		byte output[] = outputStream.toByteArray();
		return output;
	}
	
	public void printVersion() throws IOException{
		System.out.println("Version: " + version);
		System.out.println("Services: " + Utils.bytesToHex(services));
		addr.printNetworkAddress();
		System.out.println("Nonce: " + Utils.bytesToHex(nonce));
	}
	
}
