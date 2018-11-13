package Model;

/**
 * Created by Victor on 2018/9/19.
 */
public enum TransCode {
    TC4000(0,1,"D", true, false),
    TC4100(0,-1,"C", false, true),   //Todo ：贷方交易的原交易日不同，冲抵的余额也就不同
    TC4001(2,1,"D", true, false),
    TC4101(2,-1,"C", true, false),
    TC4002(2,1,"D", true, false),
    TC4102(2,-1,"C", true, false),
    TC4003(2,1,"D", true, false),
    TC4103(2,-1,"C", true, false),
    TC3000(1,1,"D", false, true),
    TC3100(1,0,"C", false, true),
    TC2000(-1,-1,"R", false, true);
    /**
     * BP对应的序号:
     * 0: RTL1
     * 1: CSH1
     * 2: INSTL
     * 3: FEE
     */
    private int BP;
    /**
     * 栏位序号：
     * 0：BNP
     * 1：CTD
     * 2：FEE BNP
     * 3：FEE CTD
     */
    private int field;   //栏位对应的序号
    private String direction;   //交易方向（D：借方；C：贷方；R:还款）
    private boolean traceback;  //该交易是否回算
    private boolean freeInt;

    public int getBP() {
        return BP;
    }

    public void setBP(int BP) {
        this.BP = BP;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isTraceback() {
        return traceback;
    }

    public void setTraceback(boolean traceback) {
        this.traceback = traceback;
    }

    public boolean isFreeInt() {
        return freeInt;
    }

    public void setFreeInt(boolean freeInt) {
        this.freeInt = freeInt;
    }

    TransCode(int BP, int field, String direction, boolean freeInt, boolean traceback) {
        this.BP = BP;
        this.field = field;
        this.direction = direction;
        this.freeInt = freeInt;
        this.traceback = traceback;
    }
}
