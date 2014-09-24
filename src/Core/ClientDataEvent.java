package Core;

import java.nio.channels.SocketChannel;

class ClientDataEvent {
	public NioClient client;
	public SocketChannel socket;
	public byte[] data;
	
	public ClientDataEvent(NioClient client, SocketChannel socket, byte[] data) {
		this.client = client;
		this.socket = socket;
		this.data = data;
	}
}