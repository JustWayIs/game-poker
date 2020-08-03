package com.yude.game.ddz.controller.generator;

import cn.hutool.core.io.file.FileWriter;
import com.yude.game.doudizhu.domain.card.CardTypeEnum;


import java.io.File;
import java.util.*;

/**
 * @Author: HH
 * @Date: 2020/7/23 18:03
 * @Version: 1.0
 * @Declare:目标：100000000000000-1-1-1 -> {牌的数量的数组:15位(3、4...大王)}-{牌型：单牌、对子、三条...}-{出牌数：三条的牌型出牌数有3种}-{等级：用于比大小}
 */
public class DdzTableGenerator {
    public static Map<String,String> map = new LinkedHashMap<>();


    public static void main(String[] args) {
        //FileWriter fileWriter = new FileWriter("ddzTable.properties");
        /*for(int i = 1; i <5 ;++i){

        }*/

        List<String> list = new ArrayList<>();


        //type 1
        List<String> singleList = CardTypeSimpleCard(1, CardTypeEnum.单牌.getType(), 1, 15);
        System.out.println("单牌 数量:" + singleList.size() + " " + singleList);
        list.addAll(singleList);

        //type 2
        //没有一对鬼
        List<String> doubles = CardTypeSimpleCard(2, CardTypeEnum.对子.getType(), 2, 13);
        System.out.println("对子 数量:" + doubles.size() + " " + doubles);
        list.addAll(doubles);

        //type 3
        List<String> three = CardTypeSimpleCard(3, CardTypeEnum.三张不带.getType(), 3, 13);
        System.out.println("三条 数量:" + three.size() + " " + three);
        list.addAll(three);

        List<String> 三带一 = 三带(CardTypeEnum.三带一.getType(), false,1);
        System.out.println("三带一 数量:" + 三带一.size() + " " + 三带一);
        list.addAll(三带一);

        List<String> 三带一对 = 三带(CardTypeEnum.三带一对.getType(), true,2);
        System.out.println("三带一对 数量:" + 三带一对.size() + " " + 三带一对);
        list.addAll(三带一对);

        ////type 4 这里的type不能使用CardTypeEnum。因为这里的type有标识牌数量的关系，和生成算法进行了绑定。如果四带的type不是4，会发生错乱。也就是说枚举的type绑定无法修改，解耦合
        List<String> 四带两对 = 四带(CardTypeEnum.四带两对.getType(), true,4, 2);
        System.out.println("四带两对 数量:" + 四带两对.size() + " " + 四带两对);
        list.addAll(四带两对);

        List<String> 四带二 = 四带(CardTypeEnum.四带二.getType(), false,2, 1);
        System.out.println("四带二 数量:" + 四带二.size() + " " + 四带二);
        list.addAll(四带二);


        //type5 顺子
        List<String> shunZi = shunZi(CardTypeEnum.顺子.getType());
        System.out.println("顺子 数量:" + shunZi.size() + " " + shunZi);
        list.addAll(shunZi);

        //type6 连对
        List<String> lianDuiList = doubleCardSerial(CardTypeEnum.连对.getType());
        System.out.println("连对 数量:" + lianDuiList.size() + " " + lianDuiList);
        list.addAll(lianDuiList);

        //type7
        List<String> 飞机不带 = plane(CardTypeEnum.飞机不带.getType(), 0);
        System.out.println("飞机不带 数量:" + 飞机不带.size() + " " + 飞机不带);
        list.addAll(飞机不带);

        List<String> 飞机带单牌 = plane(CardTypeEnum.飞机带单牌.getType(), 1);
        System.out.println("飞机带单牌 数量:" + 飞机带单牌.size() + " " + 飞机带单牌);
        list.addAll(飞机带单牌);

        List<String> 飞机带对子 = plane(CardTypeEnum.飞机带对子.getType(), 2);
        System.out.println("飞机带对子 数量:" + 飞机带对子.size() + " " + 飞机带对子);
        list.addAll(飞机带对子);

        //type n-1炸弹
        List<String> four = CardTypeSimpleCard(4, CardTypeEnum.炸弹.getType(), 4, 13);
        System.out.println("炸弹-四张 数量:" + four.size() + " " + four);
        list.addAll(four);

        //type n 王炸
        List<String> 王炸 = bigBomb(CardTypeEnum.王炸.getType());
        System.out.println("王炸 数量:" + 王炸.size() + " " + 王炸);
        list.addAll(王炸);

        System.out.println("斗地主牌型总数量："+list.size());
        System.out.println("去重后的数量："+map.size());



        generator();

    }

    public static void generator(){
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        String tailKey = null;
        while (iterator.hasNext()){
            tailKey = iterator.next().getKey();
        }

        File file = new File("E:\\My-work\\project\\service-frameworks\\game-ddz\\src\\main\\resources\\config\\ddz-table.properties");
        file.delete();
        FileWriter fileWriter1 = new FileWriter("E:\\My-work\\project\\service-frameworks\\game-ddz\\src\\main\\resources\\config\\ddz-table.properties");
        for(Map.Entry<String,String> entry : map.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            fileWriter1.append(key+"="+value);
            if(!key.equals(tailKey)){
                fileWriter1.append("\r\n");
            }
        }
    }

