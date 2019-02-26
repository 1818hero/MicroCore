import Model.Account;
import Model.TransCode;
import Model.Transaction;
import Service.DayProcess;
import Service.Dispatcher;
import Service.IOService;
import Service.TransProcess;
import Utils.DateCompute;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 程序运行流程：
 * 1. 输入计算利息和最低额的账期范围（s,e）
 * 2. 输入s-1账期的上期余额分布
 * 3. *输入s账期的上期余额分布（该步骤可通过计算首月取现利息简化）
 * 4. 输入（s-1，e）账期的账单
 * 5. 输入其它参数：额度、账单日，利率历史（暂不支持）等
 * 6. 获取结果
 * Todo 依赖log4j
 * Todo 工程分叉，建立账务系统和利息工具两个版本
 */
public class Main {
    public static void main(String[] args) {
        IOService io = new IOService();
        List<List<Integer>> strikeOrder = null;
        Transaction[] trList = null;
        try {
            //trList = io.processTrans(io.fileRead("trades.txt"));
            strikeOrder = io.processConfig("./config.txt");
            trList = io.readTransFromExcel("./readExcel.xls");
        }catch (Exception e){
            e.printStackTrace();
        }
       // System.out.println(System.getProperty("user.dir")+"\\trades.txt");
        int cycleDay = 5;       //账单日
        Date startDate = DateCompute.dateForm("2018-05-06"); //账户初始日期
        String startCycle = "2018-06";
        String endCycle = "2018-12";
        Map<Integer, Integer> strikeOrderDispatcher = new HashMap<>();
        //初始化延滞阶段对应的冲账顺序
        for(int i=0; i<4; i++){
            strikeOrderDispatcher.put(i,0); //90天以内
        }
        for(int i=4; i<=20; i++){
            strikeOrderDispatcher.put(i,1); //90天以上
        }



        Date today = DateCompute.dateForm("2018-10-06");     //今天

        //Todo 将下述代码配置文件化
        Account account = new Account(cycleDay, startDate, 100000.0);
        TransProcess TP = new TransProcess(account, strikeOrder, strikeOrderDispatcher);    //加载账户和冲账顺序
        DayProcess DP = new DayProcess(account, io);
        Dispatcher mainThread = new Dispatcher(account, trList, startCycle, endCycle, DP, TP);
        //mainThread.initBalance();
        mainThread.dispatcher();
        io.print2Excel(startCycle);
        System.out.println("完成");

    }
}
