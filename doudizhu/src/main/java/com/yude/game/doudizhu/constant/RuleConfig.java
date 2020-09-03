package com.yude.game.doudizhu.constant;

/**
 * @Author: HH
 * @Date: 2020/7/8 15:07
 * @Version: 1.0
 * @Declare:
 */
public class RuleConfig {
    /**
     * 这个值后期会是动态的，相当于房间底注
     */
    public static final int baseScoreFactor = 1;
    /**
     * 叫分的选择列表：保证顺序。第一个值一定是不叫分
     */
    public static final Integer[] callScoreOption = {0,1,2,3};

    /**
     * 不加倍/加倍  保证顺序。第一个值是 不加倍
     */
    public static final Integer[] redoubleOption = {1,2};

    /**
     * 剩余牌数警报
     */
    public static final int ALARM = 2;

    /**
     * 快速出牌/托管 所需要的玩家连续超时次数
     */
    public static final int SERIAL_TIMEOUT_OUNT = 2;

    /**
     * 当玩家没有牌可以大过上家时，重新定义该玩家的超时时间
     */
    public static final int OPERATION_TIME_CAN_NOT_WIN = 5;


    /**
     * 开局后叫分的延时时间：发牌动画的耗时
     */
    public static final int ANIMATION_CALL_SCORE_DELAYED = 2;

    /**
     * 决定地主归属的动画耗时
     */
    public static final float ANIMATION_LANDLORD_OWNERSHIP_DELAYED = 1.6f;


    public static boolean isExistsCallScoreOpion(int score){
        for(Integer option : callScoreOption){
            if(option == score){
                return true;
            }
        }
        return false;
    }

    public static Integer getMaxScore(){
       return callScoreOption[callScoreOption.length-1];
    }

    public static boolean isExistsRedoubleOption(int redoubleValue){
        for(Integer option : redoubleOption){
            if(option == redoubleValue){
                return true;
            }
        }
        return false;
    }
}
