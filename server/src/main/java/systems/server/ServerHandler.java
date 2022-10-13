package systems.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import systems.common.CommandType;
import systems.common.SqlCommands;
import systems.common.StorageCommands;
import systems.common.Message;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final String SERVER_USER_DIR = "server/storage/";
    private static final Logger logger = LogManager.getLogger(ServerHandler.class);
    private String username = null;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        if (msg.getCommandType().equals(CommandType.SQL)) {
            msgAuth(ctx, msg);
        }

        if (msg.getCommandType().equals(CommandType.STORAGE)) {
            msgStorage(ctx, msg);
        }

    }

    private void msgAuth(ChannelHandlerContext ctx, Message msg) throws ClassNotFoundException, SQLException {
        if (msg.getSqlCommands().equals(SqlCommands.SELECT_USERNAME_AUTH)) {
            SqliteHandler.connect();
            username = SqliteHandler.sqlTask(SqlCommands.SELECT_USERNAME_AUTH, msg.getUsername(), msg.getPassword());
            SqliteHandler.disconnect();
        }
        Message msgResult = new Message(SqlCommands.AUTH_STATE, username);

        ChannelFuture future = ctx.writeAndFlush(msgResult);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    private void msgStorage(ChannelHandlerContext ctx, Message msg) throws IOException {
        if (msg.getStorageCommand().equals(StorageCommands.PUT)) {
            Path root = Path.of(SERVER_USER_DIR + msg.getUsername());
            Files.createDirectories(root);
            Path file = root.resolve(msg.getFile().getName());
            try {
                Files.createFile(file);
            } catch (FileAlreadyExistsException ignored) {
                logger.error("File already exist");
            }
            Files.write(file, msg.getData());
            logger.info("Receiving file: " + file);
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
