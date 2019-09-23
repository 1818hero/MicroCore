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
     * K： Account的延滞状态
     * V： 该延滞状态下使用的strikeOrder
     *目前延滞阶段为0，1，2，3使用strikeOrder 0
     * 大于3使用strikeOrder 1
     */
    Map<Integer, Integer> strikeOrderDispatcher;

    /**
     * 冲账顺序strikeOrderList:
     * 外层List标识了哪套冲账参数
     * 内层List中：每3个数从Account中定位一个余额栏位,可支持多个不同的strikeOrder参数片
     * 第1个：标识在哪个BP（0: RTL, 1:CSH, 2:INSTL, 3:FEE）
     * 第2个：标识在本金栏位（0）还是在息费栏位（1）
     * 第3个：标识冲抵该栏位的已出金额（1）还是未出金额（0）
     */
    List<List<Integer>> strikeOrderList;

    /**
     * 还款交易处理
     * @param repay 还款交易
     */
    private void repayment (Transaction repay){
        double amount = Math.abs(repay.getAmount()); //该值在交易读取时为负值，先取绝对值
        account.setBNP(Math.max(account.getBNP()-amount,0));
        List<Integer> strikeOrder = strikeOrderList.get(strikeOrderDispatcher.get(account.getLate()));
        for(int i=0; i<strikeOrder.size(); i+=3){
            //定位某个BP下的某个栏位,不考虑get不到的情况
            List<BalanceList> BP_field = account.getBP().get(strikeOrder.get(i)).getBalance().get(strikeOrder.get(i+1));
            //amount = strikeAndAccr(BP_field, amount, repay, strikeOrder.get(i+2), today);    //冲抵了一个栏位后剩余的金额
            amount = strikeAndAccr(BP_field, amount, repay, strikeOrder.get(i+2), repay.getTransDate());
            if (amount<=0.0)  break;
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
    private void creditTrans (Transaction credit){
        double amount = Math.abs(credit.getAmount());
        Date transDate = credit.getTransDate();
        Date recordDate = credit.getRecordDate();
        List<Integer> strikeOrder = strikeOrderList.get(strikeOrderDispatcher.get(account.getLate()));
        int period = DateCompute.judgeCycle(account.getCycleDay(),transDate,recordDate);
        int billout = 0;       //交易日在当期
        if(period == 1){         //交易日在往期
            billout = 1;       //冲抵BNP
            account.setBNP(Math.max(account.getBNP()-amount,0));
        }
        TransCode TC = credit.getTC();
        //从冲账顺序表中查找冲抵栏位
        List<BalanceList> BP_field = account.getBP().get(TC.getBP()).getBalance().get(TC.getField());
        amount = strikeAndAccr(BP_field, amount, credit, billout, credit.getRecordDate());
        //Todo 这里按一种利率处理贷方回算，写死
        if(credit.getTC().isTraceback()){
            addTracebackNode(BP_field.get(0), credit, credit.getAmount(),billout);
        }
        for(int i=0; i<strikeOrder.size(); i+=3){
            //定位某个BP下的某个栏位,不考虑get不到的情况
            BP_field = account.getBP().get(strikeOrder.get(i)).getBalance().get(strikeOrder.get(i+1));
            amount = strikeAndAccr(BP_field, amount, credit, strikeOrder.get(i+2), credit.getRecordDate());    //冲抵了一个栏位后剩余的金额
            if (amount<=0.0)  break;
        }
        /**
         * 若冲抵了所有余额还有剩余，则存入溢缴款
         */
        if (amount > 0)   account.setOverflow(account.getOverflow() + amount);
    }

    /**
     * 借方交易入账处理
     * @param debit 借方交易
     * @param billout 入账交易是已出交易还是未出交易
     */
    private void debitTrans(Transaction debit, int billout){
        double amount = debit.getAmount();
        //处理溢缴款
        if(account.getOverflow() >= amount){
            account.setOverflow(account.getOverflow()-amount);
            return;
        }
        else{
            amount -= account.getOverflow();
            account.setOverflow(0);
        }
        TransCode TC = debit.getTC();
        BalanceProgram targetBP = account.getBP().get(TC.getBP());
        List<BalanceList> field = targetBP.getBalance().get(TC.getField());
        //如果该栏位没有BalanceList，则以当前利率创建一条
        if(field.size()==0){
            field.add(new BalanceList(targetBP,account.getAccRate(),TC.getField()));
        }
        int index = findRate(field);    //
        if(index < 0){
            field.add(0, new BalanceList(targetBP, account.getAccRate(), TC.getField()));
            index = 0;
        }
        else if (account.getAccRate() < field.get(index).getRate()){
            field.add(index, new BalanceList(targetBP, account.getAccRate(), TC.getField()));
        }
        Date startDate;
        if (TC.isTraceback())    startDate = debit.getTransDate();
        else    startDate = debit.getRecordDate();  //以交易属性直接判断是否回算
        BalanceList curBalanceList = field.get(index);
        BalanceNode oriNode = null;
        BalanceNode newNode = null;
        if(curBalanceList.getBL().size() > 0)    oriNode = curBalanceList.getBL().getLast();
        /**
         * 对于息费类交易入账，直接在原Node基础上增加值
         */
        if (curBalanceList.getType()==1 && oriNode != null && oriNode.isExist()) {
            oriNode.setExist(false);
            oriNode.setEndDate(DateCompute.addDate(debit.getRecordDate(), -1));
            //if (node.getBL().getBP().getWaive()==0) {      //判断waive标识
            //计算利息
            double intrests = oriNode.getAmount() * curBalanceList.getRate()
                    * (DateCompute.getIntervalDays(oriNode.getStartDate(), oriNode.getEndDate())+1);
            oriNode.setIntrests(intrests);
            newNode = new BalanceNode(curBalanceList,
                    oriNode.getAmount()+amount,
                    debit.getRecordDate(),
                    debit.getRecordDate(),
                    debit.getRecordDate(),
                    TC.isFreeInt(),
                    debit.getSummary(),
                    billout);
        }
        else{
            newNode = new BalanceNode(curBalanceList, amount, debit.getRecordDate(), startDate,
                    debit.getRecordDate(), TC.isFreeInt(), debit.getSummary(), billout);
        }
        curBalanceList.getBL().addLast(newNode);
        if(billout==0) {
            curBalanceList.setCTD(curBalanceList.getCTD() + amount);  //入账后CTD/BNP累计值增加
        }
        else{
            curBalanceList.setBNP(curBalanceList.getBNP() + amount);
        }

    }

    /**
     * 借方交易入账处理 （无TC）
     * @param BPNum
     * @param FieldNum
     * @param amount
     * @param startDate
     * @param freeInt
     * @param billout
     * @param summary
     */
    public void debitTrans(int BPNum, int FieldNum, double amount, Date startDate, boolean freeInt, int billout, String summary){
        BalanceProgram targetBP = null;
        List<BalanceList> field = null;
        try {
            targetBP = account.getBP().get(BPNum);
            field = targetBP.getBalance().get(FieldNum);
        }catch (Exception e){
            e.printStackTrace();
        }
        //如果该栏位没有BalanceList，则以当前利率创建一条
        if(field.size()==0){
            field.add(new BalanceList(targetBP,account.getAccRate(),FieldNum));
        }
        int index = findRate(field);    //
        if(index < 0){
            field.add(0, new BalanceList(targetBP, account.getAccRate(), FieldNum));
            index = 0;
        }
        else if (account.getAccRate() < field.get(index).getRate()){
            field.add(index, new BalanceList(targetBP, account.getAccRate(), FieldNum));
        }
        BalanceList curBalanceList = field.get(index);
        BalanceNode oriNode = null;
        BalanceNode newNode = null;
        if(curBalanceList.getBL().size() > 0)    oriNode = curBalanceList.getBL().getLast();
        /**
         * 对于息费类交易入账，直接在原Node基础上增加值
         */
        if (curBalanceList.getType()==1 && oriNode != null && oriNode.isExist()) {
            oriNode.setExist(false);
            oriNode.setEndDate(DateCompute.addDate(startDate, -1));
            //if (node.getBL().getBP().getWaive()==0) {      //判断waive标识
            //计算利息
            double intrests = oriNode.getAmount() * curBalanceList.getRate()
                    * (DateCompute.getIntervalDays(oriNode.getStartDate(), oriNode.getEndDate())+1);
            oriNode.setIntrests(intrests);
            newNode = new BalanceNode(curBalanceList,
                    oriNode.getAmount()+amount,
                    startDate,
                    startDate,
                    startDate,
                    freeInt,
                    summary,
                    billout);
        }
        else{
            newNode = new BalanceNode(curBalanceList, amount, startDate, startDate,
                    startDate, freeInt, summary, billout);
        }
        curBalanceList.getBL().addLast(newNode);
        if(billout==0) {
            curBalanceList.setCTD(curBalanceList.getCTD() + amount);  //入账后CTD/BNP累计值增加
        }
        else{
            curBalanceList.setBNP(curBalanceList.getBNP() + amount);
        }

    }



    /**
     * 根据TC决定每个交易由哪个方法处理
     * 不处理Memo类交易，直接返回
     * @param
     */
    public Transaction transRoute(Transaction t){
        TransCode TC = t.getTC();
        if(TC.getDirection().equals("R")){
            repayment(t);
            return null;
        }
//        else if((TC.equals(TransCode.TC3000) && !isFirstCycleDay) //非首月才读取取现交易
//                || (!TC.equals(TransCode.TC3000) && TC.getDirection().equals('D'))){
//            debitTrans(t);
//        }
        else if(TC.getDirection().equals("D")){
            debitTrans(t, 0);
            return null;
        }
        else if(TC.getDirection().equals("C")){
            creditTrans(t);
            return null;
        }
        else if(TC.getDirection().equals("I")){
            return t;
        }
        return null;
        // 可能有未收录的交易，则不处理
    }

    /**
     * 批量处理首个账单日的利息MEMO交易
     */
    public void processMEMO(List<Transaction> trList){
        for(Transaction tr : trList){
            //debitTrans(tr, 1);
            debitTrans(tr.getTC().getBP(),tr.getTC().getField(),tr.getAmount(),DateCompute.addDate(tr.getRecordDate(),1),tr.getTC().isFreeInt(),1,tr.getSummary());
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
     * 冲抵某个余额栏位，包含冲抵各个node的计息、还款交易回算逻辑
     * 还款交易回算逻辑：
     *
     * @param BP_field  余额栏位
     * @param amount    还款或贷方交易的剩余金额
     * @param tr        还款或贷方交易
     * @param billout   1表示冲抵已出余额，0表示冲抵未出金额
     * @param date      表示对应的贷方交易入账日/还款日是多少
     * @return          剩余的还款或贷方金额
     *
     * Todo 都按仅有一种利率余额处理，这里贷方和还款都按利率由高到低冲抵
     *
     */
    private double strikeAndAccr(List<BalanceList> BP_field, double amount, Transaction tr, int billout, Date date) {
        if (amount <= 0)    return 0.0;
        for (BalanceList BL : BP_field) {
            if(BL.getBL().size()==0)    continue;   //如果该BL没有Node，则跳过
            double oriAmount = amount;   //备份冲抵本BalanceList的原金额
            int start = 0;              //冲抵的起点Node标识
            int p = 0;                  //冲抵到第几个Node的标识
            int end = BL.getPointer();          //冲抵结束Node标记
            if(billout == 0){
                start = BL.getPointer();  //如果要冲抵未出金额，则从billout为0的node开始遍历
                p = start;
                end = BL.getBL().size();
            }
            ListIterator it = BL.getBL().listIterator(start);
            while (amount > 0 && it.hasNext() && p++ < end) {
                BalanceNode node = (BalanceNode) it.next();
                if (billout < 2 && node.getBillout()!=billout) break;      //如果该node的出账状态与所需不吻合，则停止冲账, 2表示不区分出账状态
                if (!node.isExist()) continue;              //
                node.setExist(false);                       //该node死亡
                //node.setEndDate(DateCompute.addDate(tr.getRecordDate(),-1));    //止息日为入账日前一天
                node.setEndDate(DateCompute.addDate(date, -1));        //测试
                //if (node.getBL().getBP().getWaive()==0) {      //判断waive标识
                    //计算利息
                double intrests = node.getAmount() * BL.getRate()
                        * (DateCompute.getIntervalDays(node.getStartDate(), node.getEndDate())+1);
                node.setIntrests(intrests);
                // 判断该利息的累计值应该放在PROV还是ACCR（现在暂时没有用处）
                if(node.isFreeInt() && node.getBillout()==0){
                    BL.setPROV(BL.getPROV() + intrests);
                }
                else {
                    BL.setACCR(BL.getACCR() + intrests);
                }
                //}
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
                            date,
                            date,
                            node.isFreeInt(),
                            node.getSummary(),
                            node.getBillout());
                    if (DateCompute.getIntervalDays(node.getStartDate(), node.getEndDate())<0){
                        leftNode.setAnoSummary(node.getAnoSummary()+"("+tr.getSummary()+")");
                    }
                    else {
                        leftNode.setAnoSummary("("+tr.getSummary()+")");
                    }
                    it.add(leftNode);   //在原Node后添加该Node，因此end就需要增加1
                    end += 1;
                    if(billout==1)  BL.setPointer(BL.getPointer()+1);
                    amount = 0.0;
                    //it.previous();  //这样才能遍历到新增的节点
                }

            }
            //还款冲抵该利率余额未冲抵完,则将冲抵部分的回算node添加至末尾
            /**
             * (弃用)若该交易为还款交易，则oriAmount和amount的差值为该栏位的BalanceList的回算计息基数
             */
//            if (tr.getTC().getDirection().equals("R")&&oriAmount!=amount){
//                addTracebackNode(BL,tr,oriAmount-amount, billout);
//            }
            /**
             * 根据billout标识计算冲账后BalanceList的余额
             */
            if (billout==1){
                BL.setBNP(BL.getBNP()+oriAmount-amount);
            }
            else{
                BL.setCTD(BL.getCTD()+oriAmount-amount);
            }
        }
        return amount;
    }

    /**
     * （暂时仅用于贷方交易，且不判断PROV、ACCR值）
     * 新增回算Node（仅用于增加还款、贷方交易回算节点）
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
                        tracebackAmount,
                        tr.getRecordDate(),
                        tr.getTransDate(),
                        DateCompute.addDate(tr.getRecordDate(), -1),
                        BL.getBP().isFreeInt(),
                        tr.getSummary() + "(回算)",
                        billout);
                tracebackNode.setExist(false);  //该node一出生就是死的
                double intrests = tracebackNode.getAmount() * BL.getRate()
                        * (DateCompute.getIntervalDays(tracebackNode.getStartDate(), tracebackNode.getEndDate())+1);

                tracebackNode.setIntrests(intrests);    //暂时不考虑ACCR、PROV
                if(billout==1){
                    //由于ACCR、PROV未同步累积，因此下述代码暂时注释，下同
//                    if (BL.getACCR() + intrests > 0) {
//                        tracebackNode.setIntrests(intrests);
//                        BL.setACCR(BL.getACCR() + intrests);
//                    } else {
//                        tracebackNode.setIntrests(-BL.getACCR());
//                        BL.setACCR(0);
//                    }
                    BL.getBL().add(BL.getPointer(), tracebackNode);
                    BL.setPointer(BL.getPointer()+1);
                }
                else if(billout==0){
//                    if (BL.getPROV() + intrests > 0) {
//                        tracebackNode.setIntrests(intrests);
//                        BL.setPROV(BL.getPROV() + intrests);
//                    } else {
//                        tracebackNode.setIntrests(-BL.getPROV());
//                        BL.setPROV(0);
//                    }
                    BL.getBL().addLast(tracebackNode);
                }
            }
    }


    public TransProcess(Account account, List<List<Integer>> strikeOrderList,
                        Map<Integer, Integer> strikeOrderDispatcher) {
        this.account = account;
        this.strikeOrderList = strikeOrderList;
        this.strikeOrderDispatcher = strikeOrderDispatcher;
    }


}
