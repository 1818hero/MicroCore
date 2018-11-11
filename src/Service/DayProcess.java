package Service;

import Model.Account;

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
     * 1. 栏位汇总迁移
     * 2. 出账(根据@Param isFirstCycleDay 判断)
     */
    public void cycleDayProcess(boolean isFirstCycleDay){

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
