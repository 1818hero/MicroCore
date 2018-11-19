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
    double rate;                    //该BalanceList的利率参数片
    double ACCR;                    //该BalanceList累积的未出利息

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
    public BalanceList(BalanceProgram BP, double rate){
        this.BP = BP;
        this.rate = rate;
        this.BL = new LinkedList<>();
        //创建该BalanceList的默认头节点
        //this.BL.add(new BalanceNode(0,BP.getAccount().getToday(),BP.getAccount().getToday(),false,""));
    }
}
