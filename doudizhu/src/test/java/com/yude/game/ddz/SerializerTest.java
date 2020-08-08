package com.yude.game.ddz;


import com.alibaba.fastjson.JSONObject;
import com.baidu.bjf.remoting.protobuf.Any;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.yude.game.doudizhu.application.request.OperationCardRequest;
import com.yude.protocol.common.message.GameRequestMessage;
import com.yude.protocol.common.message.GameResponseMessage;
import com.yude.protocol.common.message.GameResponseMessageHead;
import com.yude.protocol.common.response.HeartBeatResponse;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Author: HH
 * @Date: 2020/6/10 19:49
 * @Version 1.0
 * @Declare
 */
public class SerializerTest {

    public static void main(String[] args) throws IOException {
        Codec<GameRequestMessage> dcodec = ProtobufProxy.create(GameRequestMessage.class);
        Codec<GameResponseMessage> codec = ProtobufProxy.create(GameResponseMessage.class);


        OperationCardRequest body = handler();
        GameResponseMessage message =  new GameResponseMessage();
        GameResponseMessageHead head = new GameResponseMessageHead();
        //gameRequestMessageHead.setIp("6666.66.66.66");
        head.setCmd(000001);
        //gameRequestMessageHead.setUserId(121100);
        message.setHead(head);
        HeartBeatResponse response = new HeartBeatResponse();


        long beforeJson = System.currentTimeMillis();
        JSONObject.toJSONString(message);
        long timeJson = System.currentTimeMillis() - beforeJson;
        System.out.println("json编码"+timeJson);

        /*long before = System.currentTimeMillis();
        Any any = Any.pack(response);
        message.setAny(any);
        byte[] encode = codec.encode(message);
        long time = System.currentTimeMillis() - before;
         System.out.println("Jprotobuf 编码消耗时间："+time +"毫秒");*/


        byte[] heartBeatEncode = null;
        long before = System.currentTimeMillis();
        for(int i = 0 ; i < 1000000 ; ++i){
           Any heartBeatAny = Any.pack(response);
           message.setAny(heartBeatAny);
            heartBeatEncode = codec.encode(message);
        }
        long time = System.currentTimeMillis() - before;
        System.out.println("Jprotobuf 心跳 每百万次 编码消耗时间："+time+"毫秒");

        before = System.currentTimeMillis();
        GameResponseMessage messageD = codec.decode(heartBeatEncode);
        messageD.getAny().unpack(HeartBeatResponse.class);
        time = System.currentTimeMillis() - before;
        System.out.println("Jprotobuf 心跳 解码消耗时间:"+time+"毫秒");

         before = System.currentTimeMillis();
        GameResponseMessage heartBeatMessage = null;
        for (int i = 0 ; i < 1000000 ; ++i) {
            heartBeatMessage = codec.decode(heartBeatEncode);
            HeartBeatResponse unpack = heartBeatMessage.getAny().unpack(HeartBeatResponse.class);
        }
         time = System.currentTimeMillis() - before;
        System.out.println("Jprotobuf 心跳 每百万次 解码消耗时间:"+time+"毫秒");

        System.out.println("-------解码后：");
        //System.out.println(messageD);

        System.out.println("------Any：");
       // System.out.println(messageD.getAny());

        //解析报文体
        //OperationCardRequest operationCardRequest = messageD.getAny().unpack(OperationCardRequest.class);

        System.out.println("-----业务数据：");
        //System.out.println(operationCardRequest);
    }

    public static OperationCardRequest handler(){
        OperationCardRequest operationCardRequest = new OperationCardRequest();
        operationCardRequest.setCards(Arrays.asList(11));
        operationCardRequest.setPosId(0);

        return operationCardRequest;
    }
}
