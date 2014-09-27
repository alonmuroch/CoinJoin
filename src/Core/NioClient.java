package Core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NioClient {
	
	private final String host;
	private final int port;
	Channel channel;
	
	public NioClient(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public void run() throws InterruptedException, IOException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap()
				.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ClientInitializer());
			channel = bootstrap.connect(host, port).sync().channel();
			System.out.println("Enter a command:");
			System.out.print(">>> ");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while(true){
				String command = in.readLine();
				Message message = null;
				if (command.toLowerCase().equals("version")){
					Version ver = new Version();
					message = new Message(Command.VERSION, ver.serialize());
					System.out.println("Sending VERSION message...");
					System.out.println("");
				}
				else {
					System.out.println("Enter a command:");
					System.out.print(">>> ");
				}
				channel.write(message.serialize());
			}
		} 
		finally {
			group.shutdownGracefully();
		}
	}
}
