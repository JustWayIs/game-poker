package com.yude.game.doudizhu.util;

import com.yude.game.doudizhu.domain.card.Card;
import com.yude.game.doudizhu.domain.card.CardTypeEnum;
import com.yude.game.doudizhu.domain.card.CardTypeInfo;
import com.yude.game.doudizhu.application.response.dto.CardDTO;


import com.yude.game.doudizhu.domain.card.PokerProp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.*;

/**
 * @Author: HH
 * @Date: 2020/7/24 18:16
 * @Version: 1.0
 * @Declare:
 */
public class DdzTable {

    private static final Logger log = LoggerFactory.getLogger(DdzTable.class);

    /**
     * 数据字典：数据不可以修改
     * key:牌的数量的数组(15位：从3-大王)转换而成 -> 100000000000000
     * value: 牌的类型信息
     * <p>
     * 稳定后改为用HashMap,如果要保证提示牌的有序性
     */
    private static final Map<String, CardTypeInfo> cardRatingMap = new HashMap<>(2 << 15);
    /**
     * 重写了CardTypeInfo的equals方法和hashcode方法，只比较 类型和数量
     */
    private static final Map<CardTypeInfo, List<String>> ratingCardMap = new HashMap<>(2 << 8);

    public static final List<CardDTO> bombList = new ArrayList<>();

    public static void init() throws IOException {
        OrderProperties properties = PropertiesLoaderUtils.loadAllProperties("config/ddz-table.properties");
        Map<CardTypeInfo, Set<String>> temmpRatingCardMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entries : properties.entrySet()) {
            String key = entries.getKey();
            String value = entries.getValue();
            initTableMap(temmpRatingCardMap, key, value);
        }

        for(Map.Entry<CardTypeInfo,List<String>> entry : ratingCardMap.entrySet()){
            CardTypeInfo cardTypeInfo = entry.getKey();
            if (CardTypeEnum.炸弹.equals(cardTypeInfo.type())) {
                List<String> value = entry.getValue();
                for(String cardKey : value){
                    List<Integer> bomb = cardKeyConvertCard(cardKey);
                    CardDTO cardDTO = new CardDTO(bomb, cardTypeInfo.getType());
                    bombList.add(cardDTO);
                }
                break;
            }
        }

