package View;


import Model.Account;
import Model.Transaction;
import Service.DayProcess;
import Service.Dispatcher;
import Service.IOService;
import Service.TransProcess;
import Utils.DateCompute;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;
import org.apache.log4j.spi.LoggerFactory;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 */
public class SimpleFrame extends JFrame {

    private static Logger logger = Logger.getLogger(SimpleFrame.class);

    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 600;
    //首行位置
    private static final int FIRST_LINE_loc = 20;
    //行间距
    private static final int LINE_SPACE = 10;
    //首列位置
    private static final int FIRST_COLUMN_loc = 10;

    //上下宽度
    private static final int COMHEIGHT = 20;
    //文字左右宽度
    private static final int LABELWIDTH = 130;
    //输入框左右宽度
    private static final int TEXTWIDTH = 60;
    //列间距
    private static final int COLUMN_SPACE = 10;

    private int cycleDay;

    private String startCycle;
    private String endCycle;
    private IOService io;
    private Map<Integer, Integer> strikeOrderDispatcher = new HashMap<>();
    private List<List<Integer>> strikeOrder = null;
    private int StrikeOrder;    //0代表90天以内，4代表90天以上


    private Transaction[] trList;

    public SimpleFrame() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        io = new IOService();
    }

    public void displayWindow(){

        final SimpleFrame frame = new SimpleFrame();
        //输入部分
        JPanel textJP = new JPanel();
        frame.add(textJP);
        textJP.setLayout(null);

        JLabel RTLLabel= new JLabel("上期期初消费余额");
        RTLLabel.setBounds(FIRST_COLUMN_loc,FIRST_LINE_loc, LABELWIDTH,COMHEIGHT);
        textJP.add(RTLLabel);
        final JTextField RTLField = new JTextField(20);
        RTLField.setBounds(FIRST_COLUMN_loc+LABELWIDTH,FIRST_LINE_loc, TEXTWIDTH,COMHEIGHT);
        RTLField.setText("0");
        textJP.add(RTLField);

        JLabel RTLINTLabel= new JLabel("上期期初消费利息余额");
        RTLINTLabel.setBounds(FIRST_COLUMN_loc + LABELWIDTH + TEXTWIDTH + 6*COLUMN_SPACE ,FIRST_LINE_loc, LABELWIDTH,COMHEIGHT);
        textJP.add(RTLINTLabel);
        final JTextField RTLINTField = new JTextField(20);
        RTLINTField.setBounds(FIRST_COLUMN_loc + 2* LABELWIDTH + TEXTWIDTH + 7*COLUMN_SPACE ,FIRST_LINE_loc,TEXTWIDTH,COMHEIGHT);
        RTLINTField.setText("0");
        textJP.add(RTLINTField);

        JLabel FEELabel= new JLabel("上期期初费用");
        FEELabel.setBounds(FIRST_COLUMN_loc + 2* LABELWIDTH + 2* TEXTWIDTH + 14*COLUMN_SPACE ,FIRST_LINE_loc, LABELWIDTH,COMHEIGHT);
        textJP.add(FEELabel);
        final JTextField FEEField = new JTextField(20);
        FEEField.setBounds(FIRST_COLUMN_loc + 2* LABELWIDTH + 2* TEXTWIDTH + 23*COLUMN_SPACE ,FIRST_LINE_loc,TEXTWIDTH,COMHEIGHT);
        FEEField.setText("0");
        textJP.add(FEEField);

        JLabel CSHLabel= new JLabel("上期期初取现余额");
        CSHLabel.setBounds(FIRST_COLUMN_loc,FIRST_LINE_loc + COMHEIGHT + LINE_SPACE, LABELWIDTH,COMHEIGHT);
        textJP.add(CSHLabel);
        final JTextField CSHField = new JTextField(20);
        CSHField.setBounds(FIRST_COLUMN_loc+LABELWIDTH,FIRST_LINE_loc + COMHEIGHT + LINE_SPACE, TEXTWIDTH,COMHEIGHT);
        CSHField.setText("0");
        textJP.add(CSHField);

        JLabel CSHINTLabel= new JLabel("上期期初取现利息余额");
        CSHINTLabel.setBounds(FIRST_COLUMN_loc + LABELWIDTH + TEXTWIDTH + 6*COLUMN_SPACE ,FIRST_LINE_loc + COMHEIGHT + LINE_SPACE, LABELWIDTH,COMHEIGHT);
        textJP.add(CSHINTLabel);
        final JTextField CSHINTField = new JTextField(20);
        CSHINTField.setBounds(FIRST_COLUMN_loc + 2* LABELWIDTH + TEXTWIDTH + 7*COLUMN_SPACE ,
                FIRST_LINE_loc + COMHEIGHT + LINE_SPACE,TEXTWIDTH,COMHEIGHT);
        CSHINTField.setText("0");
        textJP.add(CSHINTField);

        JLabel MEMLabel= new JLabel("上期期初年费");
        MEMLabel.setBounds(FIRST_COLUMN_loc + 2* LABELWIDTH + 2* TEXTWIDTH + 14*COLUMN_SPACE ,
                FIRST_LINE_loc + COMHEIGHT + LINE_SPACE, LABELWIDTH,COMHEIGHT);
        textJP.add(MEMLabel);
        final JTextField MEMField = new JTextField(20);
        MEMField.setBounds(FIRST_COLUMN_loc + 2* LABELWIDTH + 2* TEXTWIDTH + 23*COLUMN_SPACE ,
                FIRST_LINE_loc + COMHEIGHT + LINE_SPACE,TEXTWIDTH,COMHEIGHT);
        MEMField.setText("0");
        textJP.add(MEMField);

        JLabel INSTLLabel= new JLabel("上期期初分期余额");
        INSTLLabel.setBounds(FIRST_COLUMN_loc,FIRST_LINE_loc + 2*COMHEIGHT + 2*LINE_SPACE, LABELWIDTH,COMHEIGHT);
        textJP.add(INSTLLabel);
        final JTextField INSTLField = new JTextField(20);
        INSTLField.setBounds(FIRST_COLUMN_loc+LABELWIDTH,FIRST_LINE_loc + 2*COMHEIGHT + 2*LINE_SPACE, TEXTWIDTH,COMHEIGHT);
        INSTLField.setText("0");
        textJP.add(INSTLField);

        JLabel INSTLINTLabel= new JLabel("上期期初分期利息余额");
        INSTLINTLabel.setBounds(FIRST_COLUMN_loc + LABELWIDTH + TEXTWIDTH + 6*COLUMN_SPACE ,
                                FIRST_LINE_loc + 2*COMHEIGHT + 2*LINE_SPACE, LABELWIDTH,COMHEIGHT);
        textJP.add(INSTLINTLabel);
        final JTextField INSTLINTField = new JTextField(20);
        INSTLINTField.setBounds(FIRST_COLUMN_loc + 2* LABELWIDTH + TEXTWIDTH + 7*COLUMN_SPACE ,
                                FIRST_LINE_loc + 2*COMHEIGHT + 2*LINE_SPACE,TEXTWIDTH,COMHEIGHT);
        INSTLINTField.setText("0");
        textJP.add(INSTLINTField);

        JLabel StartCycleLabel= new JLabel("起始账期（YYYY-MM）");
        StartCycleLabel.setBounds(FIRST_COLUMN_loc,
                                FIRST_LINE_loc + 3*COMHEIGHT + 5*LINE_SPACE, LABELWIDTH,COMHEIGHT);
        textJP.add(StartCycleLabel);
        final JTextField StartCycleField = new JTextField(20);
        StartCycleField.setBounds(FIRST_COLUMN_loc+LABELWIDTH,
                                FIRST_LINE_loc + 3*COMHEIGHT + 5*LINE_SPACE, TEXTWIDTH,COMHEIGHT);
        StartCycleField.setText("2019-03");
        textJP.add(StartCycleField);

        JLabel EndCycleLabel= new JLabel("结束账期（YYYY-MM）");
        EndCycleLabel.setBounds(FIRST_COLUMN_loc + LABELWIDTH + TEXTWIDTH + 6*COLUMN_SPACE ,
                        FIRST_LINE_loc + 3*COMHEIGHT + 5*LINE_SPACE, LABELWIDTH,COMHEIGHT);
        textJP.add(EndCycleLabel);
        final JTextField EndCycleField = new JTextField(20);
        EndCycleField.setBounds(FIRST_COLUMN_loc + 2* LABELWIDTH + TEXTWIDTH + 7*COLUMN_SPACE ,
                FIRST_LINE_loc + 3*COMHEIGHT + 5*LINE_SPACE,TEXTWIDTH,COMHEIGHT);
        EndCycleField.setText("2019-03");
        textJP.add(EndCycleField);

        JLabel CycleDayLabel= new JLabel("账单日（1~28）");
        CycleDayLabel.setBounds(FIRST_COLUMN_loc + 2* LABELWIDTH + 2* TEXTWIDTH + 14*COLUMN_SPACE ,
                FIRST_LINE_loc + 3*COMHEIGHT + 5*LINE_SPACE, LABELWIDTH,COMHEIGHT);
        textJP.add(CycleDayLabel);
        final JTextField CycleDayField = new JTextField(20);
        CycleDayField.setBounds(FIRST_COLUMN_loc + 2* LABELWIDTH + 2* TEXTWIDTH + 23*COLUMN_SPACE ,
                        FIRST_LINE_loc + 3*COMHEIGHT + 5*LINE_SPACE,TEXTWIDTH,COMHEIGHT);
        CycleDayField.setText("5");
        textJP.add(CycleDayField);

        JLabel ConfigLabel= new JLabel("冲账参数选择");
        ConfigLabel.setBounds(FIRST_COLUMN_loc,
                        FIRST_LINE_loc + 4*COMHEIGHT + 6*LINE_SPACE, LABELWIDTH,COMHEIGHT);
        textJP.add(ConfigLabel);
        final JComboBox comboBox=new JComboBox();
        comboBox.addItem("90天以内");
        comboBox.addItem("90天以上");
        comboBox.setBounds(FIRST_COLUMN_loc + LABELWIDTH   ,
                        FIRST_LINE_loc + 4*COMHEIGHT + 6*LINE_SPACE, LABELWIDTH,COMHEIGHT);
        //CycleDayField.setText("5");
        textJP.add(comboBox);


        JButton jbInt = new JButton("计算利息");
        jbInt.setBounds(225, 480, 120, 30);
        //jbInt.setBackground(Color.gray);

        JButton jbminPay = new JButton("计算最低额");
        jbminPay.setBounds(550, 480, 120, 30);
        //jbminPay.setBackground(Color.gray);

        jbInt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                double RTLBal = Double.parseDouble(RTLField.getText());
                double RTLInt = Double.parseDouble(RTLINTField.getText());
                double CSHBal = Double.parseDouble(CSHField.getText());
                double CSHInt = Double.parseDouble(CSHINTField.getText());
                double INSTLBal = Double.parseDouble(INSTLField.getText());
                double INSTLInt = Double.parseDouble(INSTLINTField.getText());
                double FEE = Double.parseDouble(FEEField.getText());
                double MEM = Double.parseDouble(MEMField.getText());

                String startCycle = StartCycleField.getText();
                String endCycle = EndCycleField.getText();
                int cycleDay = Integer.parseInt(CycleDayField.getText());
                int config = comboBox.getSelectedIndex();
                if (config > 0) config += 4;

                Date tmp = DateCompute.addMonth(DateCompute.dateForm(startCycle), -1);
                int year = DateCompute.getYear(tmp);
                int month = DateCompute.getMonth(tmp);
                Date startDate = DateCompute.addDate(DateCompute.getDate(year, month, cycleDay), 1);
                Date lastStartDate = DateCompute.addMonth(startDate, -1);
                Account account = new Account(cycleDay, startDate, 100000000.0);
                TransProcess TP = new TransProcess(account, strikeOrder, strikeOrderDispatcher);    //加载账户和冲账顺序
                DayProcess DP = new DayProcess(account, io);

                if (RTLBal > 0) {
                    TP.debitTrans(0, 0, RTLBal, lastStartDate, false, 1, "上期消费余额");
                }
                if (RTLInt > 0) {
                    TP.debitTrans(0, 1, RTLInt, lastStartDate, false, 1, "上期消费利息余额");
                }
                if (CSHBal > 0) {
                    TP.debitTrans(1, 0, CSHBal, lastStartDate, false, 1, "上期取现余额");
                }
                if (CSHInt > 0) {
                    TP.debitTrans(1, 1, CSHInt, lastStartDate, false, 1, "上期取现利息余额");
                }
                if (INSTLBal > 0) {
                    TP.debitTrans(2, 0, INSTLBal, lastStartDate, false, 1, "上期分期余额");
                }
                if (INSTLInt > 0) {
                    TP.debitTrans(2, 1, INSTLInt, lastStartDate, false, 1, "上期分期利息余额");
                }
                if (FEE > 0) {
                    TP.debitTrans(3, 1, FEE, lastStartDate, false, 1, "上期费用余额");
                }
                if (MEM > 0) {
                    TP.debitTrans(4, 1, FEE, lastStartDate, false, 1, "上期年费余额");
                }
                Dispatcher mainThread = new Dispatcher(account, trList, startCycle, endCycle, DP, TP);
                //mainThread.initBalance();
                mainThread.dispatcher();
                io.print2Excel(startCycle);
                JOptionPane.showMessageDialog(null, "计算完成", "INFORMATION_MESSAGE",JOptionPane.INFORMATION_MESSAGE);
            }
        });

        textJP.add(jbInt);
        textJP.add(jbminPay);



        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }



    public static void main(String[] args){

        SimpleFrame sf = new SimpleFrame();
        try {
            //trList = io.processTrans(io.fileRead("trades.txt"));
            sf.setStrikeOrder(sf.getIo().processConfig("./config.txt"));
            sf.setTrList(sf.getIo().readTransFromExcel("./readExcel.xls"));
        }catch (Exception e){
            e.printStackTrace();
            logger.error("读取文件失败");
        }
        for(int i=0; i<4; i++){
            sf.getStrikeOrderDispatcher().put(i,0); //90天以内
        }
        for(int i=4; i<=20; i++){
            sf.getStrikeOrderDispatcher().put(i,1); //90天以上
        }

        sf.displayWindow();

    }

    public int getCycleDay() {
        return cycleDay;
    }

    public void setCycleDay(int cycleDay) {
        this.cycleDay = cycleDay;
    }

    public String getStartCycle() {
        return startCycle;
    }

    public void setStartCycle(String startCycle) {
        this.startCycle = startCycle;
    }

    public String getEndCycle() {
        return endCycle;
    }

    public void setEndCycle(String endCycle) {
        this.endCycle = endCycle;
    }

    public IOService getIo() {
        return io;
    }

    public void setIo(IOService io) {
        this.io = io;
    }

    public Map<Integer, Integer> getStrikeOrderDispatcher() {
        return strikeOrderDispatcher;
    }

    public void setStrikeOrderDispatcher(Map<Integer, Integer> strikeOrderDispatcher) {
        this.strikeOrderDispatcher = strikeOrderDispatcher;
    }

    public List<List<Integer>> getStrikeOrder() {
        return strikeOrder;
    }

    public void setStrikeOrder(int strikeOrder) {
        StrikeOrder = strikeOrder;
    }

    public void setStrikeOrder(List<List<Integer>> strikeOrder) {
        this.strikeOrder = strikeOrder;
    }

    public Transaction[] getTrList() {
        return trList;
    }

    public void setTrList(Transaction[] trList) {
        this.trList = trList;
    }
}
