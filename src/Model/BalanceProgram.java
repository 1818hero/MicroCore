package Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 账户余额在空间维度的细分
 * Created by Victor on 2018/10/21.
 */
public class BalanceProgram {
    Account account;        //该BP所属账户
    String assetAttr = "";  //资产属性
    String productAttr = "";//业务属性
    boolean waive = false;   //生息标识,若为true则该BP下的所有余额不生息
    /**
     * 账户余额List
     * 0：BNP
     * 1：CTD
     * 2：FEE_BNP
     * 3：FEE_CTD
     * 每个List由若干不同利率的余额链构成
     */
    List<List<BalanceList>> balance;

    public BalanceProgram(Account account) {
        this.account = account;
        balance = new ArrayList<List<BalanceList>>();
        for(int i=0; i<4; i++){
            BalanceList BL = new BalanceList(this,0.0005);
            balance.add(new ArrayList<>());
        }
    }

    public String getAssetAttr() {
        return assetAttr;
    }

    public void setAssetAttr(String assetAttr) {
        this.assetAttr = assetAttr;
    }

    public String getProductAttr() {
        return productAttr;
    }

    public void setProductAttr(String productAttr) {
        this.productAttr = productAttr;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isWaive() {
        return waive;
    }

    public void setWaive(boolean waive) {
        this.waive = waive;
    }

    public List<List<BalanceList>> getBalance() {
        return balance;
    }

    public void setBalance(List<List<BalanceList>> balance) {
        this.balance = balance;
    }
}
