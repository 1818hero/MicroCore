package Service;

import Model.*;
import Utils.DateCompute;

import java.util.*;

/**
 * 交易处理
 * Created by Victor on 2018/10/29.
 */
public class TransProcess {
    Account account;

    /**
     * 冲账顺序strikeOrderList:每两个数从Account中定位一个余额栏位,可支持多个不同的strikeOrder参数片
     */
    List<List<Integer>> strikeOrderList;

    /**
     * 还款交易处理
     * @param repay 还款交易
     */
    private void repayment (Transaction repay, int strikeOrderIndex){
        double amount = repay.getAmount();
        List<Integer> strikeOrder = strikeOrderList.get(strikeOrderIndex);
        for(int i=0; i<strikeOrder.size(); i+=2){
            //定位某个BP下的某个栏位,不考虑get不到的情况
            List<BalanceList> BP_field = account.getBP().get(strikeOrder.get(i)).getBalance().get(strikeOrder.get(i+1));
 //            //将利率余额按从大到小排序,这一步应该是在新增利率余额的时候做
//            Collections.sort(BP_field, new Comparator<BalanceList>() {
//                @Override
//                public int compare(BalanceList o1, BalanceList o2) {
//                    if (o2.getRate() > o1.getRate())    return 1;
//                    else if(o1.getRate() > o2.getRate())    return -1;
//                    return 0;
//                }
//            });
            amount = strikeAndAccr(BP_field, amount, repay);    //冲抵了一个栏位后剩余的金额
            if (amount==0)  break;
        }
        /**
         * 若冲抵了所有余额还有剩余，则存入溢缴款
         */
        if (amount > 0)   account.setOverflow(account.getOverflow() + amount);
    }


    /**
     * 贷方交易入账处理
     * @param credit
     */
    private void creditTrans (Transaction credit, int strikeOrderIndex){
        double amount = credit.getAmount();
        Date transDate = credit.getTransDate();
        Date recordDate = credit.getRecordDate();
        List<Integer> strikeOrder = strikeOrderList.get(strikeOrderIndex);
        int original = DateCompute.judgeCycle(account.getCycleDay(),transDate,recordDate);
        int targetField;
        boolean traceback = true;
        if(original==0){         //交易日在当期
            targetField = 1;    //冲抵CTD
        }
        else{                   //交易日在往期
            targetField = 0;    //冲抵BNP
        }
        //从冲账顺序表中查找冲抵栏位
        List<BalanceList> BP_field = account.getBP().get(credit.getTC().getBP()).getBalance().get(targetField);
        amount = strikeAndAccr(BP_field, amount, credit);
        int index = 0;
        while(amount > 0 && index < strikeOrder.size()-1){
            BP_field = account.getBP().get(strikeOrder.get(index)).getBalance().get(strikeOrder.get(index+1));
            amount = strikeAndAccr(BP_field, amount, credit);    //冲抵了一个栏位后剩余的金额
        }
        /**
         * 若冲抵了所有余额还有剩余，则存入溢缴款
         */
        if (amount > 0)   account.setOverflow(account.getOverflow() + amount);
        // Todo 贷方交易的回算怎么办

    }

    /**
     * 借方交易入账处理
     * @param debit 借方交易
     */
    private void debitTrans(Transaction debit){
        double amount = debit.getAmount();
        //处理溢缴款
        if(account.getOverflow() >= amount){
            account.setOverflow(account.getOverflow()-amount);
            return;
        }
        else{
            account.setOverflow(0);
            amount -= account.getOverflow();
        }
        TransCode TC = debit.getTC();
        BalanceProgram targetBP = account.getBP().get(TC.getBP());
        List<BalanceList> field = targetBP.getBalance().get(TC.getField());
        //如果该栏位没有BalanceList，则以当前利率创建一条
        if(field.size()==0){
            field.add(new BalanceList(targetBP,account.getAccRate()));
        }
        int index = findRate(field);    //
        if(index < 0){
            field.add(0, new BalanceList(targetBP, account.getAccRate()));
            index = 0;
        }
        else if (account.getAccRate() < field.get(index).getRate()){
            field.add(index, new BalanceList(targetBP, account.getAccRate()));
        }
        Date startDate;
        if (TC.isTraceback())    startDate = debit.getTransDate();
        else    startDate = debit.getRecordDate();
        field.get(index).getBL().addLast(new BalanceNode(field.get(index),amount,startDate,debit.getRecordDate(),TC.isFreeInt(),debit.getSummary()));

    }



