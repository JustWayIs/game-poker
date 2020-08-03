package com.yude.game.doudizhu.domain.history;

import com.yude.game.doudizhu.domain.card.Card;
import com.yude.game.poker.common.constant.Status;

/**
 * @Author: HH
 * @Date: 2020/7/6 21:11
 * @Version: 1.0
 * @Declare:
 */
public interface Step {
     Status stepType();

     int getPosId();

     Card getCard();
}
