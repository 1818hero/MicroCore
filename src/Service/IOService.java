package Service;

import Model.*;
import Utils.DateCompute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
                tr.setTC(Enum.valueOf(TransCode.class, "TC" + trInfo[0]));
                tr.setTransDate(new SimpleDateFormat("yyyy-MM-dd").parse(trInfo[1]));
                tr.setRecordDate(new SimpleDateFormat("yyyy-MM-dd").parse(trInfo[2]));
                tr.setAmount(Double.parseDouble(trInfo[3]));
                tr.setSummary(trInfo[4]);
                res[i] = tr;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //按入账顺序将交易排序
        Arrays.sort(res, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                if(DateCompute.getIntervalDays(o1.getRecordDate(),o2.getRecordDate())<0) return 1;
                else if(DateCompute.getIntervalDays(o1.getRecordDate(),o2.getRecordDate())>0)    return -1;
                else return 0;
            };
        });
        return res;
    }

    /**
     * 将输入文件配置为冲账参数
     * @param config
     * @return
     */
    public List<Integer> processConfig(String config){
        String[] tmp = config.split("\n");
        List<Integer> res = new ArrayList<>();
        for(String s : tmp){
            String[] tmp2 = s.split(" ");
            res.add(new Integer(tmp2[0]));
            res.add(new Integer(tmp2[1]));
        }
        return res;
    }
}
