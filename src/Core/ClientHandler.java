package Core;

import java.util.Arrays;

import Core.NetworkAddress.NetworkType;
import Core.Reject.ccode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

public class ClientHandler extends ChannelInboundMessageHandlerAdapter {
	PeerEventListener listener;

	public ClientHandler(PeerEventListener listener) {
		this.listener = listener;
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
					if (Arrays.equals(ver.nonce, Main.coinjoin.nonce) || ver.version!=10000){
						listener.event.connected = false;
						listener.doNotify();
					}
					else {
						listener.event.connected = true;
						listener.doNotify();
					}
					break;
	
				case VERACK:
					listener.doNotify();
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
					listener.doNotify();
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
					listener.doNotify();
					boolean exists = false;
					for (NetworkAddress addr : a.addressList){
						for (NetworkAddress knownaddr : Main.coinjoin.peergroup.knownPeers){
							if (Arrays.equals(addr.addr, knownaddr.addr) && Arrays.equals(addr.port, knownaddr.port)){
									exists = true;
							}
						}
						if (!exists){Main.coinjoin.peergroup.knownPeers.add(addr);}
						exists = false;
					}
					System.out.println("");
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
		System.out.println("CoinJoin: connected to " + ctx.channel().localAddress());
	}
	
}
