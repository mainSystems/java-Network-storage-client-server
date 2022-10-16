package systems.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Server {

    private static final int MAX_OBJECT_SIZE = 1024 * 1024 * 100;
    private final int port;
    private static final Logger logger = LogManager.getLogger(Server.class);

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new Server(8189).run();
    }

    private void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); //just acceptation new client
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // main worker group

        try {
            ServerBootstrap server = new ServerBootstrap(); //as socket
            logger.info("Server started at port: {} Waiting connection...", port);
            server.group(bossGroup, workerGroup);
            server.channel(NioServerSocketChannel.class); //choose technology to work on
            server.option(ChannelOption.SO_BACKLOG, 128); //this is only server option not a child. For boss group, more than 128 ill be rejected
            server.childOption(ChannelOption.SO_KEEPALIVE, true); // for child

            server.childHandler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(
                            new ObjectEncoder(),
                            new ObjectDecoder(MAX_OBJECT_SIZE, ClassResolvers.cacheDisabled(null)), //decode object and now work with them
                            new ServerHandler()
                    );
                }
            });

            ChannelFuture future = server.bind(port).sync(); // sync for gain resul to future,  need this for gain channel
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("Failed to bind port on server");
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
