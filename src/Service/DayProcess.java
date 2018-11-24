package Service;

import Model.Account;
import Model.BalanceList;
import Model.BalanceProgram;

import java.util.List;

/**
 * Created by Victor on 2018/11/2.
 */
public class DayProcess {
    /**
     * TODO: 账单日逻辑
     * TODO: GraceDay逻辑
     *
     */
    Account account;

    /**
     * 账单日逻辑:
     * 1. 将每个Node的状态置为false
     * 2. 出账(根据@Param isFirstCycleDay 判断)
     */
    public void cycleDayProcess(boolean isFirstCycleDay){
        //首个账单日不计息
        //遍历各个栏位，实现栏位迁移
        for (BalanceProgram BP : account.getBP()){
            List<BalanceList> CTD =  BP.getBalance().get(1);
            //Todo 1. 每次入账是
            //Todo 2.根据BalanceNode的isFreeInt判断是否出息，若仍在免息期，则将isFreeInt置为false
        }
    }
    /**
     * TODO: 最后还款日逻辑
     */
    public void lastPaymentDayProcess(){

    }
    /**
     * 宽限日逻辑
     */
    public void graceDayProcess(){

    }
}
