package Core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

public class ClientHandler extends ChannelInboundMessageHandlerAdapter {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
		Channel incoming = ctx.channel();
		//incoming.write("[ECHO] " + message + "\n");
		System.out.println("Received message from " + incoming.remoteAddress() + ":");
		Parser p = new Parser((byte[]) msg);
		p.printMessage();
		switch(p.command){
			case VERSION:
				Version ver = new Version (p.payload);
				ver.printVersion();
				System.out.println("");
				System.out.println("Sending VERACK message...");
				System.out.println("");
				Message verack = new Message(Command.VERACK, new byte[0]);
				if (ver.version<10000){
					//Do something if it isn't the version we're looking for
				} 
				incoming.write(verack.serialize());
				System.out.println("Enter a command:");
				System.out.print(">>> ");
				break;
	
			case VERACK:
				System.out.println("");
				break;
			
		}
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
		System.out.println("Connected to server");
	}
	
}
