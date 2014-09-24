package Core;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class ClientWorker implements Runnable {
	private List queue = new LinkedList();
	
	public void processData(NioClient client, SocketChannel socket, byte[] data, int count) {
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		synchronized(queue) {
			queue.add(new ClientDataEvent(client, socket, dataCopy));
			queue.notify();
		}
	}
	
	public void run() {
		ClientDataEvent dataEvent;
		
		while(true) {
			// Wait for data to become available
			synchronized(queue) {
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = (ClientDataEvent) queue.remove(0);
			}
			
			// Parse the message to find out it's type
			Parser p = new Parser(dataEvent.data);
			System.out.println("");
			System.out.println("Received " + p.command.toString() + " message:");
			p.printMessage();
			
			//handle different message types
			switch(p.command){
				case VERSION:
					Version ver = new Version (p.payload);
					ver.printVersion();
					System.out.println("");
					System.out.println("Sending VERACK message...");
					Message verack = new Message(Command.VERACK, new byte[0]);
					try {dataEvent.client.send(verack.serialize());} 
					catch (IOException e) {e.printStackTrace();}
					if (ver.version<10000){
						//Do something if it isn't the version we're looking for
					} 
					break;
			
				case VERACK:
					break;
				
			}
			
		}
	}
}