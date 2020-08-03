package com.yude.game.ddz.controller;

import com.yude.game.doudizhu.domain.card.CardType;
import com.yude.game.doudizhu.domain.card.PokerProp;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author: HH
 * @Date: 2020/7/4 10:28
 * @Version: 1.0
 * @Declare:
 */

public class CardTypeTest {

    public static void main(String[] args) {
        /***
         * 上一版
         */
        /*int[] 四带二 = PokerProp.getCardValueByCard3(new PokerProp.CardEunm[]{PokerProp.CardEunm.方块3, PokerProp.CardEunm.黑桃3, PokerProp.CardEunm.红桃3, PokerProp.CardEunm.梅花3, PokerProp.CardEunm.红桃2, PokerProp.CardEunm.梅花2, PokerProp.CardEunm.黑桃6, PokerProp.CardEunm.梅花6});

        Integer[] 四带二_可变数组 = {PokerProp.CardEunm.方块3.ordinal() + 1, PokerProp.CardEunm.黑桃3.ordinal() + 1, PokerProp.CardEunm.红桃3.ordinal() + 1, PokerProp.CardEunm.梅花3.ordinal() + 1, PokerProp.CardEunm.红桃2.ordinal() + 1, PokerProp.CardEunm.梅花2.ordinal() + 1, PokerProp.CardEunm.黑桃6.ordinal() + 1, PokerProp.CardEunm.梅花6.ordinal() + 1};

        int[] 顺子 = PokerProp.getCardValueByCard3(new PokerProp.CardEunm[]{PokerProp.CardEunm.方块3, PokerProp.CardEunm.黑桃4, PokerProp.CardEunm.红桃5, PokerProp.CardEunm.梅花6, PokerProp.CardEunm.红桃7, PokerProp.CardEunm.梅花8, PokerProp.CardEunm.黑桃9,});

        int[] 连对 = PokerProp.getCardValueByCard3(new PokerProp.CardEunm[]{PokerProp.CardEunm.方块3, PokerProp.CardEunm.黑桃4, PokerProp.CardEunm.红桃5, PokerProp.CardEunm.梅花6, PokerProp.CardEunm.红桃7, PokerProp.CardEunm.梅花7, PokerProp.CardEunm.黑桃6,PokerProp.CardEunm.梅花5, PokerProp.CardEunm.黑桃3,PokerProp.CardEunm.梅花4});*/

        Integer[] 四带二 = PokerProp.getCardIndexsByCard(PokerProp.CardEunm.方块3, PokerProp.CardEunm.黑桃3, PokerProp.CardEunm.红桃3, PokerProp.CardEunm.梅花3,  PokerProp.CardEunm.黑桃6, PokerProp.CardEunm.梅花6);

        Integer[] 四带二_可变数组 = {PokerProp.CardEunm.方块3.ordinal() + 1, PokerProp.CardEunm.黑桃3.ordinal() + 1, PokerProp.CardEunm.红桃3.ordinal() + 1, PokerProp.CardEunm.梅花3.ordinal() + 1, PokerProp.CardEunm.红桃2.ordinal() + 1, PokerProp.CardEunm.梅花2.ordinal() + 1, PokerProp.CardEunm.黑桃6.ordinal() + 1, PokerProp.CardEunm.梅花6.ordinal() + 1};

        Integer[] 顺子 = PokerProp.getCardIndexsByCard(new PokerProp.CardEunm[]{PokerProp.CardEunm.方块3, PokerProp.CardEunm.黑桃4, PokerProp.CardEunm.红桃5, PokerProp.CardEunm.梅花6, PokerProp.CardEunm.红桃7, PokerProp.CardEunm.梅花8, PokerProp.CardEunm.黑桃9,});

        /*Integer[] 连对 = PokerProp.getCardIndexsByCard(new PokerProp.CardEunm[]{PokerProp.CardEunm.方块3, PokerProp.CardEunm.黑桃4, PokerProp.CardEunm.红桃5, PokerProp.CardEunm.梅花6, PokerProp.CardEunm.红桃7, PokerProp.CardEunm.梅花7, PokerProp.CardEunm.黑桃6,PokerProp.CardEunm.梅花5, PokerProp.CardEunm.黑桃3,PokerProp.CardEunm.梅花4});*/

        Integer[] 飞机带单牌 = PokerProp.getCardIndexsByCard(PokerProp.CardEunm.方块4, PokerProp.CardEunm.红桃3, PokerProp.CardEunm.黑桃3, PokerProp.CardEunm.红桃A, PokerProp.CardEunm.黑桃9,PokerProp.CardEunm.红桃4, PokerProp.CardEunm.黑桃4, PokerProp.CardEunm.方块3);

        Integer[] 飞机带两对 = PokerProp.getCardIndexsByCard(PokerProp.CardEunm.方块4, PokerProp.CardEunm.红桃3, PokerProp.CardEunm.黑桃3, PokerProp.CardEunm.红桃A, PokerProp.CardEunm.黑桃A,PokerProp.CardEunm.红桃4, PokerProp.CardEunm.黑桃4, PokerProp.CardEunm.方块3, PokerProp.CardEunm.红桃2, PokerProp.CardEunm.黑桃2);

        Integer[] 连对 =PokerProp.getCardIndexsByCard(PokerProp.CardEunm.方块5, PokerProp.CardEunm.黑桃5, PokerProp.CardEunm.梅花4, PokerProp.CardEunm.黑桃4, PokerProp.CardEunm.梅花3, PokerProp.CardEunm.红桃3);

        Integer[] 一炸七 = PokerProp.getCardIndexsByCard(PokerProp.CardEunm.方块7, PokerProp.CardEunm.梅花7, PokerProp.CardEunm.红桃7, PokerProp.CardEunm.黑桃7);

        Integer[] 三连飞机带单牌 = PokerProp.getCardIndexsByCard(PokerProp.CardEunm.方块Q, PokerProp.CardEunm.梅花Q, PokerProp.CardEunm.红桃Q, PokerProp.CardEunm.方块J, PokerProp.CardEunm.红桃J, PokerProp.CardEunm.黑桃J, PokerProp.CardEunm.方块10, PokerProp.CardEunm.红桃10, PokerProp.CardEunm.黑桃10, PokerProp.CardEunm.梅花7, PokerProp.CardEunm.方块4, PokerProp.CardEunm.黑桃3);

        Integer[] 四带两对 = PokerProp.getCardIndexsByCard(
                PokerProp.CardEunm.红桃7, PokerProp.CardEunm.黑桃7, PokerProp.CardEunm.红桃6, PokerProp.CardEunm.黑桃6, PokerProp.CardEunm.方块6, PokerProp.CardEunm.梅花6, PokerProp.CardEunm.方块5, PokerProp.CardEunm.黑桃5
        );

        Integer[] 四连飞机 = PokerProp.getCardIndexsByCard(PokerProp.CardEunm.红桃3, PokerProp.CardEunm.黑桃3, PokerProp.CardEunm.梅花3, PokerProp.CardEunm.方块4, PokerProp.CardEunm.梅花4, PokerProp.CardEunm.红桃4, PokerProp.CardEunm.红桃5, PokerProp.CardEunm.方块5, PokerProp.CardEunm.梅花6, PokerProp.CardEunm.方块6, PokerProp.CardEunm.黑桃5, PokerProp.CardEunm.黑桃6);
        /**
         *
         *上一版
         */
        int countSize = 1;
        long sum = 0;
       /* testVariableCard(countSize,sum,四带二_可变数组);

        List<Integer> list = Arrays.stream(四带二).boxed().collect(Collectors.toList());
        //List<Integer> list = Arrays.stream(四带二).collect(Collectors.toList());
        //(countSize,sum,list);

        //testArray(countSize,sum,四带二);*/


        testCompare();

        /**
         * 新版
         */

        testVariableCard(countSize,sum,四带二);
        /*CardType judge = CardType.四带一对.judge(feiji);
        System.out.println(judge);*/

        Integer[] cards = {42, 44, 20, 31, 32, 13, 18, 14, 6, 52, 37, 22, 53, 33, 21, 3, 40};
        CardType.sort(cards);
        System.out.println(Arrays.toString(cards));
    }

