package com.yude.game.ddz.controller;


import com.yude.game.ddz.controller.client.ClientChannelHandlerInitializer;

import com.yude.game.ddz.controller.client.CommonTCPClient;

import java.util.Random;

/**
 * @Author: HH
 * @Date: 2020/6/24 17:48
 * @Version: 1.0
 * @Declare:
 */
public class ClientApplication {
    public static void main(String[] args) {
        for (int i = 0; i < 1200; ++i) {
            new Thread(() -> {
                ClientChannelHandlerInitializer initializer = new ClientChannelHandlerInitializer();
                CommonTCPClient client = CommonTCPClient.create("192.168.6.20", 8080, initializer);

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

    }
}
