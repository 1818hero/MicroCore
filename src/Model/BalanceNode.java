package Model;

import java.util.Date;

/**
 * 组成BalanceList的原子结构
 * Created by Victor on 2018/9/20.
 */
public class BalanceNode {
    BalanceList BL; //该Node所属BalanceList
    //Date recordDate;//入账日（暂未使用）
    double amount;  //金额
    Date startDate; //起息日
    Date endDate;   //止息日
    String summary; //摘要
    int billout;    //是否已出账，0表示未出，1表示已出
    /**
     * freeInt：是否处在免息期
     * 当为True时：计息值不发生迁移
     * 最后还款日时，若已出账且freeInt为True，则freeInt变为False
     */
    boolean freeInt;    //是否在免息期
    /**
     * 计息值intrests：
     * if freeInt==true and billout==0 ：intrests累计在PROV栏位
     * else：intrests累计在ACCR栏位
     *
     */
    double intrests;          //计息值
    //double tracebackAmount; //回算金额
    boolean exist;          //node是否存活，当为False时，不再更新止息日

//    public double getTracebackAmount() {
//        return tracebackAmount;
//    }
//
//    public void setTracebackAmount(double tracebackAmount) {
//        this.tracebackAmount = tracebackAmount;
//    }
//

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isFreeInt() {
        return freeInt;
    }

    public void setFreeInt(boolean freeInt) {
        this.freeInt = freeInt;
    }

    public double getIntrests() {
        return intrests;
    }

    public void setIntrests(double intrests) {
        this.intrests = intrests;
    }

    public BalanceList getBL() {
        return BL;
    }

    public void setBL(BalanceList BL) {
        this.BL = BL;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getBillout() {
        return billout;
    }

    public void setBillout(int billout) {
        this.billout = billout;
    }

    public BalanceNode(BalanceList BL, double amount, Date startDate, Date endDate, boolean freeInt, String summary, int billout) {
        this.BL = BL;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.freeInt = freeInt;
        this.intrests = 0;
        //this.tracebackAmount = 0;
        this.exist = true;
        this.summary = summary;
        this.billout = billout;
    }
}