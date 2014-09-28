package Core;

import java.util.Arrays;

import Core.NetworkAddress.NetworkType;
import Core.Reject.ccode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

public class ClientHandler extends ChannelInboundMessageHandlerAdapter {

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
						Main.coinjoin.clients.get(0).close();
					}
					else {
						System.out.println("");
						System.out.println("Added new peer:");
						Peer peer = null;
						//If using IP network
						if (Arrays.equals(ver.onion, Utils.IP)){
							String socketAddress = incoming.remoteAddress().toString();
							String ip = null;
							if (socketAddress.contains("/")){
								socketAddress = incoming.remoteAddress().toString().substring(incoming.remoteAddress().toString().indexOf("/"), incoming.remoteAddress().toString().length());
							}
							ip = socketAddress.substring(1, socketAddress.indexOf(":"));
							peer = new Peer(new NetworkAddress(NetworkType.IPv4, Utils.ipStringToBytes(ip), System.currentTimeMillis()/1000L), ver.version);
						}
						//If using Tor
						else {
							peer = new Peer(new NetworkAddress(NetworkType.Tor, ver.onion, System.currentTimeMillis()/1000L), ver.version);
						}
						Main.coinjoin.peergroup.addConnected(peer);
						peer.printPeer();
						System.out.println("");
						System.out.println("Sending VERACK message...");
						System.out.println("");
						Message verack = new Message(Command.VERACK, new byte[0]);
						incoming.write(verack.serialize());
						System.out.println("Enter a command:");
						System.out.print(">>> ");
					}
					break;
	
				case VERACK:
					System.out.println("");
					break;
				
				case PING:
					Ping ping = new Ping(p.payload);
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
					Addr a = new Addr(p.payload);
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
		System.out.println("Connected to server");
	}
	
}
