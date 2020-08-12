package com.yude.game.ddz.controller.client;


import com.baidu.bjf.remoting.protobuf.Any;
import com.baidu.bjf.remoting.protobuf.Codec;

import com.yude.game.doudizhu.application.request.*;
import com.yude.game.doudizhu.application.response.CurrentOperatorResponse;
import com.yude.game.doudizhu.application.response.GameStartResponse;
import com.yude.game.doudizhu.application.response.LandlordOwnershipResponse;
import com.yude.game.doudizhu.application.response.LoginResponse;
import com.yude.game.doudizhu.constant.command.CommandCode;
import com.yude.game.doudizhu.constant.command.PushCommandCode;
import com.yude.protocol.common.message.GameRequestMessage;
import com.yude.protocol.common.message.GameRequestMessageHead;
import com.yude.protocol.common.message.GameResponseMessage;
import com.yude.protocol.common.message.GameResponseMessageHead;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <pre>
 * Created by someone on 2018-10-19.
 *
 * </pre>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@ChannelHandler.Sharable
public abstract class CommonTCPHandler extends SimpleChannelInboundHandler<GameResponseMessage> {
    // ===========================================================
    // Constants
    // ===========================================================
    private static Logger log = LoggerFactory.getLogger(CommonTCPHandler.class);


    // ===========================================================
    // Fields
    // ===========================================================
    private AtomicInteger         mConnectTimes = new AtomicInteger();
    private ChannelHandlerContext mContext;
    private ChannelPromise        mPromise;
    private long                  mMainThread;
    private boolean               enableSynchronizedCommunication;


    public  String sessionId;
    public  Long userId;
    public  Integer posId;
    public Integer landlord;
    public List<Integer> cards;
    public boolean status;
    public static AtomicLong userIdGenerator = new AtomicLong(30001);

