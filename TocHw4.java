/*******************************

F74001030 陳凱揚
先將網路上的檔案存取至本地端，
接著讀入檔案，並利用RE去將XX路、XX街、XX大道找出來，如果都沒有才用XX巷的資料
將結果儲存到map中，最後取不同月份出現次數最多的
並將其最高價以及最低價顯示出來

*******************************/

import java.io.*;
import java.net.*;
import org.json.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class TocHw4 {

    public static void download(String source, String destination) throws IOException, ConnectException{
        InputStream is = new URL(source).openConnection().getInputStream();
        FileOutputStream fos = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        for (int length; (length = is.read(buffer)) > 0; fos.write(buffer, 0, length));
            fos.close();
            is.close();
        }


    public static void main(String[] args) throws IOException, JSONException{
        download(args[0],"data.json");
        
        int price = 0,num = 0;
        FileReader fin;
        fin = new FileReader("data.json");
        JSONArray Data = new JSONArray(new JSONTokener( fin ));
        Map<String, Integer> datanum = new HashMap<String, Integer>();
        Map<String, Map<Integer, Integer>> datatime = new HashMap<String, Map<Integer, Integer>>();
        Map<String, Integer> maxPrice = new HashMap<String, Integer>();
        Map<String, Integer> minPrice = new HashMap<String, Integer>();

        //System.out.println(Data.length());
        for( int i= 0; i<Data.length(); i++ )
        {
            Pattern road = Pattern.compile(".*路|.*大道|.*街");
            Pattern block = Pattern.compile(".*巷");
            Matcher match1 = road.matcher((Data.getJSONObject(i)).getString("土地區段位置或建物區門牌"));
            Matcher matchfin = block.matcher((Data.getJSONObject(i)).getString("土地區段位置或建物區門牌"));
            if(match1.find())
            {
                if(datanum.get(match1.group())==null)
                {
                    datatime.put(match1.group(), new HashMap<Integer, Integer>());
                    datanum.put(match1.group(), 1);
                    datatime.get(match1.group()).put((Data.getJSONObject(i)).getInt("交易年月"), 1);
                    maxPrice.put(match1.group(), (Data.getJSONObject(i)).getInt("總價元"));
                    minPrice.put(match1.group(), (Data.getJSONObject(i)).getInt("總價元"));
                }
                else
                {
                    if(datatime.get(match1.group()).get((Data.getJSONObject(i)).getInt("交易年月"))==null)
                    {
                        datatime.get(match1.group()).put((Data.getJSONObject(i)).getInt("交易年月"), 1);
                        datanum.put(match1.group(),datanum.get(match1.group())+1);
                        //System.out.print(datanum.get(match1.group())+"\t");
                        //System.out.println(match1.group());
                    }
                    if(maxPrice.get(match1.group()) < (Data.getJSONObject(i)).getInt("總價元"))
                        maxPrice.put(match1.group(), (Data.getJSONObject(i)).getInt("總價元"));
                    if(minPrice.get(match1.group()) > (Data.getJSONObject(i)).getInt("總價元"))
                        minPrice.put(match1.group(), (Data.getJSONObject(i)).getInt("總價元"));
                }
                //System.out.println(match1.group());
                    //System.out.println(datatime.get(match1.group()).get("a"));
                
            }
            else if(matchfin.find())
            {
                if(datanum.get(matchfin.group())==null)
                {
                    datatime.put(matchfin.group(), new HashMap<Integer, Integer>());
                    datanum.put(matchfin.group(), 1);
                    datatime.get(matchfin.group()).put((Data.getJSONObject(i)).getInt("交易年月"), 1);
                    maxPrice.put(matchfin.group(), (Data.getJSONObject(i)).getInt("總價元"));
                    minPrice.put(matchfin.group(), (Data.getJSONObject(i)).getInt("總價元"));
                }
                else
                {
                    if(datatime.get(matchfin.group()).get((Data.getJSONObject(i)).getInt("交易年月"))==null)
                    {
                        datatime.get(matchfin.group()).put((Data.getJSONObject(i)).getInt("交易年月"), 1);
                        datanum.put(matchfin.group(),datanum.get(matchfin.group())+1);
                    }
                    if(maxPrice.get(matchfin.group()) < (Data.getJSONObject(i)).getInt("總價元"))
                        maxPrice.put(matchfin.group(), (Data.getJSONObject(i)).getInt("總價元"));
                    if(minPrice.get(matchfin.group()) > (Data.getJSONObject(i)).getInt("總價元"))
                        minPrice.put(matchfin.group(), (Data.getJSONObject(i)).getInt("總價元"));
                }
            }
        }
        int max=0;
        Iterator iter = datanum.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if((int) val>max)
                max = (int) val;
            //System.out.println(key+" "+val);
        }
        iter = datanum.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if((int) val>=max)
            {
                System.out.print(key+", ");
                System.out.println("最高成交價： "+maxPrice.get(key)+", 最低成交價： "+minPrice.get(key));
            }
        }
    }
}
