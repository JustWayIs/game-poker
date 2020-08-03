package com.yude.game.doudizhu.timeout;

import com.baidu.bjf.remoting.protobuf.Any;
import com.yude.game.common.dispatcher.event.DisruptorRegistrar;
import com.yude.game.common.manager.IPushManager;
import com.yude.game.common.manager.impl.PushManager;
import com.yude.game.common.timeout.TimeoutRequestGenerator;
import com.yude.game.common.timeout.TimeoutTask;
import com.yude.game.communication.dispatcher.IProducerWithTranslator;
import com.yude.game.doudizhu.domain.DouDiZhuRoom;
import com.yude.game.doudizhu.domain.DouDiZhuZone;

import com.yude.protocol.common.MessageType;
import com.yude.protocol.common.message.GameRequestMessage;
import com.yude.protocol.common.message.GameRequestMessageHead;
import com.yude.protocol.common.request.AbstractRequest;
import com.yude.protocol.common.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @Author: HH
 * @Date: 2020/7/14 15:04
 * @Version: 1.0
 * @Declare:
 */
public class DdzTimeoutTask implements TimeoutTask<DouDiZhuRoom, PushManager> {
    private static Logger log = LoggerFactory.getLogger(DdzTimeoutTask.class);
    private static List<TimeoutRequestGenerator> requestGenerators = new ArrayList<>();

    private volatile DouDiZhuRoom room;
    private DouDiZhuRoom cloneRoom;
    private IPushManager pushManager;
    private long activeTime;

    static {
        requestGenerators.add(new CallScoreRequestGenerator());
        requestGenerators.add(new RedoubleScoreRequestGenerator());
        requestGenerators.add(new OperationCardRequestGenerator());
    }

    public DdzTimeoutTask(DouDiZhuRoom room, IPushManager pushManager) {
        this.room = room;
        try {
            this.cloneRoom = (DouDiZhuRoom) room.clone();
        } catch (CloneNotSupportedException e) {
            log.error("clone room 失败 room={}", room, e);
        }
        this.pushManager = pushManager;
        this.activeTime = TimeUnit.NANOSECONDS.convert(room.getGameStatus().getTimeoutTime(), TimeUnit.SECONDS) + System.nanoTime();
    }

    public DdzTimeoutTask(long milliseconds,DouDiZhuRoom room,IPushManager pushManager) {
        log.info("自定义时间： milliseconds={}",milliseconds);
        this.room = room;
        try {
            this.cloneRoom = (DouDiZhuRoom) this.room.clone();
        } catch (CloneNotSupportedException e) {
            log.error("clone room 失败 room={}", room, e);
        }
        this.pushManager = pushManager;
        this.activeTime = TimeUnit.NANOSECONDS.convert(milliseconds,TimeUnit.MILLISECONDS)+ System.nanoTime();
    }



    @Override
    public void execute() {
        log.info("超时任务触发：  roomId={}  time={}", room.getRoomId(), System.nanoTime());

        //这个设想要基于没有重复的step【至少要保证游戏的 玩家操作流程 的step不重复】
        Integer maxStepCount = DdzTimeoutTaskPool.uselessTaskMap.get(cloneRoom.getRoomId());
        log.info("失效的任务： {}", DdzTimeoutTaskPool.uselessTaskMap);
        if (maxStepCount != null && maxStepCount >= cloneRoom.getStep()) {
            log.info("超时任务已失效：roomId={} step:{}", room.getRoomId(), cloneRoom.getStep());
            return;
        }
        DouDiZhuZone cloneGameZone = cloneRoom.getDouDiZhuZone();
        DouDiZhuZone douDiZhuZone = room.getDouDiZhuZone();
        if(cloneGameZone.getRound() != douDiZhuZone.getRound() || cloneGameZone.getInning() != douDiZhuZone.getInning()){
            log.info("不是同一局游戏，超时任务失效 roomId={}  zoneId={} cloneZoneId={}",room.getRoomId(),douDiZhuZone.getZoneId(),cloneGameZone.getZoneId());
            return;
        }

        Request request = null;
        int command = 0;
        try {

            for (TimeoutRequestGenerator generator : requestGenerators) {
                Optional<TimeoutRequestGenerator> match = generator.match(room.getGameStatus());
                if (match.isPresent()) {
                    TimeoutRequestGenerator practicalGenerator = match.get();
                    log.info("当前 TimeoutRequestGenerator: {}", practicalGenerator);
                    request = practicalGenerator.build(cloneRoom);
                    command = practicalGenerator.getCmd();
                    break;
                }
            }

            //可以用来做双重保证
            int beforeStep = cloneRoom.getStep();
            int nowStep = room.getStep();
            //重要：用于最大程度的消除时间差。在没有做到 修改seat的某个操作状态 与 gameZone的step 保持原子性的情况下。
            if (beforeStep != nowStep) {
                log.info("游戏步骤已经发生改变，不再执行超时任务： roomId={}  beforeStep={}  nowStep={}", cloneRoom.getRoomId(), beforeStep, nowStep);
                return;
            }
            if(request == null){
                log.info("超时任务请求数据为null  roomId={} activeTime={}",room.getRoomId(),activeTime);
                return;
            }

            AbstractRequest abstractRequest = (AbstractRequest) request;
            Long channelUserId = abstractRequest.getChannelUserId();
            room.userSerialTimeoutCountAdd(channelUserId);

            log.info("超时请求：command={}  request={}",command,request);
            Any any = Any.pack(request);
            GameRequestMessage gameRequestMessage = new GameRequestMessage();
            gameRequestMessage.setObject(any);
            GameRequestMessageHead head = new GameRequestMessageHead();
            head.setCmd(command);
            head.setType(MessageType.TIMEOUT.getType());
            head.setRoomId(room.getRoomId());
            gameRequestMessage.setHead(head);

            IProducerWithTranslator eventPublisher = DisruptorRegistrar.needEventPublisher(MessageType.SERVICE, room.getRoomId());
            log.info("发布超时任务事件： roomId={}  事件发布器={}",cloneRoom.getRoomId(),eventPublisher);
            eventPublisher.publish(gameRequestMessage, null);


        } catch (Exception e) {
            log.error("执行超时任务异常： roomId={}", room.getRoomId(), e);
        }
    }

    @Override
    public long getRemaining() {
        return TimeUnit.MILLISECONDS.convert(activeTime - System.nanoTime(),TimeUnit.NANOSECONDS);
    }

    //想要一个房间只有一个Task的实现没有成功，对activeTime的重置没有生效，应该是由于getDelay只被调用一次
    @Override
    public long getDelay(TimeUnit unit) {
        return this.activeTime - System.nanoTime();
    }

    @Override
    public int compareTo(Delayed o) {
        long delay = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        if (delay > 0) {
            return 1;
        }
        if (delay == 0) {
            return 0;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "DdzTimeoutTask{" +
                "room=" + room.getRoomId() +
                ", cloneRoom=" + cloneRoom.getRoomId() +
                ", step =" + room.getStep() +
                ", cloneStep=" + cloneRoom.getStep() +
                ", activeTime=" + activeTime +
                '}';
    }
}
