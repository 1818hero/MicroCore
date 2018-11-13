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
public class DateCompute {
    private static Calendar cal = Calendar.getInstance();

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
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(s);
        }catch (ParseException e){
            e.getErrorOffset();
        }
        return null;
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
        return cal.get(Calendar.MONTH);
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
     * 判断原交易日在哪个账单周期
     * @param cycleDay  账单日cycle
     * @param transDate 原交易日
     * @param recordDate 入账日
     * @return
     * 0：原交易日在当期
     * 1：原交易日在上期
     * 2：原交易日在两周期前
     */

    public static int judgeCycle(int cycleDay, Date transDate, Date recordDate){
        Date lastCycleDay = DateCompute.getDate(DateCompute.getYear(recordDate),DateCompute.getMonth(recordDate),cycleDay);
        if(DateCompute.getIntervalDays(lastCycleDay, recordDate) < 0){
            lastCycleDay = DateCompute.addMonth(lastCycleDay, -1);
        }
        if(DateCompute.getIntervalDays(lastCycleDay, transDate) > 0 )   return 0;
        Date theCycleBeforeLastCycleDay = DateCompute.addMonth(lastCycleDay, -1);
        if(DateCompute.getIntervalDays(theCycleBeforeLastCycleDay, transDate) > 0 ) return 1;
        else return 2;
    }
}