        //log.debug("斗地主表数据加载完成： cardRatingMap={}  \n  ratingCardMap={}", cardRatingMap, ratingCardMap);
    }

    private static Map<CardTypeInfo, Set<String>> initTableMap(Map<CardTypeInfo, Set<String>> temmpRatingCardMap, String key, String value) {
        String[] split = value.split("-");
        int type = Integer.parseInt(split[0]);
        CardTypeEnum cardTypeEnumByType = CardTypeEnum.getCardTypeEnumByType(type);
        CardTypeInfo cardTypeInfo = new CardTypeInfo(cardTypeEnumByType, Integer.parseInt(split[1]), Integer.parseInt(split[2]), key);
        cardRatingMap.put(key, cardTypeInfo);
        ratingCardMap.compute(cardTypeInfo, (mapKey, oldvalue) -> {
            if (oldvalue == null) {
                oldvalue = new ArrayList<>();
            }
            oldvalue.add(key);
            return oldvalue;
        });

        return temmpRatingCardMap;
    }

    /*public static CardTypeInfo judgeCardType(Integer[] cards) {
        String key = cardConvertCardKey(cards);
        log.debug("cards={} key = {}", cards, key);
        CardTypeInfo cardTypeInfo = cardRatingMap.get(key);
        return cardTypeInfo;
    }*/

    public static CardTypeInfo judgeCardType(List<Integer> cards) {
        String key = cardConvertCardKey(cards);
        log.debug("cards={} key = {}", cards, key);
        CardTypeInfo cardTypeInfo = cardRatingMap.get(key);
        return cardTypeInfo;
    }

    public static boolean compareCard(Card outCard, Card beforeCard) {
        CardTypeInfo beforeCardTypeInfo = beforeCard.getCardType();
        CardTypeInfo outCardTypeInfo = outCard.getCardType();
        return outCardTypeInfo.compareValue(beforeCardTypeInfo);
    }

    /*private static String cardConvertCardKey(Integer[] cardIndexs) {
        List<PokerProp.CardEunm> cardEunms = PokerProp.convertCardForNum(cardIndexs);
        StringBuilder stringBuilder = new StringBuilder();
        int[] cards = new int[15];
        for (PokerProp.CardEunm cardEunm : cardEunms) {
            if (cardEunm.equals(PokerProp.CardEunm.小王)) {
                cards[13] = 1;
            } else if (cardEunm.equals(PokerProp.CardEunm.大王)) {
                cards[14] = 1;
            } else {
                int value = cardEunm.ordinal() / 4;
                cards[value]++;
            }

        }
        for (int i = 0; i < cards.length; ++i) {
            stringBuilder.append(cards[i]);
        }
        return stringBuilder.toString();
    }*/

    public static int[] cardConvertCardNumArray(List<Integer> cardIndexs){
        List<PokerProp.CardEunm> cardEunms = PokerProp.convertCardForNum(cardIndexs);
        int[] cards = new int[15];
        for (PokerProp.CardEunm cardEunm : cardEunms) {
            if (cardEunm.equals(PokerProp.CardEunm.小王)) {
                cards[13] = 1;
            } else if (cardEunm.equals(PokerProp.CardEunm.大王)) {
                cards[14] = 1;
            } else {
                int value = cardEunm.ordinal() / 4;
                cards[value]++;
            }

        }
        return cards;
    }

    public static String cardConvertCardKey(List<Integer> cardIndexs) {
        StringBuilder stringBuilder = new StringBuilder();
        int[] cards = cardConvertCardNumArray(cardIndexs);

        for (int i = 0; i < cards.length; ++i) {
            stringBuilder.append(cards[i]);
        }
        return stringBuilder.toString();
    }

    public static List<CardDTO> getCardsGreaterThan(List<Integer> targetCardIndexs) {
        String targetCardKey = cardConvertCardKey(targetCardIndexs);
        List<CardDTO> cardsGreaterThan = getCardsGreaterThan(targetCardKey);
        return cardsGreaterThan;
    }

    public static List<CardDTO> getCardsGreaterThan(String targetCardKey) {
        CardTypeInfo targetCardInfo = cardRatingMap.get(targetCardKey);

        List<CardDTO> needCardList = new ArrayList<>();
        if (CardTypeEnum.王炸.equals(targetCardInfo.type())) {
            return needCardList;
        }

        /**
         * 因为重写了CardTypeInfo的hashCode与equals方法，所以只要牌型和牌的数量一致，就视为一个key.
         * 可以保持了可出牌组的顺序：从小到大 -> 相同牌型、炸弹、王炸
         */
        List<String> cardInfos = ratingCardMap.get(targetCardInfo);
        /**
         * 在排好序的情况下，不用遍历所有的值。得保证有序的可靠性
         */
        int i = cardInfos.indexOf(targetCardKey);
        List<String> subCardInfos = cardInfos.subList(i+1, cardInfos.size());
        for (String key : subCardInfos) {
            CardTypeInfo curCardInfo = cardRatingMap.get(key);
            if (curCardInfo.compareValue(targetCardInfo)) {
                List<Integer> cardIndexs = cardKeyConvertCard(key);
                CardDTO cardDTO = new CardDTO(cardIndexs,curCardInfo.getType());
                needCardList.add(cardDTO);
            }

        }

        if (!CardTypeEnum.炸弹.equals(targetCardInfo.type())) {
            needCardList.addAll(bombList);
        }

        List<Integer> cardIndexs = new ArrayList<>();
        cardIndexs.add(13);
        cardIndexs.add(14);
        CardDTO cardDTO = new CardDTO(cardIndexs, CardTypeEnum.王炸.getType());
        needCardList.add(cardDTO);


        return needCardList;
    }

    /**
     * 这里转换出来的是牌值的集合，而不是牌枚举的下标集合
     *
     * @param cardKey
     * @return
     */
    public static List<Integer> cardKeyConvertCard(String cardKey) {
        List<Integer> cardValueList = new ArrayList<>();
        char[] chars = cardKey.toCharArray();
        for (int j = 0; j < chars.length; ++j) {
            String numStr = String.valueOf(chars[j]);
            int num = Integer.parseInt(numStr);
            for (int i = 0; i < num; ++i) {
                cardValueList.add(j);
            }
        }
        return cardValueList;
    }

    public static List<Integer> cardIndexConvertcardValue(List<Integer> list) {
        List<Integer> cardValueList = new ArrayList<>();
        for (Integer cardIndex : list) {
            if (cardIndex == PokerProp.CardEunm.小王.ordinal()) {
                cardValueList.add(13);
            } else if (cardIndex == PokerProp.CardEunm.大王.ordinal()) {
                cardValueList.add(14);
            } else {
                int cardValue = cardIndex / 4;
                cardValueList.add(cardValue);
            }

        }
        return cardValueList;
    }

    public static void main(String[] args) throws IOException {
        init();
        String str = "000000000013000";
        List<CardDTO> cardsGreaterThan = getCardsGreaterThan(str);
        System.out.println(cardsGreaterThan);

        /* String s = "103000000000000";
        char[] chars = s.toCharArray();
        System.out.println(Arrays.toString(chars));
        //String str = String.valueOf(c);


        Map<CardTypeInfo, Set<String>> map = new HashMap<>();
        CardTypeInfo cardTypeInfo = new CardTypeInfo(CardTypeEnum.单牌, 1, 1);
        CardTypeInfo cardTypeInfo2 = new CardTypeInfo(CardTypeEnum.单牌, 1, 2);
        Set<String> set = new HashSet<>();
        set.add("100000000000000");
        set.add("010000000000000");
        map.put(cardTypeInfo, set);

        Set<String> strings = map.get(cardTypeInfo2);
        System.out.println(strings);

        System.out.println("info1: " + cardTypeInfo.hashCode());
        System.out.println("info2: " + cardTypeInfo2.hashCode());*/
    }

}
