package Core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

	PeerEventListener listener;
	
	public ClientInitializer(PeerEventListener listener) {
		this.listener = listener;
	}

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		ByteBuf delimiter = Unpooled.copiedBuffer("e6982d514bce827a00649bb6b2607624f6f0aa12".getBytes());
		ChannelPipeline pipeline = arg0.pipeline();
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, delimiter));
		pipeline.addLast("decoder", new ByteArrayDecoder());
		pipeline.addLast("encoder", new ByteArrayEncoder());
		pipeline.addLast("handler", new ClientHandler(listener));
	}

}
