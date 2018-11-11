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
     * 获取这个date中的“日”
     * @param date
     * @return
     */
    public static int getDate(Date date){
        cal.setTime(date);
        return cal.get(Calendar.DATE);
    }
    /**
     * 使该date增加天数
     */
    public static Date addDate(Date date, int days){
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }
}
