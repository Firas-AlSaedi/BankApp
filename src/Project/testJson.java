package Project;


import java.io.*;
import java.net.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.util.Map;
import java.util.stream.IntStream;



public class testJson {
   public static String getHTML(String urlToRead  ) throws Exception {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("x-api-key", "HyyQ3JFjbd16KwOWgGHCU58hun0HA5Tu8Pon15Dr");
      conn.setRequestMethod("GET");
      try (BufferedReader reader = new BufferedReader(
                  new InputStreamReader(conn.getInputStream()))) {
          for (String line; (line = reader.readLine()) != null; ) {
              result.append(line);
          }
      }
      
      Object obj = new JSONParser().parse(result.toString());
      JSONObject jo = (JSONObject) obj;
      Map address = ((Map)jo.get("quoteResponse"));
      
      JSONArray StocksJson = (JSONArray) address.get("result");
      
      for (int i =0;i<StocksJson.size();i++){
        JSONObject StockInfo = (JSONObject) StocksJson.get(0);
        StockInfo.get("symbol");
      }
      return urlToRead;
   }
   
   
   public static void JsonManulption(String s){

       
       
   }
   public static void main(String[] args) throws Exception
   {
   }
}
