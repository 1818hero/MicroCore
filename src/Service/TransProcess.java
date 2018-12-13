package Service;

import Model.*;
import Utils.DateCompute;

import java.util.*;
import java.util.regex.Pattern;

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
     * 处理循环利息交易
     * @param interst
     */
    private void processInterest(Transaction interst, Date today){
        account.getAnswer().put(today, interst.getAmount());
        account.setAnswer(account.getAnswer());
    }

    /**
     * 还款交易处理
     * @param repay 还款交易
     */
    private void repayment (Transaction repay, int strikeOrderIndex, Date today){
        double amount = Math.abs(repay.getAmount()); //该值在交易读取时为负值，先取绝对值
        List<Integer> strikeOrder = strikeOrderList.get(strikeOrderIndex);
        for(int i=0; i<strikeOrder.size(); i+=3){
            //定位某个BP下的某个栏位,不考虑get不到的情况
            List<BalanceList> BP_field = account.getBP().get(strikeOrder.get(i)).getBalance().get(strikeOrder.get(i+1));
            amount = strikeAndAccr(BP_field, amount, repay, strikeOrder.get(i+2), today);    //冲抵了一个栏位后剩余的金额
            if (amount==0.0)  break;
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
    private void creditTrans (Transaction credit, int strikeOrderIndex, Date today){
        double amount = Math.abs(credit.getAmount());
        Date transDate = credit.getTransDate();
        Date recordDate = credit.getRecordDate();
        List<Integer> strikeOrder = strikeOrderList.get(strikeOrderIndex);
        int period = DateCompute.judgeCycle(account.getCycleDay(),transDate,recordDate);
        int billout = 1;       //交易日在往期
        if(period==0){         //交易日在当期
            billout = 0;       //冲抵CTD
        }
        //从冲账顺序表中查找冲抵栏位
        List<BalanceList> BP_field = account.getBP().get(credit.getTC().getBP()).getBalance().get(billout);
        //Todo 这里按一种利率处理贷方回算，写死
        addTracebackNode(BP_field.get(0), credit, credit.getAmount(),billout);

        amount = strikeAndAccr(BP_field, amount, credit, billout, today);
        int index = 0;
        while(amount > 0 && index < strikeOrder.size()-1){
            BP_field = account.getBP().get(strikeOrder.get(index)).getBalance().get(strikeOrder.get(index+1));
            amount = strikeAndAccr(BP_field, amount, credit, billout, today);    //冲抵了一个栏位后剩余的金额
        }
        /**
         * 若冲抵了所有余额还有剩余，则存入溢缴款
         */
        if (amount > 0)   account.setOverflow(account.getOverflow() + amount);
    }

    /**
     * 借方交易入账处理
     * @param debit 借方交易
     */
    private void debitTrans(Transaction debit, boolean isFirstCycle){
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
        else    startDate = debit.getRecordDate();  //以交易属性直接判断是否回算
        field.get(index).getBL().addLast(new BalanceNode(field.get(index),amount,startDate,debit.getRecordDate(),debit.getRecordDate(),TC.isFreeInt(),debit.getSummary(),0));

    }





    /**
     * 根据TC决定每个交易由哪个方法处理
     * @param
     */
    public void transRoute(Transaction t, boolean isFirstCycle, int strikeOrderIndex, Date today){
        TransCode TC = t.getTC();
        if(TC.getDirection().equals('R'))  repayment(t, strikeOrderIndex, today);
//        else if((TC.equals(TransCode.TC3000) && !isFirstCycleDay) //非首月才读取取现交易
//                || (!TC.equals(TransCode.TC3000) && TC.getDirection().equals('D'))){
//            debitTrans(t);
//        }
        else if(TC.getDirection().equals('D')){
            debitTrans(t, isFirstCycle);
        }
        else if(TC.getDirection().equals('C')){
            creditTrans(t,strikeOrderIndex, today);
        }
        else if(TC.getDirection().equals('I')){
            processInterest(t, today);
        }
        // 可能有未收录的交易，则不处理
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
     * 冲抵某个余额栏位，包含冲抵各个node的计息、还款交易回算逻辑
     * 还款交易回算逻辑：
     *
     * @param BP_field  余额栏位
     * @param amount    还款或贷方交易的剩余金额
     * @param tr        还款或贷方交易
     * @return          剩余的还款或贷方金额
     *
     * Todo 都按仅有一种利率余额处理，这里贷方和还款都按利率由高到低冲抵
     *
     */
    private double strikeAndAccr(List<BalanceList> BP_field, double amount, Transaction tr, int billout, Date today) {
        if (amount <= 0)    return 0.0;
        double oriAmount = amount;   //备份原金额
        for (BalanceList BL : BP_field) {
            int pointer = 0;
            if(billout == 0)    pointer = BL.getPointer();  //如果要冲抵未出金额，则从billout为0的node开始遍历
            ListIterator it = BL.getBL().listIterator(pointer);
            while (it.hasNext()) {
                BalanceNode node = (BalanceNode) it.next();
                if (billout < 2 && node.getBillout()!=billout) break;      //如果该node的出账状态与所需不吻合，则停止冲账, 2表示不区分出账状态
                if (!node.isExist()) continue;              //如果该BalanceList已被冲掉，那么则冲抵下一个余额栏位
                node.setExist(false);                       //该node死亡
                node.setEndDate(tr.getRecordDate());
                if (!node.getBL().getBP().isWaive()) {      //判断waive标识
                    //计算利息
                    double intrests = node.getAmount() * BL.getRate()
                            * (DateCompute.getIntervalDays(node.getStartDate(), node.getEndDate())+1);
                    node.setIntrests(intrests);
                    // 判断该利息的累计值应该放在PROV还是ACCR
                    if(node.isFreeInt() && node.getBillout()==0){
                        BL.setPROV(BL.getPROV() + intrests);
                    }
                    else {
                        BL.setACCR(BL.getACCR() + intrests);
                    }
                }
                if (amount >= node.getAmount()) {
                    /**
                     * 若金额足以冲抵该node，则冲抵完该node后剩余amount进一步冲抵下一个node
                     */
                    amount -= node.getAmount();

                } else {
                    /**
                     * 在node后插入一个新的node用于记录该node被冲账后剩余的金额
                     */
                    BalanceNode leftNode = new BalanceNode(BL, node.getAmount() - amount,
                            tr.getRecordDate(),
                            today,
                            today,
                            node.isFreeInt(),
                            node.getSummary(),
                            node.getBillout());
                    it.add(leftNode);
                    amount = 0.0;
                    //it.previous();  //这样才能遍历到新增的节点
                }
            }
            //还款冲抵该利率余额未冲抵完,则将冲抵部分的回算node添加至末尾
//
            /**
             * 若该交易为还款交易，则oriAmount和amount的差值为该栏位的BalanceList的回算计息基数
             *
             */
            if (tr.getTC().getDirection().equals('R')){
                addTracebackNode(BL,tr,oriAmount-amount, billout);
            }
        }
        return amount;
    }

    /**
     * 新增回算Node
     * @param BL
     * @param tr
     * @param tracebackAmount
     * @return 新增的回算Node
     *
     * 还款回算：在冲抵余额栏位后以冲抵金额作为计息基数回算
     * 贷方回算：只新建一个Node
     */
    private void addTracebackNode(BalanceList BL, Transaction tr, double tracebackAmount, int billout){
            int period = DateCompute.judgeCycle(account.getCycleDay(),tr.getTransDate(),tr.getRecordDate());
            if(period < 2) {    //检查是否原交易日在两周期前
                BalanceNode tracebackNode = new BalanceNode(BL,
                        -tracebackAmount,
                        tr.getTransDate(),
                        tr.getRecordDate(),
                        tr.getRecordDate(),
                        false,
                        tr.getSummary() + "(回算)",
                        billout);
                tracebackNode.setExist(false);  //该node一出生就是死的
                double intrests = tracebackNode.getAmount() * BL.getRate()
                        * (DateCompute.getIntervalDays(tracebackNode.getStartDate(), tracebackNode.getEndDate())+1);
                if(billout==1){
                    if (BL.getACCR() + intrests > 0) {
                        tracebackNode.setIntrests(intrests);
                        BL.setACCR(BL.getACCR() + intrests);
                    } else {
                        tracebackNode.setIntrests(-BL.getACCR());
                        BL.setACCR(0);
                    }
                    BL.getBL().add(BL.getPointer(), tracebackNode);
                }
                else if(billout==0){
                    if (BL.getPROV() + intrests > 0) {
                        tracebackNode.setIntrests(intrests);
                        BL.setPROV(BL.getPROV() + intrests);
                    } else {
                        tracebackNode.setIntrests(-BL.getPROV());
                        BL.setPROV(0);
                    }
                    BL.getBL().addLast(tracebackNode);
                }
            }
    }


    public TransProcess(Account account, List<List<Integer>> strikeOrderList) {
        this.account = account;
        this.strikeOrderList = strikeOrderList;
    }


}
