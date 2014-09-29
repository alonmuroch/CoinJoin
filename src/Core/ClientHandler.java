package Core;

import java.util.Arrays;

import Core.NetworkAddress.NetworkType;
import Core.Reject.ccode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

public class ClientHandler extends ChannelInboundMessageHandlerAdapter {
	int peerID;

	public ClientHandler(int peerID) {
		this.peerID = peerID;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
		Channel incoming = ctx.channel();
		System.out.println("Received message from " + incoming.remoteAddress() + ":");
		try {
			Parser p = new Parser((byte[]) msg);
			p.printMessage();
			switch(p.command){
				case VERSION:
					Version ver = new Version();
					ver.parse(p.payload);
					ver.printVersion();
					if (ver.version<10000){
						//Do something if it isn't the version we're looking for
					} 
					if (Arrays.equals(ver.nonce, Main.coinjoin.nonce)){
						Main.coinjoin.peergroup.peers.get(peerID).completeConnection(false);
					}
					else {
						System.out.println("");
						System.out.println("Sending VERACK message...");
						System.out.println("");
						Message verack = new Message(Command.VERACK, new byte[0]);
						incoming.write(verack.serialize());
						Main.coinjoin.peergroup.peers.get(peerID).completeConnection(true);
						System.out.println("Enter a command:");
						System.out.print(">>> ");
					}
					break;
	
				case VERACK:
					System.out.println("");
					break;
				
				case PING:
					Ping ping = new Ping();
					ping.parse(p.payload);
					System.out.println("");
					System.out.println("Sending PONG message...");
					Pong pong = new Pong(ping.nonce);
					Message msgpong = new Message(Command.PONG, pong.serialize());
					incoming.write(msgpong.serialize());
					break;
				
				case PONG:
					Pong pongresp = new Pong(p.payload);
					byte[] nonce = pongresp.nonce;
					//This is where we will check to see if the received nonce is the same as the one we sent.
					//If so, then we connected to ourselves and must disconnect.
					System.out.println("");
					System.out.println("Enter a command:");
					System.out.print(">>> ");
					break;			
				
				case GETADDR:
					System.out.println("");
					break;
					
				case ADDR:
					Addr a = new Addr();
					a.parse(p.payload);
					a.printAddr();
					System.out.println("");
					System.out.println("Enter a command:");
					System.out.print(">>> ");
					break;
			
				case REJECT:
					Reject r = new Reject(p.payload);
					r.printReject();
					System.out.println("");
					System.out.println("Enter a command:");
					System.out.print(">>> ");
			}
		} catch (MalformedMessageException e){
			System.out.println("Message malformed. Sending REJECT message...");
			Reject r = new Reject(Parser.getCommand((byte[]) msg), ccode.REJECT_MALFORMED);
			Message rejectmessage = new Message(Command.REJECT, r.serialize());
			incoming.write(rejectmessage.serialize());
		} catch (InvalidChecksumException e1){
			System.out.println("Invalid Checksum. Sending REJECT message...");
			Reject r = new Reject(Parser.getCommand((byte[]) msg), ccode.REJECT_MALFORMED);
			Message rejectmessage = new Message(Command.REJECT, r.serialize());
			incoming.write(rejectmessage.serialize());
		}
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
		System.out.println("Peer " + peerID + " connected to server");
	}
	
}
