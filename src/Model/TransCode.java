package Model;

/**
 * Created by Victor on 2018/9/19.
 */
public enum TransCode {
    TC4000(0,0,"D", true, false),
    TC4100(0,0,"C", false, true),
    TC4001(2,0,"D", true, false),
    TC4101(2,0,"C", true, false),
    TC4002(2,0,"D", true, false),
    TC4102(2,0,"C", true, false),
    TC4003(2,0,"D", true, false),
    TC4103(2,0,"C", true, false),
    TC3000(1,0,"D", false, true),
    TC3100(1,0,"C", false, true),
    TC2000(-1,-1,"R", false, false),
    TC4306(0,0,"C", false, false),
    TC5802(3,1,"D",false,false),
    TC5803(3,1,"D",false,false),
    TC5808(3,1,"D",false,false),
    TC5805(2,1,"D",false,false),
    TC5806(2,1,"D",false,false),
    TC5809(2,1,"D",false,false),
    TC6011(4,1,"D",false,false),
    TC6013(3,1,"D",false,false),
    TC9698(-1,-1,"I",false,false);
    /**
     * BP对应的序号:
     * 0: RTL1
     * 1: CSH1
     * 2: INSTL
     * 3: FEE
     * 4: 年费
     */
    private int BP;
    /**
     * 栏位序号：
     * 0：本金
     * 1：FEE
     */
    private int field;   //栏位对应的序号
    private String direction;   //交易方向（D：借方；C：贷方；R:还款）
    private boolean traceback;  //该交易是否回算
    private boolean freeInt;    //该交易是否为免息交易

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
