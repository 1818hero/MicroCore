package Service;

import Model.*;
import Utils.DateCompute;
import Utils.WriteExcel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * 输入输出交易文件、参数配置文件
 * Created by Victor on 2018/9/15.
 */
public class IOService {

    /**
     * 交易文件、参数配置文件读取
     * @return 返回读取字符串
     * @throws Exception
     */
    private List<List<BalanceNode>> outputTrans;
    private List<Double> outputInt;

    private void wrapHead(Date cycle, List<List<String>> dataList, List<Integer> startCol){
        List<String> headCycle = new ArrayList<>();
        headCycle.add(DateCompute.reDateForm(cycle).substring(0,7));
        dataList.add(headCycle);
        startCol.add(0);
        List<String> head = new ArrayList<>();
        head.add("计息金额");
        head.add("起息日");
        head.add("止息日");
        head.add("计息天数");
        head.add("利息金额");
        head.add("摘要");
        dataList.add(head);
        startCol.add(0);
    }

    public void print2Excel(String startCycle){
        if(outputTrans.size()!=outputInt.size())    return;
        Date d = DateCompute.dateForm(startCycle);
        List<List<String>> dataList = new ArrayList<>();
        List<Integer> startCol = new ArrayList<>();
        for(int p = 0; p < outputTrans.size(); p++){    //一个cycle
            wrapHead(d, dataList, startCol);
            d = DateCompute.addMonth(d, 1);
            List<BalanceNode> detail = outputTrans.get(p);
            Collections.sort(detail, new Comparator<BalanceNode>() {
                @Override
                public int compare(BalanceNode o1, BalanceNode o2) {
                    if(DateCompute.getIntervalDays(o1.getRecordDate(),o2.getRecordDate())>0){
                        return -1;
                    }
                    else if(DateCompute.getIntervalDays(o1.getRecordDate(),o2.getRecordDate())<0){
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
            });
            for(int i = 0; i < detail.size(); i++){
                BalanceNode curNode = detail.get(i);
                List<String> oneRow = new ArrayList<>();
                oneRow.add(String.format("%.2f",curNode.getAmount()));
                oneRow.add(DateCompute.reDateForm(curNode.getStartDate()));
                oneRow.add(DateCompute.reDateForm(curNode.getEndDate()));
                oneRow.add(String.valueOf(DateCompute.getIntervalDays(curNode.getStartDate(),curNode.getEndDate())+1));
                oneRow.add(String.format("%.2f",curNode.getIntrests()));
                oneRow.add(curNode.getSummary()+curNode.getAnoSummary());
                dataList.add(oneRow);
                startCol.add(0);
            }
            List<String> res = new ArrayList<>();
            res.add(String.format("%.2f",outputInt.get(p)));
            dataList.add(res);
            startCol.add(4);
        }
        WriteExcel.write2Excel(dataList,startCol,"./利息明细.xlsx");

    }

    public String fileRead(String filename) throws Exception {
        //File file = new File(System.getProperty("user.dir")+"\\trades.txt");//定义一个file对象，用来初始化FileReader
        File file = new File(System.getProperty("user.dir")+"\\"+filename);//定义一个file对象，用来初始化FileReader
        FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
        BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
        StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
        String s = "";
        while ((s =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
            sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
            System.out.println(s);
        }
        bReader.close();
        return sb.toString();
    }


    /**
     * 将输入文件处理成交易对象
     * @param transLog
     * @return
     */
    public Transaction[] processTrans(String transLog){
        String[] trans = transLog.split("\n");
        Transaction[] res = new Transaction[trans.length];
        try {
            for (int i = 0; i < trans.length; i++) {
                Transaction tr = new Transaction();
                String[] trInfo = trans[i].split(",");
                //String TCStr = "TC" + trInfo[0] ;
                //TransCode TC = Enum.valueOf(TransCode.class, TCStr);
                //System.out.println(TC.toString());
                tr.setTC( Enum.valueOf(TransCode.class, "TC" + trInfo[0]));
                tr.setTransDate(new SimpleDateFormat("yyyy-MM-dd").parse(trInfo[1]));
                tr.setRecordDate(new SimpleDateFormat("yyyy-MM-dd").parse(trInfo[2]));
                tr.setAmount(Double.parseDouble(trInfo[3]));
                tr.setSummary(trInfo[4]);
                res[i] = tr;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //按入账顺序和TC将交易排序
        Arrays.sort(res, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                if(DateCompute.getIntervalDays(o1.getRecordDate(),o2.getRecordDate())<0) return 1;
                else if(DateCompute.getIntervalDays(o1.getRecordDate(),o2.getRecordDate())>0)    return -1;
                else{
                    String o1direct = o1.getTC().getDirection();
                    String o2direct = o2.getTC().getDirection();
                    if (o1direct.equals("D") && !o2direct.equals("D"))   return 1;
                    else if (!o1direct.equals("D") && o2direct.equals("D")) return -1;
                    else
                    return Integer.parseInt(o1.getTC().toString().substring(2))
                            - Integer.parseInt(o2.getTC().toString().substring(2));
                }
            };
        });
        return res;
    }

    /**
     * 将输入文件配置为冲账参数
     * @param config
     * @return
     */
    public List<List<Integer>> processConfig(String config){
        String[] tmp = config.split("===\n");
        List<List<Integer>> res = new ArrayList<>();
        for(String s : tmp){
            List<Integer> position = new ArrayList<>();
            s = s.replace("\n"," ");
            String[] tmp2 = s.split(" ");
            for(String pos : tmp2){
                position.add(new Integer(pos));
            }
            res.add(position);
        }
        return res;
    }

    public List<List<BalanceNode>> getOutputTrans() {
        return outputTrans;
    }

    public void setOutputTrans(List<List<BalanceNode>> outputTrans) {
        this.outputTrans = outputTrans;
    }

    public List<Double> getOutputInt() {
        return outputInt;
    }

    public void setOutputInt(List<Double> outputInt) {
        this.outputInt = outputInt;
    }


    public IOService(){
        this.outputTrans = new ArrayList<>();
        this.outputInt = new ArrayList<>();
    }

}
