package com.yude.game.ddz.controller.client;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.yude.protocol.common.message.GameRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: H2
 * @Date: 2020/7/17 19:44
 * @Declare:
 */
public class ClientEncoder extends MessageToByteEncoder<GameRequestMessage> {

    /**
     * 序列化的工具类
     */
    public static final Codec<GameRequestMessage> requestCodec = ProtobufProxy.create(GameRequestMessage.class);

    public static final Logger log = LoggerFactory.getLogger(ClientEncoder.class);


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, GameRequestMessage gameRequestMessage, ByteBuf byteBuf) throws Exception {
        Codec<GameRequestMessage> codec = requestCodec;
        byte[] encode = codec.encode(gameRequestMessage);
        byteBuf.writeBytes(encode);
    }
}
