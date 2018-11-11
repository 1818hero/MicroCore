package Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Victor on 2018/9/19.
 * 账户结构
 * + Account
 *    - BP(RTL)
 *      - Field（BNP）
 *          - 高利率BalanceList
 *              - BalanceNode链表
 *          - 低利率BalanceList
 *      - Field (CTD)
 *      - Field (FEE BNP)
 *      - Field (FEE CTD)
 *    - BP(CSH)
 *    - BP(分期)
 *    - BP(FEE)
 *
 */
public class Account {
    List<BalanceProgram> BP = new ArrayList<>();   //账户余额
    double overflow=0;          //溢缴款
    int cycleDay;               //账单日（每月几号）可以写死也可以根据Dispatcher的方法计算得到
    int lastPaymentDay;         //最后还款日（每月几号）
    int graceDay;               //宽限日（每月几号）
    Date startDate;             //账户初始日期
    Date today;                 //当前日期
    int late = 1;               //延滞标识
    double accRate;             //账户当日利率
    double limit;               //账户额度
    double avLimit;             //可用额度
    double lateDayDueAmount;    //宽限日前未还清金额


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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
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

    public double getAvLimit() {
        return avLimit;
    }

    public void setAvLimit(double avLimit) {
        this.avLimit = avLimit;
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

    public double getLateDayDueAmount() {
        return lateDayDueAmount;
    }

    public void setLateDayDueAmount(double lateDayDueAmount) {
        this.lateDayDueAmount = lateDayDueAmount;
    }

    public Account(int cycleDay, Date startDate, Date today, double limit){
        this.startDate = startDate;
        this.cycleDay = cycleDay;
        this.today = today;
        this.limit = limit;
        this.avLimit = limit;
        BalanceProgram RTL1 = new BalanceProgram(this);
        BP.add(RTL1);   //位置0
        BalanceProgram CSH1 = new BalanceProgram(this);
        BP.add(CSH1);   //位置1
        BalanceProgram INSTL = new BalanceProgram(this);
        BP.add(INSTL);  //位置2
        BalanceProgram FEE = new BalanceProgram(this);
        BP.add(FEE);    //位置3
    }
}