    /**
     * 根据TC决定每个交易由哪个方法处理
     * @param trans
     */
    public void transRoute(Transaction[] trans, int index, boolean isFirstCycleDay){
        Transaction t = trans[index];
        TransCode TC = t.getTC();
        if(TC.equals(TransCode.TC2000))  repayment(t, 0);    //Todo 暂未区分90天以内和90天以上
        else if((TC.equals(TransCode.TC3000) && !isFirstCycleDay) //非首月才读取取现交易
                || TC.equals(TransCode.TC4000)  //Todo 利用反射批量处理400x这样的TC
                || TC.equals(TransCode.TC4001)
                || TC.equals(TransCode.TC4002)
                || TC.equals(TransCode.TC4003)){
            debitTrans(t);
        }
        else{
            creditTrans(t,0);
        }
    }

    /**
     * 用于查找某个栏位的利率余额的工具类 （等效Collections.binarySearch()）
     * @param field
     * @return accRate在利率余额列表中的的index或比accRate大的最近的index，若accRate比最大利率余额的利率的还大，则返回-1
     *
     */
    private int findRate(List<BalanceList> field){
        double accRate = account.getAccRate();
        int left = 0;
        int right = field.size()-1;
        while(left<right){
            int mid = (left+right)/2;
            if (field.get(mid).getRate()==accRate)  return mid;
            else if(field.get(mid).getRate()>accRate) {
                left = mid + 1;
            }
            else right = mid -1;
        }
        if(field.get(left).getRate()>=accRate)  return left;
        else return left-1;
    }

    /**
     * 冲抵某个余额栏位，包含冲抵各个node的计息、回算逻辑
     * @param BP_field  余额栏位
     * @param amount    还款或贷方交易的剩余金额
     * @param tr        还款或贷方交易
     * @return  剩余的还款或贷方金额
     */
    private double strikeAndAccr(List<BalanceList> BP_field, double amount, Transaction tr){
        for(BalanceList BL : BP_field){
            ListIterator it = BL.getBL().listIterator();
            while(it.hasNext()){
                BalanceNode node = (BalanceNode) it.next();
                if (!node.isExist())    continue;              //如果该BalanceList已被冲掉，那么则冲抵下一个余额栏位
                if (amount > 0) node.setExist(false);          //若该还款还有余额，则该node死亡
                node.setEndDate(tr.getRecordDate());
                if(!node.getBL().getBP().isWaive()) {          //判断waive标识
                    //计算利息
                    node.setIntrests(node.getAmount() * BL.getRate()
                            * (DateCompute.getIntervalDays(node.getStartDate(), node.getEndDate())));
                    //还款交易回算逻辑：Min(交易金额，node余额)*利率*（入账日-交易日）
                    //Todo: 将回算改为新增一个Node，并且增加该BalanceList中的ACCR用于精确回算值
                    //两周期前的交易不回算
                    if(DateCompute.judgeCycle(account.getCycleDay(), tr.getTransDate(), tr.getRecordDate()) < 2) {
                        //还款交易的回算按实际冲抵余额进行回算
                        if(tr.getTC().equals(TransCode.TC2000)){
                            node.setTracebackAmount(-Math.min(node.getAmount(), amount) * BL.getRate()
                                    * (DateCompute.getIntervalDays(tr.getTransDate(), node.getEndDate())));
                        }
                    }
                }
                if (amount >= node.getAmount()){
                    /**
                     * 若还款金额足以冲抵该node，则冲抵完该node后剩余amount进一步冲抵下一个node
                     */
                    amount -= node.getAmount();
                }
                else{
                    /**
                     * 若还款金额不足以冲抵该node，则在node后插入一个新的node用于记录还款后的金额
                     */

                    BalanceNode leftNode = new BalanceNode(BL,node.getAmount()-amount,
                            tr.getRecordDate(),
                            tr.getRecordDate(),
                            node.isFreeInt(),
                            node.getSummary());
                    it.add(leftNode);
                    return 0.0;
                    //it.previous();  //这样才能遍历到新增的节点
                }
            }
        }
        return amount;
    }

    public TransProcess(Account account, List<List<Integer>> strikeOrderList) {
        this.account = account;
        this.strikeOrderList = strikeOrderList;
    }
}
