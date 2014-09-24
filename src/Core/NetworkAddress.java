package Core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class NetworkAddress {

	long timestamp = System.currentTimeMillis() / 1000L;
	byte[] paddedAddr;
	byte[] port = { 83, 35 };
	
	public NetworkAddress(InetAddress toaddr){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		try {outputStream.write(new byte[] { 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, (byte) 0xFF, (byte) 0xFF});} 
		catch (IOException e1) {e1.printStackTrace();}
		try {outputStream.write(toaddr.getAddress());} 
		catch (IOException e) {e.printStackTrace();}
		paddedAddr = outputStream.toByteArray();
	}
	
	public byte[] serialize() throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(ByteBuffer.allocate(8).putLong(timestamp).array());
		outputStream.write(paddedAddr); 
		outputStream.write(port);
		byte output[] = outputStream.toByteArray();
		return output;
	}
}