    public static List<String> CardTypeSimpleCard(int value, int type, int cardNum, int length) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < length; ++i) {
            int[] arry = new int[15];
            arry[i] = value;

            //空格没法去掉
            /*String s = Arrays.toString(arry).trim();
            String result = s.replace("[", "").replace("]", "").replace(",", "");
            StringBuilder str = new StringBuilder(result);*/
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < arry.length; ++j) {
                str.append(arry[j]);
            }

            String key = str.toString();
            StringBuilder strValue = new StringBuilder();
            strValue.append(type).append("-").append(cardNum).append("-").append(i + 1);
            String valueStr = strValue.toString();
            put(key,valueStr);


            str.append("-").append(type).append("-").append(cardNum).append("-").append(i + 1);
            list.add(new String(str));
        }

        return list;
    }

    /**
     *
     * @param type
     * @param isDouble 是否带一对
     * @param num
     * @return
     */
    public static List<String> 三带(int type, boolean isDouble,int num) {
        List<String> list = new ArrayList<>();
        //没有三张鬼
        for (int i = 0; i < 13; i++) {
            int[] arry = new int[15];
            arry[i] = 3;

            int length = 15;
            if(isDouble){
                length = 13;
            }
            //不能带鬼
            for (int j = 0; j < length; ++j) {
                if (j != i) {
                    arry[j] = num;
                    StringBuilder str = new StringBuilder();
                    for (int k = 0; k < arry.length; ++k) {
                        str.append(arry[k]);
                    }
                    String key = str.toString();
                    StringBuilder strValue = new StringBuilder();
                    strValue.append(type).append("-").append(3 + num).append("-").append(i + 1);
                    String valueStr = strValue.toString();
                    put(key,valueStr);

                    str.append("-").append(type).append("-").append(3 + num).append("-").append(i + 1);
                    list.add(new String(str));
                    arry[j] = 0;
                }
            }
        }
        return list;
    }

    /**
     *
     * @param isDuble 是否是对子
     * @param num
     * @param step
     * @return
     */
    public static List<String> 四带(int type,boolean isDuble, int num, int step) {
        List<String> list = new ArrayList<>();
        //没有四张鬼
        for (int i = 0; i < 13; i++) {
            int[] arry = new int[15];
            arry[i] = 4;

            //不能带鬼
            for (int j = 0; j < 13; ++j) {
                if (j != i) {
                    arry[j] += step;

                    for (int l = 0; l < arry.length; ++l) {
                        StringBuilder str = new StringBuilder();
                        if (l != i) {
                            if (isDuble && l == j) {
                                continue;
                            }
                            arry[l] += step;
                            for (int k = 0; k < arry.length; ++k) {
                                str.append(arry[k]);
                            }
                            String key = str.toString();
                            StringBuilder strValue = new StringBuilder();
                            strValue.append(type).append("-").append(4 + num).append("-").append(i + 1);
                            String valueStr = strValue.toString();
                            put(key,valueStr);

                            str.append("-").append(type).append("-").append(4 + num).append("-").append(i + 1);
                            list.add(new String(str));
                            arry[l] -= step;
                        }

                    }
                    arry[j] -= step;

                }
            }
        }
        return list;
    }

    public static List<String> shunZi(int type) {
        List<String> list = new ArrayList<>();
        int cursor = 5;
        int length = 12;
        //顺子没有 大小王、2
        for (int i = 0; i < length; i++) {
            int[] arry = new int[15];


            if (cursor < length) {

                serialRecursion(1, arry, type, cursor, cursor, list, i, 0,1);
            }
            cursor++;

        }
        return list;
    }

    public static void serialRecursion(int size, int[] arry, int type, int num, int cursor, List<String> list, int rating, int initJ,int isDouble) {
        //牌为2
        if (cursor > 12) {
            return;
        }
        for (int j = initJ; j < cursor; j++) {
            arry[j] = size;
        }
        StringBuilder str = new StringBuilder();
        for (int k = 0; k < arry.length; ++k) {
            str.append(arry[k]);
        }
        String key = str.toString();
        StringBuilder strValue = new StringBuilder();
        strValue.append(type).append("-").append(num*isDouble).append("-").append(initJ + 1);
        String valueStr = strValue.toString();
        put(key,valueStr);

        str.append("-").append(type).append("-").append(num*isDouble).append("-").append(initJ + 1);
        list.add(new String(str));

        arry[initJ] = 0;
        initJ++;
        cursor++;
        serialRecursion(size, arry, type, num, cursor, list, rating, initJ,isDouble);
    }

    public static List<String> doubleCardSerial(int type) {
        List<String> list = new ArrayList<>();
        int cursor = 3;
        int length = 12;
        //连对没有 大小王、2
        for (int i = 0; i < length; i++) {
            int[] arry = new int[15];


            if (cursor < length) {

                serialRecursion(2, arry, type, cursor, cursor, list, i, 0,2);
            }
            cursor++;

        }
        return list;
    }

    public static List<String> plane(int type, int num) {
        List<String> list = new ArrayList<>();
        int length = 12;
        for (int s = 2; s <= 6; s++) {

            for (int i = 0; i < length; i++) {
                int[] arry = new int[15];
                boolean isOut = false;
                for (int j = i; j - i < s; ++j) {
                    if (j >= 12) {
                        isOut = true;
                    }
                    arry[j] = 3;

                }
                if (isOut) {
                    break;
                }

                int cursor = i + s;
                if (num == 0) {
                    StringBuilder str = new StringBuilder();
                    for (int k = 0; k < arry.length; ++k) {
                        str.append(arry[k]);
                    }
                    String key = str.toString();
                    StringBuilder strValue = new StringBuilder();
                    strValue.append(type).append("-").append(s * 3 + num * cursor).append("-").append(i + 1);;
                    String valueStr = strValue.toString();
                    put(key,valueStr);

                    str.append("-").append(type).append("-").append(s * 3 + num * cursor).append("-").append(i + 1);
                    list.add(new String(str));
                    continue;
                }
                planeAdd(arry, cursor, type, s, num, list, i);

            }
        }

        return list;
    }

    /**
     * @param arry
     * @param type
     * @param wingNum 翅膀数
     * @param num     {0,1,2} 分别代表 不带，带单牌，带对子
     */
    public static void planeAdd(int[] arry, int cursor, int type, int wingNum, int num, List<String> list, int rating) {
        List<Integer> validList = new ArrayList<>();
        int length = 15;
        length = num == 0 ? num : length;
        length = num == 2 ? 14 - num : length;
        /*for (int i = 0; i < length; i++) {
            *//**
             * 其实只要判断当前索引的array的值是否大于0就行了
             *//*
            int cur = cursor;
            boolean isOut = false;
            for (int n = 0; n < wingNum; n++) {
                if (i == --cur) {
                    isOut = true;
                }
                continue;
            }
            if (isOut) {
                continue;
            }
            validList.add(i);
        }*/

        for (int i = 0; i < length; i++) {
            /**
             * 其实只要判断当前索引的array的值是否大于0就行了
             */
            if(arry[i] == 0){
                validList.add(i);
            }

        }
        fillListRecursion(arry,validList,list,wingNum,num,type,wingNum,rating);
    }

    public static List<String> fillListRecursion(int[] array,List<Integer> validList,List<String> list,int count,int num,int type,int wingNum,int rating){
        int cardNum = wingNum * 3 + num * wingNum;
        //H2 这里限制张数的话，可能不止这里要限制
        if(cardNum > 20){
            return list;
        }

        int copyCount = count;
        for(Integer i : validList){
            array[i] = num;
            if(count == 1){
                StringBuilder str = new StringBuilder();
                for (int k = 0; k < array.length; ++k) {
                    str.append(array[k]);
                }
                String key = str.toString();
                StringBuilder strValue = new StringBuilder();

                strValue.append(type).append("-").append(cardNum).append("-").append(rating + 1);
                String valueStr = strValue.toString();
                put(key,valueStr);

                str.append("-").append(type).append("-").append(wingNum * 3 + num * wingNum).append("-").append(rating + 1);
                list.add(new String(str));
            }else{

                int i1 = validList.indexOf(i);
                List<Integer> validList2 = new ArrayList<>(validList.subList(i1+1,validList.size()));
                //int[] copyArray = new int[array.length];
                //System.arraycopy(array,0,copyArray,0,array.length);
                fillListRecursion(array,validList2,list,--copyCount,num,type,wingNum,rating);
            }
            array[i] = 0;
            copyCount = count;
        }

        return list;
    }

    public static List<String> bigBomb(int type) {
        List<String> list = new ArrayList<>();
        int[] arry = new int[15];
        for (int i = 13; i < 15; i++) {
            arry[i] = 1;
        }

        StringBuilder str = new StringBuilder();
        for (int k = 0; k < arry.length; ++k) {
            str.append(arry[k]);
        }
        String key = str.toString();
        StringBuilder strValue = new StringBuilder();
        strValue.append(type).append("-").append(2).append("-").append(1);
        String valueStr = strValue.toString();
        put(key,valueStr);


        str.append("-").append(type).append("-").append(2).append("-").append(1);
        list.add(new String(str));

        return list;
    }

    public static void put(String key,String value){
        String s = map.get(key);
        if(s != null){
           // System.out.println("-----key : "+key);
            if(!s.equals(value)){
                System.out.println("-------value:"+value);
            }
        }

        map.put(key,value);
    }
}
