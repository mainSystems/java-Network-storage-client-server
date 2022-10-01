package systems.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import systems.common.Message;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final String SERVER_USER_DIR = "server/user-dir";
    private static final Logger logger = LogManager.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg.getCommand().equals("put")) {
            Path root = Path.of(SERVER_USER_DIR);
            Files.createDirectories(root);
//            Path file = root.resolve(msg.getFile().getPath());
            Path file = root.resolve(msg.getFile().getName());
//            Files.createDirectories(file.getParent());
            try {
                Files.createFile(file);
            } catch (FileAlreadyExistsException ignored) {
                    logger.error("File already exist");
            }
            Files.write(file,msg.getData());
        }
        ChannelFuture future = ctx.writeAndFlush(String.format("File %s sended\n", msg.getFile().getName()));
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("We got exception");
        cause.printStackTrace();
    }
}
