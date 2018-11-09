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
     * 冲账顺序strikeOrder:每两个数从Account中定位一个余额栏位
     */
    List<Integer> strikeOrder;
    /**
     * 还款交易处理
     * @param repay 还款交易
     */
    public void repayment (Transaction repay){
        double amount = repay.getAmount();
        Date transDate = repay.getTransDate();
        for(int i=0; i<strikeOrder.size(); i+=2){
            //定位某个BP下的某个栏位,不考虑get不到的情况
            List<BalanceList> BP_field = account.getBP().get(strikeOrder.get(i)).getBalance().get(strikeOrder.get(i+1));
            Collections.sort(BP_field, new Comparator<BalanceList>() {
                @Override
                public int compare(BalanceList o1, BalanceList o2) {
                    if (o2.getRate() > o1.getRate())    return 1;
                    else if(o1.getRate() > o2.getRate())    return -1;
                    return 0;
                }
            });
            for(BalanceList BL : BP_field){
                ListIterator it = BL.getBL().listIterator();
                while(it.hasNext()){
                    BalanceNode node = (BalanceNode) it.next();
                    if (!node.isExist())    continue;              //如果该BalanceList已被冲掉，那么则冲抵下一个余额栏位
                    if (amount > 0) node.setExist(false);          //若该还款还有余额，则该node死亡
                    node.setEndDate(account.getToday());           //这里没有回算,Todo 后续考虑Today和入账日同步问题
                    if(!node.getBL().getBP().isWaive()) {          //判断是否免息
                        //计算利息
                        node.setIntrests(node.getAmount() * BL.getRate()
                                * (DateCompute.getIntervalDays(node.getStartDate(), node.getEndDate())));
                        //回算逻辑：node余额*利率*（入账日-交易日）
                        node.setTracebackAmount(-node.getAmount() * BL.getRate()
                                * (DateCompute.getIntervalDays(transDate, node.getEndDate())));
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
                                account.getToday(),
                                account.getToday(),
                                node.isFreeInt(),
                                node.getSummary());
                        it.add(leftNode);
                        return;
                        //it.previous();  //这样才能遍历到新增的节点

                    }

                }
            }

        }
        /**
         * 若冲抵了所有余额还有剩余，则存入溢缴款
         */
        if (amount>0)   account.setOverflow(account.getOverflow()+amount);
    }
    /**
     * 借方交易处理
     * @param debit 借方交易
     */
    public void debitTrans(Transaction debit){
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
        field.get(index).getBL().addLast(new BalanceNode(field.get(index),amount,startDate,account.getToday(),TC.isFreeInt(),debit.getSummary()));

    }

    /**
     * 根据TC决定每个交易由哪个方法处理
     * @param trans
     */
    public void transRoute(Transaction[] trans){
        //按入账顺序将交易排序
        Arrays.sort(trans, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                if(DateCompute.getIntervalDays(o1.getRecordDate(),o2.getRecordDate())<0) return 1;
                else if(DateCompute.getIntervalDays(o1.getRecordDate(),o2.getRecordDate())>0)    return -1;
                else return 0;
            };
        });
        for(Transaction t : trans){
            if(t.getTC().equals(TransCode.TC2000))  repayment(t);
            else if(t.getTC().equals(TransCode.TC3000) || t.getTC().equals(TransCode.TC4000))   debitTrans(t);
        }
    }

    /**
     * 工具类用于查找某个栏位的利率余额 （等效Collections.binarySearch()）
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

    public TransProcess(Account account, List<Integer> strikeOrder) {
        this.account = account;
        this.strikeOrder = strikeOrder;
    }
}
