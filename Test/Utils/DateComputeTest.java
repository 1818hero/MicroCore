package test.Utils; 

import Utils.DateCompute;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.Date;

/** 
* DateCompute Tester. 
* 
* @author <Authors name> 
* @since <pre>02/12/2019</pre> 
* @version 1.0 
*/ 
public class DateComputeTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: getIntervalDays(Date startDate, Date endDate) 
* 
*/ 
@Test
public void testGetIntervalDays() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: dateForm(String s) 
* 
*/ 
@Test
public void testDateForm() throws Exception { 
//TODO: Test goes here...
    Date a = DateCompute.dateForm("2018/2/1");
    Date b = DateCompute.dateForm("2019-9-21");
    Date c = DateCompute.dateForm("20170911");
    Date d = DateCompute.dateForm("201709");
    Date e = DateCompute.dateForm("2013-9");
    Date f = DateCompute.dateForm("2013/9");
    System.out.println(a);
    System.out.println(b);
    System.out.println(c);
    System.out.println(d);
    System.out.println(e);
}

/** 
* 
* Method: reDateForm(Date d) 
* 
*/ 
@Test
public void testReDateForm() throws Exception { 
//TODO: Test goes here...
    System.out.println(DateCompute.reDateForm(DateCompute.dateForm("20180901")));
    System.out.println(DateCompute.reDateForm(DateCompute.dateForm("201809")));
    System.out.println(DateCompute.reDateForm(DateCompute.dateForm("2018/1/22")));
} 

/** 
* 
* Method: getDate(int year, int month, int day) 
* 
*/ 
@Test
public void testGetDate() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getDayOfMonth(Date date) 
* 
*/ 
@Test
public void testGetDayOfMonth() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getMonth(Date date) 
* 
*/ 
@Test
public void testGetMonth() throws Exception { 
//TODO: Test goes here...
    Date t = DateCompute.dateForm("2018-1-1");
    System.out.println(DateCompute.getYear(t));
} 

/** 
* 
* Method: getYear(Date date) 
* 
*/ 
@Test
public void testGetYear() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: addDate(Date date, int days) 
* 
*/ 
@Test
public void testAddDate() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: addMonth(Date date, int months) 
* 
*/ 
@Test
public void testAddMonth() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: judgeCycle(int cycleDay, Date transDate, Date recordDate) 
* 
*/ 
@Test
public void testJudgeCycle() throws Exception { 
//TODO: Test goes here... 
} 


} 
