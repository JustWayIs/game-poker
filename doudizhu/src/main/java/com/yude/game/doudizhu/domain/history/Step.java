package com.yude.game.doudizhu.domain.history;

import com.yude.game.common.constant.Status;
import com.yude.game.doudizhu.domain.card.Card;


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
