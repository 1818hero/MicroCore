package Service;

import Model.Account;
import Model.BalanceList;
import Model.BalanceNode;
import Model.BalanceProgram;
import Utils.DateCompute;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Victor on 2018/11/2.
 */
public class DayProcess {

    Account account;
    IOService out;
    Date curCycle;

    /**
     * 账单日逻辑
     * 若为首期账单日，则需读取利息交易，并将该值累加到RTL1的FEE BNP上。首期账单日不出帐。
     * 首个账期内，所有不免息交易Node记录的Interest值在账单日都清零，并且起息日从账单日后一天开始计。
     * 一、BP各栏位处理
     * 1. 入账日在两周期前的Node会被清理
     * 2. Node结转处理：对于Exist为true的Node，则止息日为账单日，Node的金额都会计入下期余额，然后将isExist置为false。
     * 3. 出利息
     * 3.1 免息标识处理：若FreeInt为true，则什么都不做。
     * 3.2 首月/WAIVE处理：若FreeInt为false，但为首月/BP的WAIVE为TRUE，则将该Node移除，该Node流程结束。
     * 3.3 出利息：若FreeInt为false且不为首月，则输出该Node的计息信息，同时将该Node的Interests栏位值累计到已出利息值，然后将该Node移除。
     *
     * 遍历结束BalaceList后，新建“上期余额”和利息的BalanceNode
     *
     * 二、最低额计算
     *
     * 说明：Exist标识和Node存在的关系
     * - Exist为true表示，该Node的余额未被冲抵。
     * - Node存在表示，该余额产生的利息还未出账。
     *
     *
     */


    public void cycleDayProcess(boolean isFirstCycleDay, Date today){
        double IBNP = 0.0;                      //本月总出账利息
        double BNP = 0.0;                       //本月总出帐本金
        for (BalanceProgram BP : account.getBP()){
            for(List<BalanceList> field : BP.getBalance()){
                BalanceList BL = field.get(0);  //Todo 利率余额写死
                BL.setBNP(BL.getBNP()+BL.getCTD());
                BNP += BL.getBNP();
                double lastBalance = 0.0;       //记录进入下一账期的余额
                double ints = 0.0;              //记录该BP的出账利息
                ListIterator<BalanceNode> li = BL.getBL().listIterator();
                while(li.hasNext()){
                    BalanceNode curNode = li.next();
                    int period = DateCompute.judgeCycle(account.getCycleDay(), curNode.getRecordDate(), today);
                    /**
                     * 1. 清理两周期前的交易
                     */
                    if(period >= 2){
                        li.remove();
                        continue;
                    }
                    /**
                     * 2. 结息并统计进入下个账期的余额
                     */
                    if (curNode.isExist()) {
                        lastBalance += curNode.getAmount();
                        curNode.setEndDate(today);
                        double intrests = curNode.getAmount() * BL.getRate()
                                * (DateCompute.getIntervalDays(curNode.getStartDate(), curNode.getEndDate())+1);
                        curNode.setIntrests(intrests);

                        curNode.setExist(false);
                    }

                    /**
                     * 3. 出利息
                     */
                    if(!curNode.isFreeInt()){
                        if(!(isFirstCycleDay || BL.getBP().isWaive())) {
                            //输出该Node的计息信息
                            System.out.println(curNode.getAmount()+"  "+curNode.getStartDate()+"  "+curNode.getEndDate()
                            +"  "+curNode.getIntrests()+"  "+curNode.getSummary());
                            ints += curNode.getIntrests();
                        }
                        li.remove();
                        continue;
                    }
                }

                // 新建上期余额节点
                Date tomorrow = DateCompute.addDate(today, 1);
                if(lastBalance>0) {
                    BL.getBL().addLast(new BalanceNode(BL,
                            lastBalance,
                            tomorrow,
                            tomorrow,
                            tomorrow,
                            false,
                            "上期余额(" + BL.getBP().getProductAttr() + ")",
                            1));
                }
                //新建利息余额节点，并将该栏位利息累加到IBNP
                if(ints > 0 && !isFirstCycleDay) {
                    IBNP += ints;
                    BalanceList intBL = BL.getBP().getBalance().get(1).get(0);  //Todo 利率余额写死
                    intBL.getBL().addLast(new BalanceNode(intBL,
                            ints,
                            tomorrow,
                            tomorrow,
                            tomorrow,
                            false,
                            "上期利息余额(" + BL.getBP().getProductAttr() + ")",
                            1));
                }

                //Todo 统计最低还款额

            }
            if(!isFirstCycleDay){
                if(account.getAnswer().get(curCycle) < 0||(IBNP-account.getAnswer().get(curCycle) < 0.03 && IBNP-account.getAnswer().get(curCycle)>0)){
                    //Todo 输出IBNP
                    account.setLateDayDueAmount(IBNP + BNP);
                    System.out.println("本期利息是："+IBNP);

                }
                else{
                    //Todo 打印错误信息

                }
            }

        }
    }
    /**
     * TODO: 最后还款日逻辑
     */
    public void lastPaymentDayProcess(){

    }
    /**
     * 宽限日逻辑
     * 1. 判断lateDayDueAmt是否小于10元
     * 若是，则清除每个栏位freeInt为true且Billout为true的Node；
     * 若否，则仅将所有freeInt为true的Node置为false；
     *
     */
    public void graceDayProcess(){
        for (BalanceProgram BP : account.getBP()) { // Todo 每次都要写循环，可尝试使用AOP简化
            for (List<BalanceList> field : BP.getBalance()) {
                BalanceList BL = field.get(0);      // Todo 利率余额写死
                ListIterator<BalanceNode> li = BL.getBL().listIterator();
                int count= 0;
                while (li.hasNext() && count < BL.getPointer()) {
                    BalanceNode curNode = li.next();
                    if(account.getLateDayDueAmount() <= 10) {
                        if (curNode.isFreeInt()){
                            li.remove();
                        }
                    }
                    else{
                        curNode.setFreeInt(false);
                    }
                    count += 1;
                }
            }
        }

    }
}
