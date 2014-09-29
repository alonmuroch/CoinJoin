package Core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import Core.NetworkAddress.NetworkType;
import Core.Reject.ccode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

public class ServerHandler extends ChannelInboundMessageHandlerAdapter {
	
	private static final ChannelGroup channels = new DefaultChannelGroup();
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
		Channel incoming = ctx.channel();
		channels.add(ctx.channel());
		System.out.println("Received incoming connection from " + incoming.remoteAddress());
		System.out.println("");
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
		Channel incoming = ctx.channel();
		channels.remove(ctx.channel());
		System.out.println(incoming.remoteAddress() + " has disconnected");
		System.out.println("");
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object message) throws Exception {
		
		boolean twice = false;
		Channel incoming = ctx.channel();
		System.out.println("Received message from " + incoming.remoteAddress() + ":");
		try {
			Parser p = new Parser((byte[]) message);
			p.printMessage();
			switch(p.command){
				case VERSION:
					Version ver = new Version();
					ver.parse(p.payload);
					ver.printVersion();
					if (ver.version<10000){
						//Do something if it isn't the version we're looking for
					} 
					Peer peer = null;
					//If using IP network
					if (ver.addr.network!=NetworkType.Tor){
						String ip = incoming.remoteAddress().toString().substring(1, incoming.remoteAddress().toString().indexOf(":"));
						ver.addr.setAddress(ver.addr.network, ip);
					}
					CoinjoinAppKit.peergroup.knownPeers.add(ver.addr);
					System.out.println("");
					System.out.println("Sending VERACK message...");
					Message verack = new Message(Command.VERACK, new byte[0]);
					incoming.write(verack.serialize());
					NetworkAddress n = new NetworkAddress(NetworkType.IPv4, new byte[]{00,00,00,00}, System.currentTimeMillis() / 1000L);
					ver = new Version(Main.coinjoin.nonce, n);
					Message version = new Message(Command.VERSION, ver.serialize());
					System.out.println("Sending VERSION message...");
					System.out.println("");
					incoming.write(version.serialize());
					break;
	
				case VERACK:
					System.out.println("");
					break;
				
				case PING:
					Ping ping = new Ping();
					ping.parse(p.payload);
					System.out.println("");
					System.out.println("Sending PONG message...");
					System.out.println("");
					Pong pong = new Pong(ping.nonce);
					Message msgpong = new Message(Command.PONG, pong.serialize());
					incoming.write(msgpong.serialize());
					break;
				
				case PONG:
					Pong pongresp = new Pong(p.payload);
					byte[] nonce = pongresp.nonce;
					//This is where we will check to see if the received nonce is the same as the one we sent.
					//If so, then we connected to ourselves and must disconnect.
					break;
				
				case GETADDR:
					System.out.println("");
					System.out.println("Sending ADDR message...");
					Addr a = new Addr(Main.coinjoin.peergroup.knownPeers);
					Message addrmsg = new Message(Command.ADDR, a.serialize());
					incoming.write(addrmsg.serialize());
					break;
					
				case REJECT:
					Reject r = new Reject(p.payload);
					r.printReject();
					System.out.println("");
					break;
			}
		} catch (MalformedMessageException e){
			System.out.println("Message malformed. Sending REJECT message...");
			Reject r = new Reject(Parser.getCommand((byte[]) message), ccode.REJECT_MALFORMED);
			Message rejectmessage = new Message(Command.REJECT, r.serialize());
			incoming.write(rejectmessage.serialize());
		} catch (InvalidChecksumException e1){
			System.out.println("Invalid Checksum. Sending REJECT message...");
			Reject r = new Reject(Parser.getCommand((byte[]) message), ccode.REJECT_MALFORMED);
			Message rejectmessage = new Message(Command.REJECT, r.serialize());
			incoming.write(rejectmessage.serialize());
		}
		
	}

}
