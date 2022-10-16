package main.systems.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import systems.common.CommandType;
import systems.common.Message;

import java.util.function.Consumer;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private final Message message;
    private final Consumer<Message> callback;
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    public ClientHandler(Message message, Consumer<Message> callback) {
        this.message = message;
        this.callback = callback;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(message); //when connected to server
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        callback.accept(msg);
        if (msg.getCommandType().equals(CommandType.SQL) && !msg.getUsername().isEmpty()) {
            ClientApplication.getINSTANCE().switchToMainWindow(msg.getUsername());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("We got exception"); // what we do if take exception, without this method we can get oom
        cause.printStackTrace();
    }
}
