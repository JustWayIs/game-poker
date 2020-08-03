package com.yude.game.ddz.controller.client;

;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@ChannelHandler.Sharable
@Component
public class ClientChannelHandlerInitializer extends ChannelInitializer<SocketChannel> {
    /**
     * 这里用@Resource = @Qualifier + @Autowired : 因为没有单独为不同的handler类型抽象出不同的接口
     */
    @Resource(name="tcpCoreHandler")
    ChannelInboundHandler tcpCoreHandler;
    @Resource(name="heartBeathandler")
    ChannelInboundHandler heartBeathandler;
    @Resource(name="loginHandler")
    ChannelInboundHandler loginHandler;

    @Autowired
    MessageToByteEncoder messageToByteEncoder;
    /*@Autowired
    ByteToMessageDecoder byteToMessageDecoder;*/


    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        //H2: 还需要添加编解码器等等 hanler

        //TCP传输报文信息日志
        //pipeline.addLast("messageLog", new LoggingHandler(LogLevel.DEBUG));
        // proto - 解码器
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
        pipeline.addLast("customMessageDncoder", new ClientDecoder());

        // proto - 编码器
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
        pipeline.addLast("customMessageEcoder", new ClientEncoder());

        pipeline.addLast("core", new CommonTCPHandler() {
            @Override
            protected boolean onReceive(ChannelHandlerContext pContext, ByteBuf pData, ChannelPromise pPromise) {
                return false;
            }

            @Override
            protected void onClientActive(ChannelHandlerContext pContext) {

            }

            @Override
            protected void onClientInactive(ChannelHandlerContext pContext) {

            }

            @Override
            protected void onKeepAlive(ChannelHandlerContext pContext) {

            }
        });
        //超时检测
        // pipeline.addLast("readTimeOutHandler",new ReadTimeoutHandler(50));

    }
}
