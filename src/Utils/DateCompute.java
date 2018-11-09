package Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Created by Victor on 2018/10/29.
 */
public class DateCompute {
    public static int getIntervalDays(Date startDate, Date endDate) {

        if (null == startDate || null == endDate) {

            return -1;

        }

        long intervalMilli = endDate.getTime() - startDate.getTime();

        return (int) (intervalMilli / (24 * 60 * 60 * 1000));

    }
    public static Date dateForm(String s){
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(s);
        }catch (ParseException e){
            e.getErrorOffset();
        }
        return null;
    }
}
