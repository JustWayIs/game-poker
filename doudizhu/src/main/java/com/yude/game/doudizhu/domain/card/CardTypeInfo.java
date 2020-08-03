package com.yude.game.doudizhu.domain.card;



import java.util.Objects;

/**
 * @Author: HH
 * @Date: 2020/7/24 18:00
 * @Version: 1.0
 * @Declare:
 */
public class CardTypeInfo implements Comparable{
    /**
     * 类型
     */
    private CardTypeEnum type;
    /**
     * 牌的数量
     */
    private int cardNum;
    /**
     * 牌组在该牌型的等级
     */
    private int rating;

    /**
     * 冗余进来
     */
    private String cardKey;

    public CardTypeInfo(CardTypeEnum type, int cardNum, int rating) {
        this.type = type;
        this.cardNum = cardNum;
        this.rating = rating;
    }

    public CardTypeInfo(CardTypeEnum type, int cardNum, int rating, String cardKey) {
        this.type = type;
        this.cardNum = cardNum;
        this.rating = rating;
        this.cardKey = cardKey;
    }

    public int getType() {
        return type.getType();
    }

    public CardTypeInfo setType(CardTypeEnum type) {
        this.type = type;
        return this;
    }

    public CardTypeEnum type(){
        return type;
    }

    public int getCardNum() {
        return cardNum;
    }

    public CardTypeInfo setCardNum(int cardNum) {
        this.cardNum = cardNum;
        return this;
    }

    public int getRating() {
        return rating;
    }

    public CardTypeInfo setRating(int rating) {
        this.rating = rating;
        return this;
    }

    public String getCardKey() {
        return cardKey;
    }

    public CardTypeInfo setCardKey(String cardKey) {
        this.cardKey = cardKey;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CardTypeInfo that = (CardTypeInfo) o;
        return cardNum == that.cardNum &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, cardNum);
    }

    public Boolean compareValue(Object o) {
        CardTypeInfo lastOutCard = (CardTypeInfo) o;
        if(this.equals(lastOutCard)){
            if(this.rating > lastOutCard.rating){
                return true;
            }else{
                return false;
            }
        }else if(CardTypeEnum.王炸.equals(this.type)){
            return true;
        }else if(!CardTypeEnum.炸弹.equals(lastOutCard.type()) && CardTypeEnum.炸弹.equals(this.type)){
            return true;
        }
        //类型不一样
        return null;
    }

    @Override
    public String toString() {
        return "CardTypeInfo{" +
                "type=" + type +
                ", cardNum=" + cardNum +
                ", rating=" + rating +
                ", cardKey='" + cardKey + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        CardTypeInfo other = (CardTypeInfo) o;
        if(this.rating < other.rating){
            return 1;
        }
        return -1;
    }
}
