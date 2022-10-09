package main.systems.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import systems.common.Message;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;


public class Client {

    private final String host;
    private final int port;
    private static final Logger logger = LogManager.getLogger(Client.class);

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

//    public static void main(String[] args) {
//        File file = new File("client/dir/my-file.txt");
////        File file = new File("C:\\java\\NetworkStorage\\client\\dir\\my-file.txt");
//        try {
//            Message message = new Message("put", file, Files.readAllBytes(file.toPath()));
//            logger.info("Starting client");
//            new Client("localhost", 8189).send(message, (response) -> {
//                System.out.println("response = " + response);
//            });
//        } catch (IOException e) {
//            logger.error("Can`t read file");
//            e.printStackTrace();
//        }
//    }

    public void send(Message message, Consumer<Message> callback) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap client = new Bootstrap();
            client.group(workerGroup);
            client.channel(NioSocketChannel.class);
            client.option(ChannelOption.SO_KEEPALIVE, true);
            client.handler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(
                        new ObjectEncoder(),
                        new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()),
                        new StringDecoder(StandardCharsets.UTF_8),
                        new ClientHandler(message,callback)
                    );
                }
            });
            ChannelFuture future = client.connect(host, port).sync();
            future.channel().closeFuture().sync(); // if we stop sending
        } catch (InterruptedException e) {
            logger.error("Failed to bind port on server");
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
