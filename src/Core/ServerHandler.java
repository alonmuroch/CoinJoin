package Core;

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
		for (Channel channel : channels){
			channel.write("[SERVER] - " + incoming.remoteAddress() + " has left\n");
		}
		channels.remove(ctx.channel());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object message) throws Exception {
		
		Channel incoming = ctx.channel();
		//incoming.write("[ECHO] " + message + "\n");
		System.out.println("Received message from " + incoming.remoteAddress() + ":");
		Parser p = new Parser((byte[]) message);
		p.printMessage();
		switch(p.command){
			case VERSION:
				Version ver = new Version (p.payload);
				ver.printVersion();
				System.out.println("");
				System.out.println("Sending VERACK message...");
				Message verack = new Message(Command.VERACK, new byte[0]);
				incoming.write(verack.serialize());
				if (ver.version<10000){
					//Do something if it isn't the version we're looking for
				} 
				ver = new Version();
				Message version = new Message(Command.VERSION, ver.serialize());
				System.out.println("Sending VERSION message...");
				System.out.println("");
				incoming.write(version.serialize());
				break;
	
			case VERACK:
				System.out.println("");
				break;
			
		}
	}

}