    /*public static void testList(int countSize,long sum,List<Integer> cards){

        CardType cardType2 = null;
        for(int i = 0 ; i < countSize ; ++i) {
            long beforeTime2 = System.nanoTime();
            cardType2 = CardType.judgeCardType(cards);
            sum += (System.nanoTime() - beforeTime2);
        }
        System.out.println("集合循环次数："+countSize +" 集合 总共耗时： "+sum/1000/1000+" 毫秒");
        System.out.println("集合 平均耗时："+(sum/countSize)+ "  纳秒");
        System.out.println(cardType2);
    }*/

    /*public static void testArray(int countSize,long sum,int[] cards){
        CardType cardType3 = null;
        for(int i = 0 ; i < countSize ; ++i){
            long beforeTime3 = System.nanoTime();
             cardType3 = CardType.judgeCardType(cards);
            sum += (System.nanoTime() - beforeTime3);
        }


       System.out.println("数组循环次数："+countSize+"  总共耗时： "+sum/1000/1000+" 毫秒");
        System.out.println("数组 平均耗时："+(sum/countSize)+ "  纳秒");
        System.out.println(cardType3);
    }*/

    public static void testVariableCard(int countSize,long sum,Integer[] cards){
        CardType cardType1 = null;
        for(int i = 0 ; i < countSize ; ++i) {
            long beforeTime1 = System.nanoTime();
             cardType1 = CardType.judgeCardType(cards);
            sum += System.nanoTime() - beforeTime1;
        }
        System.out.println("可变数组 循环次数："+countSize
                +" 数组 总共耗时： "+sum/1000/1000+" 毫秒");
        System.out.println("可变数组 平均耗时："+(sum/countSize)+ "  纳秒");
         System.out.println(cardType1);
    }


    public static void testCompare(){
        /*Integer[] targetIndex = {13, 8, 9, 10};
        Card targetCard = new Card(targetIndex,CardType.三带一);

        Integer[] cardIndex = {12, 14, 15, 11};
        Card card = new Card(cardIndex,CardType.三带一);
        CardType.compareCard(card,targetCard);*/

    }
}
