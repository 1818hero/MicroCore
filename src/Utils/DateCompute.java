package Utils;

import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期相关的工具类
 * Created by Victor on 2018/10/29.
 */
public class DateCompute {  //Todo 什么情况下class可以用private修饰
    private static Calendar cal = Calendar.getInstance();

    /**
     * 获取两个日期之间的间隔天数
     */
    public static int getIntervalDays(Date startDate, Date endDate) {

        if (null == startDate || null == endDate) {

            return -1;

        }

        long intervalMilli = endDate.getTime() - startDate.getTime();

        return (int) (intervalMilli / (24 * 60 * 60 * 1000));

    }

    /**
     * 按xxxx-xx-xx形式输入时间，返回Date
     * @param s
     * @return
     */
    @Nullable
    public static Date dateForm(String s){
        int type = s.split("-").length;
        try {
            if(type==3) return new SimpleDateFormat("yyyy-MM-dd").parse(s);
            if(type==2) return new SimpleDateFormat("yyyy-MM").parse(s);
        }catch (ParseException e){
            e.getErrorOffset();
        }
        return null;
    }
    /**
     * 输入Date，按xxxx-xx-xx形式输出时间
     * @param d
     * @return
     */
    public static String reDateForm(Date d){
        //String[] str = d.toString()
        // Todo 搞清楚toInstant()的用法
        return DateCompute.addDate(d,1).toInstant().toString().substring(0,10);
    }

    /**
     * 按年月日获取Date
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date getDate(int year, int month, int day){
        cal.set(year, month, day);
        return cal.getTime();
    }


    /**
     * 获取这个date中的“日”
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date){
        cal.setTime(date);
        return cal.get(Calendar.DATE);
    }

    /**
     * 获取这个date中的“月”
     * @param date
     * @return
     */
    public static int getMonth(Date date){
        cal.setTime(date);
        return cal.get(Calendar.MONTH)+1;
    }

    /**
     * 获取这个date中的“年”
     * @param date
     * @return
     */
    public static int getYear(Date date){
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }


    /**
     * 使该date增加天数
     */
    public static Date addDate(Date date, int days){
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    /**
     * 使该date增加月份
     */
    public static Date addMonth(Date date, int months){
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * Todo 未考虑改cycle的情况
     * 判断原交易日在哪个账单周期
     * @param cycleDay  账单日cycle
     * @param transDate 原交易日
     * @param recordDate 入账日
     * @return
     * -1: 原交易日大于入账日
     * 0：原交易日在当期
     * 1：原交易日在上期
     * 2：原交易日在两周期前
     */

    public static int judgeCycle(int cycleDay, Date transDate, Date recordDate){
        if(DateCompute.getIntervalDays(transDate, recordDate) < 0)  return -1;
        Date lastCycleDay = DateCompute.getDate(DateCompute.getYear(recordDate),DateCompute.getMonth(recordDate),cycleDay);
        if(DateCompute.getIntervalDays(lastCycleDay, recordDate) <= 0){
            lastCycleDay = DateCompute.addMonth(lastCycleDay, -1);
        }
        if(DateCompute.getIntervalDays(lastCycleDay, transDate) > 0 )   return 0;
        Date theCycleBeforeLastCycleDay = DateCompute.addMonth(lastCycleDay, -1);
        if(DateCompute.getIntervalDays(theCycleBeforeLastCycleDay, transDate) > 0 ) return 1;
        else return 2;
    }
}
