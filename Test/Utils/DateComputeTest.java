package Utils;

import org.junit.Assert;

import static org.junit.Assert.*;

/**
 * Created by Victor on 2018/11/12.
 */
public class DateComputeTest {
    @org.junit.Test
    public void judgeCycle() throws Exception {
        Assert.assertEquals(DateCompute.judgeCycle(5,DateCompute.dateForm("2018-9-23"),DateCompute.dateForm("2018-11-8")), 2);
    }

}