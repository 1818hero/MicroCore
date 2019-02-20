package test.Service; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

/** 
* TransProcess Tester. 
* 
* @author <Authors name> 
* @since <pre>11/28/2018</pre> 
* @version 1.0 
*/ 
public class TransProcessTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: transRoute(Transaction t, boolean isFirstCycleDay, int strikeOrderIndex, Date today) 
* 
*/ 
@Test
public void testTransRoute() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: repayment(Transaction repay, int strikeOrderIndex, Date today) 
* 
*/ 
@Test
public void testRepayment() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = TransProcess.getClass().getMethod("repayment", Transaction.class, int.class, Date.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: creditTrans(Transaction credit, int strikeOrderIndex, Date today) 
* 
*/ 
@Test
public void testCreditTrans() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = TransProcess.getClass().getMethod("creditTrans", Transaction.class, int.class, Date.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: debitTrans(Transaction debit) 
* 
*/ 
@Test
public void testDebitTrans() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = TransProcess.getClass().getMethod("debitTrans", Transaction.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: findRate(List<BalanceList> field) 
* 
*/ 
@Test
public void testFindRate() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = TransProcess.getClass().getMethod("findRate", List<BalanceList>.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: strikeAndAccr(List<BalanceList> BP_field, double amount, Transaction tr, int billout, Date today) 
* 
*/ 
@Test
public void testStrikeAndAccr() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = TransProcess.getClass().getMethod("strikeAndAccr", List<BalanceList>.class, double.class, Transaction.class, int.class, Date.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: addTracebackNode(BalanceList BL, Transaction tr, double tracebackAmount, int billout) 
* 
*/ 
@Test
public void testAddTracebackNode() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = TransProcess.getClass().getMethod("addTracebackNode", BalanceList.class, Transaction.class, double.class, int.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
