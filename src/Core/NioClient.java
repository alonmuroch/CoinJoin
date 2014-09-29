package Core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import Core.Reject.ccode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NioClient {
	
	private final String host;
	private final int port;
	Channel channel;
	EventLoopGroup group;
	boolean close = false;
	
	public NioClient(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public void run(int peerID) throws InterruptedException, IOException {
		group = new NioEventLoopGroup();
		
		Bootstrap bootstrap = new Bootstrap()
			.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ClientInitializer(peerID));
		channel = bootstrap.connect(host, port).sync().channel();
	}
	
	public void send(Message message) throws IOException{
		channel.write(message.serialize());
	}
	
	public void close(){
		group.shutdownGracefully();
		close = true;
	}
}
