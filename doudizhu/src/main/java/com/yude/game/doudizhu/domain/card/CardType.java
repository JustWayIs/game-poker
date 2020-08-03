package com.yude.game.doudizhu.domain.card;

import com.yude.game.poker.common.exception.BizException;

import com.yude.protocol.common.constant.StatusCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: HH
 * @Date: 2020/6/29 10:13
 * @Version: 1.0
 * @Declare:斗地主牌型判断 ： 应不应该全写在一个类里的
 */
public enum CardType implements JudgeCardType {
    /**
     * 不适用于多副牌的斗地主->虽然貌似并没有玩多副牌的斗地主
     */
    单牌 {
        public static final int cardNum = 1;


        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length == cardNum) {
                return this;
            }
            return null;
        }

    },
    顺子 {
        /**
         * 构成顺子的牌的最小数量
         */
        public static final int cardNum = 5;


        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length < cardNum) {
                return null;
            }
            /**
             * 确保cards里面没有card的值是0,在接收牌的Controller就应该校验
             * 如果牌组不包含2、小王、大王。并且牌值分一直是连续的说明是顺子
             */
            int beforeCard = -1;
            for (Integer card : cardIndexs) {
                if (beforeCard == -1) {
                    beforeCard = card;
                    continue;
                }

                if (!CardType.isSequential(beforeCard, card)) {
                    return null;
                }
                beforeCard = card;
            }
            return this;
        }


    },
    对子 {
        public static final int cardNum = 2;


        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length != cardNum) {
                return null;
            }
            int beforeCard = -1;
            for (Integer card : cardIndexs) {
                if (beforeCard == -1) {
                    beforeCard = card;
                    continue;
                }
                if (PokerProp.getCardScore(card) != PokerProp.getCardScore(beforeCard)) {
                    return null;
                }
            }
            return this;
        }


    },
    连对 {
        private static final int doubleCard = 2;
        public static final int cardNum = 6;


        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length < cardNum || cardIndexs.length % doubleCard != 0) {
                return null;
            }

            ////相当于跳两格
            for (int i = 0; i < cardIndexs.length; i = i + 2) {
                if (i + 2 >= cardIndexs.length) {
                    if (PokerProp.getCardScore(cardIndexs[i]) != PokerProp.getCardScore(cardIndexs[i + 1])) {
                        return null;
                    }
                    return this;
                }
                if (PokerProp.getCardScore(cardIndexs[i]) != PokerProp.getCardScore(cardIndexs[i + 1])) {
                    return null;
                }
                if (!CardType.isSequential(cardIndexs[i], cardIndexs[i + 2])) {
                    return null;
                }

            }

            return this;

        }


    },
    三张 {
        public static final int cardNum = 3;


        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length != cardNum) {
                return null;
            }
            int beforeCard = -1;
            for (Integer card : cardIndexs) {
                if (beforeCard == -1) {
                    beforeCard = card;
                    continue;
                }
                if (PokerProp.getCardScore(beforeCard) != PokerProp.getCardScore(card)) {
                    return null;
                }
                beforeCard = card;
            }
            return this;
        }


    },
    三带一 {
        public static final int cardNum = 4;


        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length != cardNum) {
                return null;
            }
            /**
             * 上面判断对子用这个方式似乎更丝滑
             *
             * 按牌值分成两组，如果数量差值为2,说明是3带1
             */
            Map<Integer, List<Integer>> cardNumMap = Arrays.stream(cardIndexs).collect(Collectors.groupingBy(card -> PokerProp.getCardScore(card)));
            if (cardNumMap.size() != 2) {
                return null;
            }
            int beforeCardSize = -1;
            for (Map.Entry<Integer, List<Integer>> entry : cardNumMap.entrySet()) {
                List<Integer> cardList = entry.getValue();
                int cardSize = cardList.size();
                if (beforeCardSize == -1) {
                    beforeCardSize = cardSize;
                    continue;
                }

                if (Math.abs(beforeCardSize - cardSize) != 2) {
                    return null;
                }
            }
            return this;
        }


    },
    三带一对 {
        public static final int cardNum = 5;


        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length != cardNum) {
                return null;
            }
            /**
             *
             * 按牌值分成两组，如果数量差值为1,说明是3带1
             */
            Map<Integer, List<Integer>> cardNumMap = Arrays.stream(cardIndexs).collect(Collectors.groupingBy(card -> PokerProp.getCardScore(card)));
            if (cardNumMap.size() != 2) {
                return null;
            }
            int beforeCardSize = -1;
            for (Map.Entry<Integer, List<Integer>> entry : cardNumMap.entrySet()) {
                List<Integer> cardList = entry.getValue();
                int cardSize = cardList.size();
                if (beforeCardSize == -1) {
                    beforeCardSize = cardSize;
                    continue;
                }

                if (Math.abs(beforeCardSize - cardSize) != 1) {
                    return null;
                }
            }
            return this;
        }


    },
    飞机不带 {
        /**
         * 六张以及以上都有可能是飞机：不能333444 带 1个单牌。
         *  要么带2个单牌，要么带2对。要么不带。
         *
         * 使用每个牌型的cardNum作为猜测执行的依据 ，要不然每次都算飞机太费时间了：而且还没找到判断是哪一种飞机 的依据
         * @param cards
         * @return
         */
        public static final int cardNum = 6;


        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length < cardNum || cardIndexs.length % 3 != 0) {
                return null;
            }
            /**
             * key:相同数量的牌 的数量值  value:数量相同的牌 的集合
             */
            Map<Integer, List<Integer>> cardNumAndCardValueMap = new HashMap<>();

            /**
             * 依赖于已经排序的情况
             */
            Integer beforeCard = cardIndexs[0];
            int num = 0;
            int i = 0;
            for (Integer card : cardIndexs) {
                i++;
                if (PokerProp.getCardScore(beforeCard) == PokerProp.getCardScore(card)) {
                    num++;

                    if (i == cardIndexs.length) {
                        cardNumAndCardValueMap.compute(num, (key, oldValue) -> {
                            if (oldValue == null) {
                                oldValue = new ArrayList<>();
                            }
                            oldValue.add(card);
                            return oldValue;
                        });
                    }
                    continue;
                }
                Integer finalBeforeCard = beforeCard;
                cardNumAndCardValueMap.compute(num, (key, oldValue) -> {
                    if (oldValue == null) {
                        oldValue = new ArrayList<>();
                    }
                    oldValue.add(finalBeforeCard);
                    return oldValue;
                });
                num = 1;
                beforeCard = card;
            }
            if (cardNumAndCardValueMap.size() > 1) {
                return null;
            }
            for (Map.Entry<Integer, List<Integer>> entry : cardNumAndCardValueMap.entrySet()) {
                int cardNum = entry.getKey();
                List<Integer> value = entry.getValue();
                if (cardNum != 3) {
                    return null;
                }

                int preCard = -1;
                for (Integer card : value) {
                    if (preCard == -1) {
                        preCard = card;
                        continue;
                    }

                    if (!CardType.isSequential(preCard, card)) {
                        return null;
                    }
                    preCard = card;
                }
            }


            return this;
        }


    },
    飞机带单牌 {
        private static final int cardNum = 8;
        public static final int cardGroupNum = 2;

        /**
         * 1.还是跟原来一样，按照 牌的数量 -> 相同数量的牌的集合  分组。
         * 2.如果分出来不是两组，就over
         * 3.如果两组中没有一组为3，那么也over.
         * 4.如果数量为3的那一组的 牌的集合 里牌的分值不是连续的，也over。
         * 5.如果key之比（数量之比） 是 1:3/3:1 的关系就是带单牌，如果是 2:3/3:2 的关系 并且数量为2的集合里cards[i]的分值要 等于 cards[i+1]的分值 然后 i = i +2 就是带对子
         * @param cardIndexs
         * @return
         */
        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length < cardNum || cardIndexs.length % 4 != 0) {
                return null;
            }

            Map<Integer, List<Integer>> cardNumAndCardValueMap = new HashMap<>();

            /**
             * 依赖于已经排序的情况
             */

            //相同分数，并且数量为3 的组 的牌的数量
            int threeCardGroup = 0;
            //另一组牌的总数量  可以是 1+1 也可以是 1+2
            int otherCardGroup = 0;

            int beforeCard = cardIndexs[0];
            int sameCardNum = 1;
            int cursorIndex = 1;
            for(int i = 0 ; i < cardIndexs.length ;){
                if(cursorIndex < cardIndexs.length){
                    int card = PokerProp.getCardScore(cardIndexs[cursorIndex]);
                    if(PokerProp.getCardScore(beforeCard) == card){
                        sameCardNum++;
                        cursorIndex++;
                        continue;
                    }
                    if(sameCardNum == 3){
                        threeCardGroup += sameCardNum;
                    }else{
                        otherCardGroup += sameCardNum;
                    }
                    putCard(beforeCard,sameCardNum,cardNumAndCardValueMap);
                    sameCardNum = 1;
                    i = cursorIndex;
                    beforeCard = cardIndexs[i];
                    cursorIndex++;
                    continue;
                }
                if(sameCardNum == 3){
                    threeCardGroup += sameCardNum;
                }else{
                    otherCardGroup += sameCardNum;
                }
                putCard(beforeCard,sameCardNum,cardNumAndCardValueMap);
                break;
            }

            if(cardNumAndCardValueMap.size() < 2){
                return null;
            }

            //3:1 原则
            if (!ratio(threeCardGroup, otherCardGroup)) {
                return null;
            }

            for(Map.Entry<Integer,List<Integer>> entry : cardNumAndCardValueMap.entrySet()){
                int cardNum = entry.getKey();
                if(cardNum == 3){
                    List<Integer> cardList = entry.getValue();

                    int preCard = -1;
                    for(Integer cardIndex : cardList){
                        if (preCard == -1) {
                            preCard = cardIndex;
                            continue;
                        }

                        if (!CardType.isSequential(preCard, cardIndex)) {
                            return null;
                        }
                        preCard = cardIndex;
                    }
                    return this;
                }

            }

            return null;
        }

        private void putCard(Integer card, int num, Map<Integer, List<Integer>> cardNumAndCardValueMap) {
            cardNumAndCardValueMap.compute(num, (key, oldValue) -> {
                if (oldValue == null) {
                    oldValue = new ArrayList<>();
                }
                oldValue.add(card);
                return oldValue;
            });
        }

        private boolean ratio(double a, double b) {
            final double x = 1L;
            final double y = 3L;
            if (a / b == x / y || a / b == y / x) {
                return true;
            }
            return false;
        }
    }, 飞机带对子 {
        public static final int cardNum = 10;
        public static final int cardGroupNum = 2;

        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length < cardNum || cardIndexs.length % 5 != 0) {
                return null;
            }
            /**
             * 逻辑几乎和飞机带单牌一样
             */
            Map<Integer, List<Integer>> cardNumAndCardValueMap = new HashMap<>();

            /**
             * 依赖于已经排序的情况
             */
            //相同分数，并且数量为3 的组 的牌的数量
            int threeCardGroup = 0;
            //另一组牌的总数量  只能是 2+2...+
            int otherCardGroup = 0;

            int beforeCard = cardIndexs[0];
            int sameCardNum = 1;
            int cursorIndex = 1;
            for(int i = 0 ; i < cardIndexs.length ;){
                if(cursorIndex < cardIndexs.length){
                    int card = PokerProp.getCardScore(cardIndexs[cursorIndex]);
                    if(PokerProp.getCardScore(beforeCard) == card){
                        sameCardNum++;
                        cursorIndex++;
                        continue;
                    }
                    if(sameCardNum == 3){
                        threeCardGroup += sameCardNum;
                    }else{
                        otherCardGroup += sameCardNum;
                    }
                    putCard(beforeCard,sameCardNum,cardNumAndCardValueMap);
                    sameCardNum = 1;
                    i = cursorIndex;
                    beforeCard = cardIndexs[i];
                    cursorIndex++;
                    continue;
                }
                if(sameCardNum == 3){
                    threeCardGroup += sameCardNum;
                }else{
                    otherCardGroup += sameCardNum;
                }
                putCard(beforeCard,sameCardNum,cardNumAndCardValueMap);
                break;
            }

            if(cardNumAndCardValueMap.size() != 2){
                return null;
            }

            //3:2 原则
            if (!ratio(threeCardGroup, otherCardGroup)) {
                return null;
            }

            for(Map.Entry<Integer,List<Integer>> entry : cardNumAndCardValueMap.entrySet()){
                int cardNum = entry.getKey();
                if(cardNum == 3){
                    List<Integer> cardList = entry.getValue();

                    int preCard = -1;
                    for(Integer cardIndex : cardList){
                        if (preCard == -1) {
                            preCard = cardIndex;
                            continue;
                        }

                        if (!CardType.isSequential(preCard, cardIndex)) {
                            return null;
                        }
                        preCard = cardIndex;
                    }
                    return this;
                }

            }

            return null;
        }

        private void putCard(Integer card, int num, Map<Integer, List<Integer>> cardNumAndCardValueMap) {
            cardNumAndCardValueMap.compute(num, (key, oldValue) -> {
                if (oldValue == null) {
                    oldValue = new ArrayList<>();
                }
                oldValue.add(card);
                return oldValue;
            });
        }

        private boolean ratio(double a, double b) {
            final double x = 2L;
            final double y = 3L;
            if (a / b == x / y || a / b == y / x) {
                return true;
            }
            return false;
        }
    },
    四带二 {
        public static final int cardNum = 6;


        /**
         *
         * @param cardIndexs
         * @return
         */
        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length != cardNum) {
                return null;
            }

            int num = 0;
            int beforeCardScore = PokerProp.getCardScore(cardIndexs[0]);
           for(Integer cardIndex : cardIndexs){
               int cardScore = PokerProp.getCardScore(cardIndex);
               if(beforeCardScore == cardScore){
                   num++;
               }else{
                   num = 1;
                   beforeCardScore = cardScore;
               }

               if(num == 4){
                   return this;
               }
           }
            return null;
        }


    },
    四带两对 {

        public static final int cardNum = 8; //四带两对

        /**
         *   不仅需要添加四带一对，在服务器还必须与四带二区分，要不然判断大小的时候没办法判断:
         *   逻辑其实与四带二一致
         */
        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length != cardNum) {
                return null;
            }

            Map<Integer, Long> cardNumMap = Arrays.stream(cardIndexs).collect(Collectors.groupingBy(card -> PokerProp.getCardScore(card), Collectors.counting()));

            Map<Long, List<Integer>> cardNumAndCardsMap = new HashMap<>();

            for (Map.Entry<Integer, Long> entry : cardNumMap.entrySet()) {
                Long cardNum = entry.getValue();
                Integer card = entry.getKey();
                cardNumAndCardsMap.compute(cardNum, (key, oldValue) -> {
                    if (oldValue == null) {
                        oldValue = new ArrayList<>();
                    }

                    oldValue.add(card);
                    return oldValue;
                });
            }
            if (cardNumAndCardsMap.size() != 2) {
                return null;
            }

            long beforeCardSize = -1;
            long beforeCardNum = -1;
            for (Map.Entry<Long, List<Integer>> entry : cardNumAndCardsMap.entrySet()) {
                Integer cardSize = entry.getValue().size();
                Long cardNum = entry.getKey();
                if (beforeCardSize == -1) {
                    beforeCardSize = cardSize;
                    beforeCardNum = cardNum;
                    continue;
                }
                if (Math.abs(beforeCardSize - cardSize) != 1) {
                    return null;
                }
            }
            return this;

        }
    },
    炸弹 {
        //连环炸弹要怎么算
        public static final int cardNum = 4;
        private static final int excludeCard = 2;

        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length % cardNum != 0) {
                return null;
            }
            Map<Integer, Long> cardNumMap = Arrays.stream(cardIndexs).collect(Collectors.groupingBy(card -> PokerProp.getCardScore(card), Collectors.counting()));

            for (Map.Entry<Integer, Long> entry : cardNumMap.entrySet()) {
                Long cardSize = entry.getValue();
                Integer card = entry.getKey();

                if (cardSize != cardNum) {
                    return null;
                }

                /**
                 * 连环炸弹不能有4个2
                 * 如果有连环炸弹，理想状态中,这四个2会因为牌的分值放在Map最后面： 但是-> Map是无序的....
                 * 所以判断是否连续要不能通过遍历Map来判断
                 */
                if (cardNumMap.size() > 1 && PokerProp.getCardScore(card) == excludeCard) {
                    return null;
                }


            }

            /*List<Integer> distinctCardList = Stream.iterate(0, i -> i + 1).limit(cards.length).map(index -> PokerProp.getRealCardValue(cards[index])).distinct().collect(Collectors.toList());*/
            List<Integer> distinctCardList = Arrays.stream(cardIndexs).map(card ->PokerProp.getCardScore(card)).distinct().collect(Collectors.toList());
            int beforeCard = -1;
            for (Integer card : distinctCardList) {
                if (beforeCard == -1) {
                    beforeCard = card;
                    continue;
                }
                if (!CardType.isSequential(beforeCard, card)) {
                    return null;
                }
                beforeCard = card;
            }

            return this;
        }

    },
    王炸 {
        public static final int cardNum = 2;


        @Override
        public CardType judge(Integer[] cardIndexs) {
            if (cardIndexs.length != cardNum) {
                return null;
            }

            //正常情况下，斗地主一副牌里 不会有一对小王  和 一对大王
            List<Integer> filterCardList = Arrays.stream(cardIndexs).filter(card -> {
                PokerProp.CardEunm cardEunm = PokerProp.getCardEnum(card);
                if (cardEunm.equals(PokerProp.CardEunm.大王) || cardEunm.equals(PokerProp.CardEunm.小王)) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            if (filterCardList.size() != cardNum) {
                return null;
            }
            return this;
        }


    };

    private static final Logger log = LoggerFactory.getLogger(CardType.class);


    /**
     * H2: 注意，由于改变了排序方式 改成了从大到小，所以这里的beforeCard在前面
     * 可以通过把2的分 比A 多2来避过每次都要判断2的问题
     * <p>
     * 判断两个值是否是连续的
     *
     * @param beforeCardIndex
     * @param cardIndex
     * @return
     */
    private static boolean isSequential(int beforeCardIndex, int cardIndex) {
        return PokerProp.getCardScore(beforeCardIndex) - PokerProp.getCardScore(cardIndex) == 1 ? true : false;
    }

    public static Comparator<Integer> cardComparator = (a, b) -> {
        /**
         * 判断牌型时，排序应该是按牌的分值来算，而不是牌值本身（牌的自然顺序）
         * H2: 注意，这里改变了排序方式 改成了从大到小
         */
        int before = PokerProp.getCardScore(a);
        int after = PokerProp.getCardScore(b);

        if (before < after) {
            return 1;
        } else {
            return -1;
        }
    };

    public static void sort(Integer[] cardIndexs) {
        Arrays.sort(cardIndexs, cardComparator);
    }

    public static void sort(List<Integer> cardIndexList) {
        Collections.sort(cardIndexList, cardComparator);
    }

    /**
     * 由于没法自定义的方式用Arrays对int[] 进行排序，所以用Integer[]  实际上牌值并没有null的需求
     *
     * @param cardIndexs：数组下标的数组 -> 由于客户端和服务器的计算方式不一样，所以用数组下标来标识牌值
     * @return
     */
    public static CardType judgeCardType(Integer[] cardIndexs) {
        //由于排序判断方式，已经采用和界面摆放一致的顺序：从牌分值大到小，所以这里不需要再复制了
        log.info("原牌型判断： cardIndexs={}",Arrays.toString(cardIndexs));
        //确保cards里面没有null
        //long beforeTime = System.currentTimeMillis();
        sort(cardIndexs);
        log.info("排序后牌型判断：cardIndexs={}  \n 转换后：{}", Arrays.toString(cardIndexs),PokerProp.convertCardForNum(cardIndexs));
        //log.info("排序耗时：{}  毫秒\n", (System.currentTimeMillis() - beforeTime));

        for (CardType cardType : CardType.values()) {
            //log.info("牌型：{}", cardType);
            //beforeTime = System.currentTimeMillis();
            CardType result = cardType.judge(cardIndexs);
            //log.info("耗时： {}  毫秒\n", (System.currentTimeMillis() - beforeTime));

            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * @param card
     * @param targetCard : 被比较的card
     * @return
     */
    /*public static boolean compareCard(Card card, Card targetCard) {
        CardType cardType = card.getCardType();
        Integer[] cardIndexs = card.getCards();

        if (cardType.equals(targetCard.getCardType())) {
            if(CardType.飞机带单牌.equals(cardType) || CardType.飞机带对子.equals(cardType) || CardType.三带一.equals(cardType) || CardType.三带一对.equals(cardType) || CardType.四带两对.equals(cardType) || CardType.四带二.equals(cardType)){
                Integer[] targetCardIndexs = targetCard.getCards();

                int cardMaxNum = 0;
                int maxNumCardScore = -1;

                int targetCardMaxNum = 0;
                int targetMaxNumCardScore = -1;
                for (int i = 0; i < cardIndexs.length; i++) {
                    int count = 0;
                    int targetCount = 0;

                    int cardScore = PokerProp.getCardScore(cardIndexs[i]);
                    int targetCardScore = PokerProp.getCardScore(targetCardIndexs[i]);
                    for (int j = 0; j < cardIndexs.length; j++) {
                        if (cardScore == PokerProp.getCardScore(cardIndexs[j])){
                            count++;
                        }

                        if(targetCardScore == PokerProp.getCardScore(targetCardIndexs[j])){
                            targetCount++;
                        }

                    }
                    if (count >= cardMaxNum){
                        cardMaxNum = count;
                        maxNumCardScore = cardScore;
                    }
                    if(targetCount >= targetCardMaxNum){
                        targetCardMaxNum = targetCount;
                        targetMaxNumCardScore = targetCardScore;
                    }

                }
                return maxNumCardScore > targetMaxNumCardScore;
            }


            int cardScore = PokerProp.getCardScore(cardIndexs[0]);
            int targetCardScore = PokerProp.getCardScore(targetCard.getCards()[0]);
            return cardScore > targetCardScore;
        }
        if (CardType.王炸.equals(cardType) || (CardType.炸弹.equals(cardType) && !CardType.炸弹.equals(targetCard.getCardType()))) {
            return true;
        }

        throw new BizException("非法的牌型比较", StatusCodeEnum.ILLEGAL_OUT);
    }*/

    /**
     * @param cardIndexs
     * @param targetCardIndexs : 被比较的card
     * @return
     */
    public static boolean compareCard(Integer[] cardIndexs, Integer[] targetCardIndexs) {
        CardType cardType = judgeCardType(cardIndexs);
        CardType targetCardType = judgeCardType(targetCardIndexs);
        if (cardType.equals(targetCardType)) {
            return PokerProp.getCardScore(cardIndexs[0]) > PokerProp.getCardScore(targetCardIndexs[0]);
        }
        if (CardType.王炸.equals(cardType) || CardType.炸弹.equals(cardType)) {
            return true;
        }

        throw new BizException("非法的牌型比较", StatusCodeEnum.ILLEGAL_OUT);
    }


    public static void main(String[] args) {

    }


}

interface JudgeCardType {
    /**
     * 实现上要注意： 顺子类型：要用 牌的分值来判断。  是否相同，要用牌的真实值来判断。
     * 应该传枚举的，测试的时候更加 直观，而且可以根据分值进行天然排序
     *
     * 因为斗地主的牌型实际上是和牌的花色无关的，传的数组，应该用牌的分值数组更合适，就不用在judge的实现里面做转换了
     * @param cardIndexs
     * @return
     */
    CardType judge(Integer[] cardIndexs);

}
