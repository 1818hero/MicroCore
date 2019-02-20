package Model;


import java.util.Date;

/**
 * Created by Victor on 2018/9/19.
 */
public class Transaction  {
    double amount;
    TransCode TC;
    Date transDate;
    Date recordDate;
    String MCC;
    String summary;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransCode getTC() {
        return TC;
    }

    public void setTC(TransCode TC) {
        this.TC = TC;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getMCC() {
        return MCC;
    }

    public void setMCC(String MCC) {
        this.MCC = MCC;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }



}
