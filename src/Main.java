import Model.Account;
import Model.Transaction;
import Service.Input;
import Service.TransProcess;
import Utils.DateCompute;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
//        Input in = new Input();
//        List<Integer> strikeOrder = null;
//        Transaction[] trList = null;
//        try {
//            trList = in.processTrans(in.fileRead("trades.txt"));
//            strikeOrder = in.processConfig(in.fileRead("config.txt"));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//       // System.out.println(System.getProperty("user.dir")+"\\trades.txt");
//        int cycleDay = 5;       //账单日
//        Date startDate = DateCompute.dateForm("2018-05-06"); //账户初始日期
//        Date today = DateCompute.dateForm("2018-10-06");     //今天
//        Account account = new Account(cycleDay, startDate, today, 10000);
//        TransProcess TRservice = new TransProcess(account, strikeOrder);    //加载账户和冲账顺序
//        //TRservice.transRoute(trList);
//        System.out.println("读取完成");
        Date date = DateCompute.dateForm("2018-12-31");

        System.out.println(DateCompute.addDate(date,-2));
    }
}
