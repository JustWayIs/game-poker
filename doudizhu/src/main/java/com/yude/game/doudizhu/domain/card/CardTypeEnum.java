package com.yude.game.doudizhu.domain.card;

/**
 * @Author: HH
 * @Date: 2020/7/24 18:02
 * @Version: 1.0
 * @Declare:
 */
public enum CardTypeEnum {
    /**
     * type = ordinal + 1 : type的值在生成表的时候就决定了。如果有变动需要重新生成表
     */
    过("0"),
    单牌("1"),
    对子("2"),
    三张不带("3"),
    三带一("4"),
    三带一对("5"),
    四带二("6"),
    四带两对("8"),
    顺子("n"),
    连对("n"),
    飞机不带("3*翅膀数"),
    /**
     * //存在分开的必要，5个飞机带单牌的数量 等于 4个飞机带对子，会导致被标识为一个牌型，如果type一致的话
     */
    飞机带单牌("4*翅膀数"),
    飞机带对子("5*翅膀数"),
    炸弹("4"),
    王炸("2");


   private String cardNum;

    CardTypeEnum(String cardNum) {
        this.cardNum = cardNum;
    }

    public int getType(){
        return  ordinal();
    }

    public static CardTypeEnum getCardTypeEnumByType(int type){
        return  CardTypeEnum.values()[type];
    }
}
