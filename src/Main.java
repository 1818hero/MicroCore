import Model.TransCode;
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
 * Todo 建立Maven工程，依赖log4j
 * Todo 工程分叉，建立账务系统和利息工具两个版本
 */
public class Main {
    public static void main(String[] args) {
//        IOService in = new IOService();
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
        System.out.println(TransCode.TC3100.toString().substring(2));
        System.out.println(Pattern.matches("\\d1..",TransCode.TC3100.toString().substring(2)));
        System.out.println(DateCompute.addDate(date,-2));
        List<StringBuilder> list = new ArrayList<>();
        StringBuilder sb1 = new StringBuilder("abc");
        StringBuilder sb2 = new StringBuilder("def");
        list.add(sb1);
        list.add(sb2);
        sb1.append("z");
        System.out.println(list.get(0));
    }
}
