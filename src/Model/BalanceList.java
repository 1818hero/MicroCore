package Model;

import java.util.LinkedList;
import java.util.List;

/**
 * BP在时间维度的细分
 * Created by Victor on 2018/9/19.
 */
public class BalanceList {
    BalanceProgram BP;              //该BalanceList所属BP
    LinkedList<BalanceNode> BL;
    int pointer;                    //标识未出余额从第几个Node开始
    double rate;                    //该BalanceList的利率参数片
    double PROV;                    //该BalanceList的PROV栏位
    double ACCR;                    //该BalanceList累积的未出利息
    double CTD;                     //该BalanceList的未出总金额
    double BNP;                     //该BalanceList的已出总金额

    public LinkedList<BalanceNode> getBL() {
        return BL;
    }

    public void setBL(LinkedList<BalanceNode> BL) {
        this.BL = BL;
    }

    public BalanceProgram getBP() {
        return BP;
    }

    public void setBP(BalanceProgram BP) {
        this.BP = BP;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getACCR() {
        return ACCR;
    }

    public void setACCR(double ACCR) {
        this.ACCR = ACCR;
    }

    public double getCTD() {
        return CTD;
    }

    public void setCTD(double CTD) {
        this.CTD = CTD;
    }

    public double getBNP() {
        return BNP;
    }

    public void setBNP(double BNP) {
        this.BNP = BNP;
    }

    public double getPROV() {
        return PROV;
    }

    public void setPROV(double PROV) {
        this.PROV = PROV;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public BalanceList(BalanceProgram BP, double rate){
        this.BP = BP;
        this.rate = rate;   // 默认BalanceList的利率
        this.BL = new LinkedList<>();
        this.CTD = 0;
        this.BNP = 0;
        this.PROV = 0;
        this.ACCR = 0;
        //创建该BalanceList的默认头节点
        //this.BL.add(new BalanceNode(0,BP.getAccount().getToday(),BP.getAccount().getToday(),false,""));
    }
}