    public static AtomicLong activeCount = new AtomicLong(0);
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(12, 1000, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000));

    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================

    public void enableSynchronizedCommunication(boolean pSynchronized) {
        this.enableSynchronizedCommunication = pSynchronized;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void exceptionCaught(ChannelHandlerContext pContext, Throwable pCause) {
        String message = pCause.getMessage();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext pContext, GameResponseMessage responseMessage) {
        GameRequestMessage gameRequestMessage = new GameRequestMessage();
        GameRequestMessageHead requestMessageHead = new GameRequestMessageHead();
        requestMessageHead.setCmd(CommandCode.HEART_BEAT);
        gameRequestMessage.setHead(requestMessageHead);
        pContext.writeAndFlush(gameRequestMessage);
        threadPoolExecutor.execute(()->{
            Codec<GameResponseMessage> responseCodec = ClientDecoder.responseCodec;
            try {
                GameResponseMessageHead head = responseMessage.getHead();



                int cmd =head.getCmd();
                if(cmd == CommandCode.LOGIN){

                    Any any = responseMessage.getAny();
                    LoginResponse unpack = any.unpack(LoginResponse.class);
                    sessionId = unpack.getSessionId();

                    GameRequestMessage match = new GameRequestMessage();
                    GameRequestMessageHead matchHead = new GameRequestMessageHead();
                    matchHead.setSessionId(sessionId);
                    matchHead.setCmd(CommandCode.MATCH);

                    match.setHead(matchHead);

                    MatchRequest matchRequest = new MatchRequest();
                    matchRequest.setUserId(userId);
                    match.setObject(Any.pack(matchRequest));
                    pContext.writeAndFlush(match);
                    return;
                }if(cmd == PushCommandCode.SETTLEMENT){
                    return;
                }
                if(cmd == PushCommandCode.GAME_START){
                    Any any = responseMessage.getAny();
                    GameStartResponse unpack = any.unpack(GameStartResponse.class);
                    posId = unpack.getPosId();
                    cards = unpack.getCards();
                    return;
                }

                if(cmd == PushCommandCode.CALL_SCORE_OPTION){
                    if(posId == 0){
                        GameRequestMessage callScore = new GameRequestMessage();
                        GameRequestMessageHead callScoreHead = new GameRequestMessageHead();
                        callScoreHead.setSessionId(sessionId);
                        callScoreHead.setCmd(CommandCode.CALL_SOCRE);

                        callScore.setHead(callScoreHead);

                        CallScoreRequest callScoreRequest = new CallScoreRequest();
                        callScoreRequest.setCallScore(1);
                        Any pack = Any.pack(callScoreRequest);

                        callScore.setObject(pack);
                        pContext.writeAndFlush(callScore);
                        return;
                    }

                }
                if(cmd == PushCommandCode.CALL_SCORE){
                    return;
                }
                if(cmd == PushCommandCode.LANDLORD_OWNERSHIP){
                    Any any = responseMessage.getAny();
                    LandlordOwnershipResponse unpack = any.unpack(LandlordOwnershipResponse.class);
                    landlord = unpack.getLandlordPosId();
                    if(posId.equals(landlord) ){
                        cards.addAll(unpack.getHoleCards());
                    }

                    return;
                }
                if(cmd == PushCommandCode.REDOUBLE_OPTION){

                    GameRequestMessage msg = new GameRequestMessage();
                    GameRequestMessageHead msgHead = new GameRequestMessageHead();
                    msgHead.setSessionId(sessionId);
                    msgHead.setCmd(CommandCode.REDOUBLE_SCORE);
                    msg.setHead(msgHead);

                    RedoubleScoreRequest request = new RedoubleScoreRequest();
                    request.setPosId(posId);
                    request.setRedoubleNum(2);

                    Any pack = Any.pack(request);
                    msg.setObject(pack);
                    pContext.writeAndFlush(msg);
                    return;

                } if(cmd == PushCommandCode.FARMERS_REDOUBLE){
                    return;
                }

                if(cmd == PushCommandCode.LANDOWNERS_REDOUBLE){
                    return;

                }if(cmd == PushCommandCode.REDOUBLE_DETAIL){
                    status = true;
                    return;
                }
                if(cmd == PushCommandCode.CURRENT_OPERATOR && status){
                    Any any = responseMessage.getAny();
                    CurrentOperatorResponse unpack = any.unpack(CurrentOperatorResponse.class);
                    List<Integer> operatorList = unpack.getOperatorList();
                    if(operatorList.contains(landlord) && posId == landlord){
                        GameRequestMessage msg = new GameRequestMessage();
                        GameRequestMessageHead msgHead = new GameRequestMessageHead();
                        msgHead.setSessionId(sessionId);
                        msgHead.setCmd(CommandCode.OPERATION_CARD);
                        msg.setHead(msgHead);
                        if(cards.size() > 0){

                            Integer card = cards.remove(cards.size() - 1);
                            OperationCardRequest request = new OperationCardRequest();
                            List<Integer> objects = new ArrayList<>();
                            objects.add(card);
                            request.setCards(objects);
                            request.setPosId(posId);

                            Any pack = Any.pack(request);
                            msg.setObject(pack);

                            pContext.writeAndFlush(msg);
                        }
                        return;
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("---------------解码失败----------------");
            }

        });
    }

    @Override
    public void channelActive(ChannelHandlerContext pContext) {
       log.info("-----------------激活数量：{}",activeCount.getAndIncrement());
        GameRequestMessage heartBeatGameRequestMessage = new GameRequestMessage();
        GameRequestMessageHead requestMessageHead = new GameRequestMessageHead();
        requestMessageHead.setCmd(CommandCode.HEART_BEAT);
        heartBeatGameRequestMessage.setHead(requestMessageHead);
        pContext.writeAndFlush(heartBeatGameRequestMessage);

        GameRequestMessage gameRequestMessage = new GameRequestMessage();
        GameRequestMessageHead head = new GameRequestMessageHead();
        head.setCmd(CommandCode.LOGIN);

        userId = userIdGenerator.getAndIncrement();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserId(userId);
        try {
            Any any = Any.pack(loginRequest);
            gameRequestMessage.setHead(head);
            gameRequestMessage.setObject(any);
            pContext.channel().writeAndFlush(gameRequestMessage);

            System.out.println("---------------发送消息完成-----------------");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("---------------发送消息异常-----------------");
        }
        onClientActive(pContext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext pContext) {
        log.info("------------------关闭通道 激活数量为：{}",activeCount.getAndDecrement());
        if(this.mPromise != null && !this.mPromise.isDone()){
            this.mPromise.setFailure(new RuntimeException("TCP client channel inactive"));
        }
        onClientInactive(pContext);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext pContext, Object pEvent) {
        //super.userEventTriggered(pContext, pEvent);
        if (pEvent instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) pEvent;
            //LOG.dd("TCP idle state event state[%s]", e.state());
            switch (e.state()) {
                case READER_IDLE:
                case WRITER_IDLE:
                case ALL_IDLE:
                    onKeepAlive(pContext);
                    break;
            }
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 接收数据
     * @param pContext ChannelHandlerContext
     * @param pData 收到的数据
     * @param pPromise 此数据前发送请求的promise
     * @return true 外部已经处理了 ChannelPromise
     */
    protected abstract boolean onReceive(ChannelHandlerContext pContext, ByteBuf pData, ChannelPromise pPromise);

    /**
     * 客户端开始工作
     * @param pContext ChannelHandlerContext
     */
    protected abstract void onClientActive(ChannelHandlerContext pContext);

    /**
     * 客户端停止工作
     * @param pContext ChannelHandlerContext
     */
    protected abstract void onClientInactive(ChannelHandlerContext pContext);

    /**
     * 触发心跳
     * @param pContext ChannelHandlerContext
     */
    protected abstract void onKeepAlive(ChannelHandlerContext pContext);

    /**
     * 异步通信<br/>
     * 只有 enableSynchronizedCommunication(false) 关闭同步模式才能使用才方法通信<br/>
     * 默认是异步模式
     *
     * @param pData 报文
     */
    public void send(ByteBuf pData){
        if(enableSynchronizedCommunication){
            throw new RuntimeException("synchronized mode enabled");
        }
        if(this.mContext != null && this.mContext.channel().isActive() && this.mContext.channel().isWritable()){
            this.mContext.writeAndFlush(pData);
        }{

        }
    }

    /**
     * 同步通信，即请求报文后必须收到返回才能进行下一次请求<br/>
     * 此方法必须在线程中调用<br/>
     * 只有 enableSynchronizedCommunication(true) 开启同步模式才能使用才方法通信
     *
     * @param pData 报文
     * @param pListener 完成监听
     */
    public synchronized void sendSynchronized(ByteBuf pData, ChannelFutureListener pListener, long pTimeoutMillis){
        if(!enableSynchronizedCommunication){
            throw new RuntimeException("synchronized mode disabled");
        }
        if(this.mMainThread == Thread.currentThread().getId()){
            throw new RuntimeException("cannot run this method in client's thread");
        }
        if(this.mContext != null && this.mContext.channel().isActive() && this.mContext.channel().isWritable()){
            this.mPromise = this.mContext.writeAndFlush(pData).channel().newPromise();
            try {
                if(pListener != null){
                    this.mPromise.addListener(pListener);
                }
                if(pTimeoutMillis > 0){
                    this.mPromise.await(pTimeoutMillis);
                }{
                    this.mPromise.await();
                }
            } catch (InterruptedException ignored) {}
        }{

        }
    }
    public synchronized void sendSynchronized(ByteBuf pData){
        sendSynchronized(pData, null, 0);
    }

    public synchronized void sendSynchronized(ByteBuf pData, ChannelFutureListener pListener){
        sendSynchronized(pData, pListener, 0);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
