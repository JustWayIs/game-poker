package com.yude.game.doudizhu.domain.card;
import com.yude.game.common.exception.BizException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/6/17 18:17
 * @Version: 1.0
 * @Declare: 斗地主所需要的牌
 */
public class PokerProp {
    public static final List<Integer> CARDS = new ArrayList();

    static {
        //直接用数组下标作为牌的代表值与客户端对接
        CardEunm[] values = CardEunm.values();
        for (int i = 0; i < values.length ; ++i) {
            //这样写只是为了表明两者的关联
            CARDS.add(values[i].ordinal());
        }

    }


    /**
     *
     * @param cardIndex 牌值下标
     * @return
     */
    public static int getCardScore(int cardIndex) {
        return CardEunm.getCardByIndex(cardIndex).getScore();
    }

    public static CardEunm getCardEnum(int cardIndex){
        return CardEunm.getCardByIndex(cardIndex);
    }



    /*public static boolean isJoker(int card) {
        return getRealCardColor(card) > PokerProp.COLOR_CRITICAL_VALUE;
    }*/


    public static List<CardEunm> convertCardForNum(Integer[] cardIndexs) {
       // Object[] cardEnumArray = Arrays.stream(cards).mapToObj(card -> CardEunm.getCard(card)).toArray();
        List<CardEunm> cardList = new ArrayList<>();
        for(Integer cardIndex : cardIndexs){
            cardList.add(CardEunm.getCardByIndex(cardIndex));
        }
        return cardList;
    }

    public static List<CardEunm> convertCardForNum(List<Integer> cardIndexs){
        List<CardEunm> cardList = new ArrayList<>();
        for(Integer cardIndex : cardIndexs){
            cardList.add(CardEunm.getCardByIndex(cardIndex));
        }
        return cardList;
    }

    public static Integer[] getCardIndexsByCard(CardEunm... cardEunms){
        List<Integer> cardNums = new ArrayList<>();
        for (CardEunm card : CardEunm.values()) {
            for (CardEunm cardParm : cardEunms) {
                if (card.equals(cardParm)) {
                    cardNums.add(card.ordinal());
                }
            }
        }
        //int[] cardArray = cardNums.stream().mapToInt(card -> card).toArray();
        Integer[] cardArray = new Integer[cardEunms.length];
        return cardNums.toArray(cardArray);
    }

    public static int[] getCardValueByCard2(CardEunm[] cardEunms){
        int[] cardArray2 = Arrays.stream(cardEunms).flatMap((cardEunmParm) ->
                Arrays.stream(CardEunm.values()).filter(cardEunm -> cardEunm.equals(cardEunmParm))
        ).mapToInt(matchCardEnum -> matchCardEnum.ordinal() + 1).toArray();
        return cardArray2;
    }


    public enum CardEunm {
        /**
         *
         */
        方块3(1),
        梅花3(1),
        红桃3(1),
        黑桃3(1),

        方块4(2),
        梅花4(2),
        红桃4(2),
        黑桃4(2),

        方块5(3),
        梅花5(3),
        红桃5(3),
        黑桃5(3),

        方块6(4),
        梅花6(4),
        红桃6(4),
        黑桃6(4),

        方块7(5),
        梅花7(5),
        红桃7(5),
        黑桃7(5),

        方块8(6),
        梅花8(6),
        红桃8(6),
        黑桃8(6),

        方块9(7),
        梅花9(7),
        红桃9(7),
        黑桃9(7),

        方块10(8),
        梅花10(8),
        红桃10(8),
        黑桃10(8),

        方块J(9),
        梅花J(9),
        红桃J(9),
        黑桃J(9),

        方块Q(10),
        梅花Q(10),
        红桃Q(10),
        黑桃Q(10),

        方块K(11),
        梅花K(11),
        红桃K(11),
        黑桃K(11),

        方块A(12),
        梅花A(12),
        红桃A(12),
        黑桃A(12),

        方块2(14),
        梅花2(14),
        红桃2(14),
        黑桃2(14),

        小王(16),
        大王(18);

        /**
         * 用来判断牌是否连续，和单牌比大小。
         * 故意把2，小王，大王 的分值 设置为比前一个牌的分 多2。使其不能连续
         */
        private int score;

        CardEunm(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }


        private static CardEunm getCardByIndex(int cardIndex){
            CardEunm[] values = CardEunm.values();
            CardEunm value;
            try{
                value = values[cardIndex];
            }catch (Exception e){
                throw new BizException("不存在的牌值下标："+cardIndex);
            }
            return value;
        }



        public static void main(String[] args) {
           // CardEunm card = getCard(14);
            //System.out.println(card);
        }

    }

    public static void main(String[] args) {
        /*boolean joker = isJoker(99);
        System.out.println(joker);*/

       /* int cardScore = getCardScore(1);
        System.out.println(cardScore);*/

        CardEunm[] siDaiEr = new PokerProp.CardEunm[]{PokerProp.CardEunm.方块3, PokerProp.CardEunm.黑桃3, PokerProp.CardEunm.红桃3, PokerProp.CardEunm.梅花3, PokerProp.CardEunm.红桃2, PokerProp.CardEunm.梅花2, PokerProp.CardEunm.黑桃6, PokerProp.CardEunm.梅花6};
        long beforeTieme = System.currentTimeMillis();
        getCardIndexsByCard(siDaiEr);
        System.out.println("传统遍历：耗时"+(System.currentTimeMillis()-beforeTieme)+" 毫秒");
        beforeTieme = System.currentTimeMillis();
        //getCardValueByCard2(siDaiEr);
        System.out.println("lambda表达式：耗时"+(System.currentTimeMillis()-beforeTieme)+" 毫秒");
    }
}

