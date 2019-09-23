package Service;

import Model.*;
import Utils.DateCompute;
import Utils.ReadExcel;
import Utils.ReadWriteExcel;
import org.apache.log4j.Logger;

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
    private static Logger logger = Logger.getLogger(IOService.class);
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
        try {
            ReadWriteExcel.write2Excel(dataList, startCol, "./利息明细.xlsx");
        }catch (Exception e){
            logger.error("写入文件失败");
        }
        outputTrans.clear();
        outputInt.clear();

    }

    public Transaction[] readTransFromExcel(String xlsPath){
        ReadExcel obj = new ReadExcel();
        File file = new File(xlsPath);
        List excelList = obj.readExcel(file);
        return processTrans(excelList); //Todo 文件检查、字符检查等等
    }

    public Transaction[] readTransFromDoc(String path){
        String content = null;
        try {
            content = fileRead(path);
            return processTrans(content);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("文件读取错误");
        }
        return null;
    }

    private String fileRead(String path) throws Exception {
        //File file = new File(System.getProperty("user.dir")+"\\trades.txt");//定义一个file对象，用来初始化FileReader
        //File file = new File(System.getProperty("user.dir")+"\\"+filename);//定义一个file对象，用来初始化FileReader
        File file = new File(path);
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
        List<List<String>> transList = new ArrayList<>();
        for(String s : trans){
            String[] oneTrans = s.split("[\\s,]");
            List<String> oneTransList = new ArrayList<>();
            for(String t : oneTrans){
                oneTransList.add(t);
            }
            transList.add(oneTransList);
        }
        return processTrans(transList);
    }

    /**
     *
     * @param trans 每一个数组元素代表一条交易的完整信息,且信息已经预处理好
     * @return
     */
    public Transaction[] processTrans(List<List<String>> trans){
        //Transaction[] res = new Transaction[trans.size()];
        List<Transaction> res = new ArrayList<>();
        try {
            for (int i = 0; i < trans.size(); i++) {
                List<String> oneLine = trans.get(i);
                if(oneLine.size()!=5){
                    logger.warn("读取交易时输入栏位数目错误，跳过此条交易");
                    continue;
                }
                Transaction tr = new Transaction();
                String TC = oneLine.get(0);
                if(!TC.matches("^[0-9]{4}$")){
                    logger.warn("TC栏位读取错误，跳过此条交易");
                    continue;
                }
                try {
                    tr.setTC(Enum.valueOf(TransCode.class, "TC" + TC));
                }catch (Exception e){
                    logger.warn("该TC未配置");
                    continue;
                }
                tr.setTransDate(DateCompute.dateForm(oneLine.get(1)));
                tr.setRecordDate(DateCompute.dateForm(oneLine.get(2)));
                String amt = oneLine.get(3);
                amt.replaceAll(" ","");
                if(!amt.matches("^\\-{0,1}[0-9]{1,}\\.{0,1}[0-9]{0,}$")){
                    logger.warn("金额栏位读取错误，跳过此条交易");
                    continue;
                }
                tr.setAmount(Double.parseDouble(amt));
                tr.setSummary(oneLine.get(4).trim());
                res.add(tr);
                //res[i] = tr;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //按入账顺序和TC将交易排序
        Collections.sort(res, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                if(DateCompute.getIntervalDays(o1.getRecordDate(),o2.getRecordDate())<0) return 1;
                else if(DateCompute.getIntervalDays(o1.getRecordDate(),o2.getRecordDate())>0)    return -1;
                else{
                    String o1direct = o1.getTC().getDirection();
                    String o2direct = o2.getTC().getDirection();
                    if (!o1direct.equals("D") && o2direct.equals("D"))   return 1;
                    else if (o1direct.equals("D") && !o2direct.equals("D")) return -1;
                    else
                        return Integer.parseInt(o1.getTC().toString().substring(2))
                                - Integer.parseInt(o2.getTC().toString().substring(2));
                }
            };
        });
        Transaction[] r = new Transaction[res.size()];
        return res.toArray(r);

    }

    /**
     * 将输入文件配置为冲账参数
     * @param path
     * @return
     */
    public List<List<Integer>> processConfig(String path){
        String config = "";
        try {
            config = fileRead(path);
        }catch (Exception e){
            e.printStackTrace();
        }
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
