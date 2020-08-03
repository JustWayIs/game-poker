package com.yude.game.ddz.controller.client;


import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.yude.protocol.common.message.GameResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author: H2
 * @Date: 2020/7/17 19:50
 * @Declare:
 */
public class ClientDecoder extends ByteToMessageDecoder {


    public static final Codec<GameResponseMessage> responseCodec = ProtobufProxy.create(GameResponseMessage .class);

    private static final Logger log = LoggerFactory.getLogger(ClientDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        Codec<GameResponseMessage> codec = responseCodec;
        byte[] responseByte = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(responseByte);
        GameResponseMessage gameResponseMessage = codec.decode(responseByte);
        list.add(gameResponseMessage);
    }
}
