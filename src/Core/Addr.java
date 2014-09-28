package Core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Addr {

	private int size;
	private byte[] addresses;
	ArrayList<NetworkAddress> addressList = new ArrayList<NetworkAddress>();
	
	public Addr (ArrayList<NetworkAddress> addresslist){
		this.size = addresslist.size();
		this.addressList = addresslist;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		for (NetworkAddress addr : addresslist){
			try {outputStream.write(addr.serialize());} catch (IOException e) {e.printStackTrace();}
		}
		addresses = outputStream.toByteArray();
	}
	
	public Addr (byte[] payload){
		byte[] sz = new byte[4];
		for (int i=0; i<4; i++){sz[i]=payload[i];}
		ByteBuffer wrapped = ByteBuffer.wrap(sz);
		this.size = wrapped.getInt();
		int a=0;
		addresses = new byte[this.size*27];
		for (int i=4; i<(size*27)+4; i++){
			addresses[a] = payload[i];
			a++;
		}
		ArrayList<byte[]> temp = new ArrayList<byte[]>();
		temp = Utils.divideArray(addresses, 27);
		for (byte[] arr : temp){addressList.add(new NetworkAddress(arr));}
	}
	
	public byte[] serialize() throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(ByteBuffer.allocate(4).putInt(size).array());
		outputStream.write(addresses);
		byte output[] = outputStream.toByteArray();
		return output;
	}
	
	public void printAddr(){
		System.out.println("Size: " + size);
		System.out.println("Addresses: ");
		for (NetworkAddress addr : addressList){
			addr.printNetworkAddress();
		}
	}
	
}
