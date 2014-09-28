package Core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import Core.Reject.ccode;

public class NetworkAddress {

	long timestamp;
	NetworkType network;
	byte[] addr;
	byte[] port = new byte[] { (byte) 0x1F, (byte) 0x40};
	
	public NetworkAddress(NetworkType type, byte[] addr, long timestamp) throws IOException{
		this.network = type;
		this.timestamp = timestamp;
		if (type==NetworkType.IPv6){this.addr = addr;}
		else if (type==NetworkType.IPv4){
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			outputStream.write(addr);
			outputStream.write(new byte[] {00,00,00,00,00,00,00,00,00,00,00,00});
			this.addr = outputStream.toByteArray();
		}
		else if (type==NetworkType.Tor){
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			outputStream.write(addr);
			outputStream.write(new byte[] {00,00,00,00,00,00});
			this.addr = outputStream.toByteArray();
		}
	}
	
	public NetworkAddress(byte[] payload){
		byte[] ts = new byte[8];
		for (int i=0; i<8; i++){
			ts[i]=payload[i];
		}
		ByteBuffer wrapped = ByteBuffer.wrap(ts);
		timestamp = wrapped.getLong();
		byte[] type = new byte[1];
		type[0]=payload[8];
		if (Arrays.equals(type, NetworkType.IPv4.getValue())){this.network = NetworkType.IPv4;}
		else if (Arrays.equals(type, NetworkType.IPv6.getValue())){this.network = NetworkType.IPv6;}
		else if (Arrays.equals(type, NetworkType.Tor.getValue())){this.network = NetworkType.Tor;}
		if (network==NetworkType.IPv4){
			addr = new byte[4];
			int a=0;
			for (int i=9; i<13; i++){
				addr[a]=payload[i];
				a++;
			}
		}
		if (network==NetworkType.IPv6){
			addr = new byte[16];
			int a=0;
			for (int i=9; i<25; i++){
				addr[a]=payload[i];
				a++;
			}
		}
		if (network==NetworkType.Tor){
			addr = new byte[10];
			int a=0;
			for (int i=9; i<19; i++){
				addr[a]=payload[i];
				a++;
			}
		}
		int a=0;
		for (int i=25; i<27; i++){
			port[a]=payload[i];
			a++;
		}
		
	}
	
	public enum NetworkType {
		IPv4 (new byte[] {0x11}),
		IPv6 (new byte[] {0x22}),
		Tor (new byte[] {0x33});
		
		private byte[] value;
		
		NetworkType(){
			
		}
		
	    NetworkType(byte[] val) {
	        this.value = val;
	    }

	    public byte[] getValue() {
	        return value;
	    }
	}
	
	public byte[] serialize() throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(ByteBuffer.allocate(8).putLong(timestamp).array());
		outputStream.write(network.getValue());
		outputStream.write(addr); 
		outputStream.write(port);
		byte output[] = outputStream.toByteArray();
		return output;
	}
	
	public void printNetworkAddress(){
		System.out.println("Timestamp: " + timestamp);
		System.out.println("Network Type: " + network.toString());
		System.out.println("Address: " + Utils.bytesToHex(addr));
		System.out.println("Port: " + Utils.bytesToHex(port));
	}
}
