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
		Main.peergroup.disconnectPeer(Peer.getPeerID(incoming.remoteAddress()));
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
					Version ver = new Version(p.payload);
					ver.printVersion();
					if (ver.version<10000){
						//Do something if it isn't the version we're looking for
					} 
					System.out.println("");
					System.out.println("Added new peer:");
					Peer peer = null;
					//If using IP network
					if (Arrays.equals(ver.onion, Utils.IP)){
						String ip = incoming.remoteAddress().toString().substring(1, incoming.remoteAddress().toString().indexOf(":"));
						peer = new Peer(new NetworkAddress(NetworkType.IPv4, Utils.ipStringToBytes(ip), System.currentTimeMillis()/1000L), ver.version);
					}
					//If using Tor
					else {
						peer = new Peer(new NetworkAddress(NetworkType.Tor, ver.onion, System.currentTimeMillis()/1000L), ver.version);
					}
					Main.peergroup.addConnected(peer);
					peer.printPeer();
					System.out.println("");
					System.out.println("Sending VERACK message...");
					Message verack = new Message(Command.VERACK, new byte[0]);
					incoming.write(verack.serialize());
					ver = new Version();
					Message version = new Message(Command.VERSION, ver.serialize());
					System.out.println("Sending VERSION message...");
					System.out.println("");
					incoming.write(version.serialize());
					break;
	
				case VERACK:
					System.out.println("");
					break;
				
				case PING:
					Ping ping = new Ping(p.payload);
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
					ArrayList<NetworkAddress> addressList = new ArrayList<NetworkAddress>();
					for (Peer peers : Main.peergroup.peergroup){
						addressList.add(peers.networkAddress);
					}
					Addr a = new Addr(addressList);
					Message addrmgs = new Message(Command.ADDR, a.serialize());
					incoming.write(addrmgs.serialize());
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
