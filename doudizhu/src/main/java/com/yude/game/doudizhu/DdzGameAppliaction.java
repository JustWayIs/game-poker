package com.yude.game.doudizhu;


import cn.hutool.core.util.ClassUtil;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.game.communication.tcp.server.CommonTCPServer;
import com.yude.game.doudizhu.domain.DouDiZhuRoom;
import com.yude.game.doudizhu.domain.manager.RoomManager;
import com.yude.game.doudizhu.timeout.DdzTimeoutTaskPool;
import com.yude.game.doudizhu.util.DdzTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: HH
 * @Date: 2020/6/17 15:56
 * @Version: 1.0
 * @Declare:
 */


@Component
@ComponentScan({"com.yude","base.service.frameworks.web.server.core"})
@PropertySource("classpath:config/core.properties")
public class DdzGameAppliaction {
    private static final Logger log = LoggerFactory.getLogger(DdzGameAppliaction.class);
   /* private static final int size = Runtime.getRuntime().availableProcessors() * 2;

    @Value("${netty.port}")
    private  int port;

    @Value("${netty.bossThreadNum:"+size+"}")
    private int bossThreadNum = 1;

    //根据运行状态调整到最优。使用@Value意味着必须传值。CPU核心数又是不确定的，该不该把这个值交由properties还有待商榷
    @Value("${netty.workThreadNum}")
    private int workThreadNum = Runtime.getRuntime().availableProcessors() * 2;*/

    public static void main(String[] args) {
        try {
            log.info("----------斗地主游戏服启动中-----------");

            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DdzGameAppliaction.class);
            CommonTCPServer server = context.getBean(CommonTCPServer.class);
            Thread thread = new Thread(server);
            thread.setName("Thread-TCP-Server");
            thread.start();

            RoomManager roomManager = context.getBean(RoomManager.class);
            roomManager.initRoomType(DouDiZhuRoom.class,3);

            DdzTable.init();
            jprotobufClassInit();
            DdzTimeoutTaskPool.getInstance().init();
        } catch (Exception e) {
           log.error("斗地主游戏服启动失败",e);
           System.exit(1);
        }
    }

    public static void jprotobufClassInit(){
        Set<Class<?>> classes = new HashSet<>();
        classes.addAll(ClassUtil.scanPackageByAnnotation("com.yude", ProtobufClass.class));
        for (Class c : classes) {
            ProtobufProxy.create(c);
        }
    }
}
