package com.yude.game.ddz.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.game.ddz.controller.client.ClientChannelHandlerInitializer;

import com.yude.game.ddz.controller.client.CommonTCPClient;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * @Author: HH
 * @Date: 2020/6/24 17:48
 * @Version: 1.0
 * @Declare:
 */
public class ClientApplication {
    public static void main(String[] args) {
        for (int i = 0; i < 900; ++i) {
            new Thread(() -> {
                ClientChannelHandlerInitializer initializer = new ClientChannelHandlerInitializer();
                CommonTCPClient client = CommonTCPClient.create("192.168.22.29", 8080, initializer);

                client.run();
            }).start();
            try {
                Random random = new Random();
                int nextInt = random.nextInt(50);
                Thread.sleep(nextInt);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("");

        /*Set<Class<?>> classes = new HashSet<>();
        classes.addAll(ClassUtil.scanPackageByAnnotation("com.yude",ProtobufClass.class));
        for (Class c : classes) {
            ProtobufProxy.create(c);
        }
        System.out.println(classes);*/

    }

}
