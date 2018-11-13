package Service;

import Model.Account;
import Model.Transaction;
import Utils.DateCompute;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 调度系统：
 * 1. 计算并排序入账日、账单日等,并在时间顺序每日进行批次处理
 * 2. 加载每日参数，决定在什么情况下使用哪套参数
 * Created by Victor on 2018/11/10.
 */
public class Dispatcher {
    Account account;
    DayProcess DP;
    TransProcess TP;
    Transaction[] transList;
    /**
     * K： Account的延滞状态
     * V： 该延滞状态下使用的strikeOrder
     */
    Map<Integer, Integer> strikeOrderDispatcher;
    /**
     * K: Account的延滞状态
     * V：该延滞状态下，各余额是否免FEE
     */
    Map<Integer,List<Boolean>> waiveDispacher;


    boolean isFirstCycleDay = true;
    int freeIntInterval = 18;   //免息期
    int graceDayInterval = 3;   //宽限期

    /**
     * 起始日期:
     * 起始日期为上一个账期的第一天
     *
     */

    Date startDate;
    Date endDate;               //结束日期

    /**
     * 分发方法，从startDate到endDate的每一天处理
     */
    public void dispatcher(){
        Date today = startDate;
        int transIndex = 0;         //标识执行到第几条交易
        while(!today.equals(endDate)){
            while (transIndex < transList.length &&
                    DateCompute.getIntervalDays(transList[transIndex].getRecordDate(), today) >= 0){
                if(transList[transIndex].getRecordDate().equals(today)){
                    TP.transRoute(transList, transIndex, isFirstCycleDay);
                }
                transIndex += 1;
            }

            if(DateCompute.getDayOfMonth(today)==account.getCycleDay()){
                DP.cycleDayProcess(isFirstCycleDay);
                if(isFirstCycleDay) isFirstCycleDay = false;    //首个账单日应该不计算利息
            }
            if(DateCompute.getDayOfMonth(today)==account.getLastPaymentDay()){
                DP.lastPaymentDayProcess();
            }
            if(DateCompute.getDayOfMonth(today)==account.getGraceDay()){
                DP.graceDayProcess();
            }
            today = DateCompute.addDate(today,1);
        }
    }


    /**
     * Todo: 计算账户的最后还款日和宽限日
     */
    public void setAccountDay(){

    }



}
