package Model;

/**
 * Created by Victor on 2018/9/19.
 */
public enum TransCode {
//从左到右：BP，Field，方向，是否免息，是否回算
    TC4000(0,0,"D", true, false),
    TC4100(0,0,"C", false, true),
    TC4001(2,0,"D", true, false),
    TC4101(2,0,"C", true, false),
    TC3000(1,0,"D", false, true),
    TC3100(1,0,"C", false, true),
    TC2000(-1,-1,"R", false, false),
    TC4306(0,0,"C", false, false),
    TC5802(3,0,"D",false,false),
    TC5902(3,0,"C",false,false),
    TC6011(4,0,"D",false,false),
    TC6111(4,0,"C",false,false),
    TC1200(0,1,"I",true,false),
    TC1300(0,1,"C",true,false),
    TC1000(1,1,"I",false,false),
    TC1100(1,1,"C",false,false),
    TC5805(2,1,"I",true,false),
    TC5905(2,1,"C",true,false);


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
