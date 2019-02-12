package Model;

import java.util.*;

/**
 * Created by Victor on 2018/9/19.
 * 账户结构
 * + Account
 *    - BP(RTL)
 *      - Field（Balance）
 *          - 高利率BalanceList
 *              - BalanceNode链表
 *          - 低利率BalanceList
 *      - Field (FEE)
 *    - BP(CSH)
 *    - BP(分期)
 *    - BP(FEE)
 *
 */
public class Account {
    List<BalanceProgram> BP = new ArrayList<>();   //账户余额
    double overflow;          //溢缴款
    int cycleDay;               //账单日（每月几号）
    int lastPaymentDay;         //最后还款日（每月几号）
    int graceDay;               //宽限日（每月几号）
    //static final List<Integer> cycle2graceDay = new ArrayList<Integer>(){};  //Todo 构造方法后面接{}是什么意思
    Date initDate;              //账户初始日期
    int late;                   //延滞标识(与会计核算码一致)
    static double accRate = 0.0005;    //账户当日利率
    //static final int freeIntInterval = 18;   //免息期间隔
    //static final int graceDayInterval = 3;   //宽限期间隔
    double limit;               //账户额度
    double spent;               //已用额度
    double BNP;                 //宽限日未还清账单金额
    double lateDayDueAmt;       //宽限日未还最低金额（用于计算违约金）

    /**
     * 记录s-1期到e期的系统利息计算结果
     */
    Map<Date, Double> answer;
    Queue<Double> due;          //due值
    double minPayment;          //最低还款额

    public Map<Date, Double> getAnswer() {
        return answer;
    }

    public void setAnswer(Map<Date, Double> answer) {
        this.answer = answer;
    }

    public Queue<Double> getDue() {
        return due;
    }

    public void setDue(Queue<Double> due) {
        this.due = due;
    }

    public double getMinPayment() {
        return minPayment;
    }

    public void setMinPayment(double minPayment) {
        this.minPayment = minPayment;
    }




    public List<BalanceProgram> getBP() {
        return BP;
    }

    public void setBP(List<BalanceProgram> BP) {
        this.BP = BP;
    }

    public int getCycleDay() {
        return cycleDay;
    }

    public void setCycleDay(int cycleDay) {
        this.cycleDay = cycleDay;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public double getOverflow() {
        return overflow;
    }

    public void setOverflow(double overflow) {
        this.overflow = overflow;
    }

    public int getLastPaymentDay() {
        return lastPaymentDay;
    }

    public void setLastPaymentDay(int lastPaymentDay) {
        this.lastPaymentDay = lastPaymentDay;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

    public int getGraceDay() {
        return graceDay;
    }

    public void setGraceDay(int graceDay) {
        this.graceDay = graceDay;
    }

    public int getLate() {
        return late;
    }

    public void setLate(int late) {
        this.late = late;
    }

    public double getAccRate() {
        return accRate;
    }

    public void setAccRate(double accRate) {
        this.accRate = accRate;
    }

    public double getBNP() {
        return BNP;
    }

    public void setBNP(double BNP) {
        this.BNP = BNP;
    }

    public Account(int cycleDay, Date initDate, double limit){
        this.initDate = initDate;
        this.cycleDay = cycleDay;
        this.limit = limit;
        this.spent = limit;
        BalanceProgram RTL1 = new BalanceProgram(this);
        RTL1.setProductAttr("消费");
        RTL1.setFreeInt(true);
        BP.add(RTL1);   //位置0
        BalanceProgram CSH1 = new BalanceProgram(this);
        CSH1.setProductAttr("取现");
        CSH1.setFreeInt(false);
        BP.add(CSH1);   //位置1
        BalanceProgram INSTL = new BalanceProgram(this);
        INSTL.setProductAttr("分期");
        INSTL.setFreeInt(true);
        BP.add(INSTL);  //位置2
        BalanceProgram FEE = new BalanceProgram(this);
        FEE.setProductAttr("费用");
        FEE.setFreeInt(false);
        BP.add(FEE);    //位置3
        answer = new HashMap<>();
        BalanceProgram MEM = new BalanceProgram(this);
        MEM.setProductAttr("年费");
        MEM.setFreeInt(false);
        BP.add(MEM);    //位置4
        answer = new HashMap<>();
//        Date start = DateCompute.dateForm(startCycle+"-"+String.valueOf(cycleDay));
//        //Todo 跟进输入账期范围初始化answer
//        Date end = DateCompute.dateForm(endCycle+"-"+String.valueOf(cycleDay));
//        Date curDate = start;
//        while(!curDate.equals(end)){
//            answer.put(curDate, -1.0);
//            curDate = DateCompute.addMonth(curDate, 1);
//        }
        //Todo 测试用
        this.graceDay = 23;
    }
}
