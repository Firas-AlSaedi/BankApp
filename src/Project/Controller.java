/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DecimalFormat;

/**
 * FXML Controller class
 *
 */
public class Controller  implements Initializable  {
    // API Request !
    String TheURL = "https://yfapi.net/v6/finance/quote?region=US&lang=en&symbols=AAPL%2C7010.SR%2C7020.SR%2C1120.SR%2C1150.SR%2C2222.SR";
    Connection con;
    String username;
    HashMap<String,String> SymbolsToStocks = new HashMap<>();
    HashMap<String,Number> NamesToPrices = new HashMap<>();
    ObservableList<String> TheStocks =FXCollections.observableArrayList();
    ObservableList<String> YourStocksList =FXCollections.observableArrayList();
    private ListView<String> Mobilelist;
    @FXML
    private Label Errorlabel;
    Boolean check= false;
    @FXML
    private ListView<String> YourStocks;
    @FXML
    private TextField Amount;
    @FXML
    private ListView<String> StockMarket;
    @FXML
    private Label Balance;
    @FXML
    private Label Networth;
    @FXML
    private Label StocksValue;
    @FXML
    private TextField UserToTransfer;
    @FXML
    private Label AccountNumberLabel;
    @FXML
    private Label UsernameLabel;
    
    private double StocksValuation=0;
    @FXML
    private Label StocksCost2;
    
    
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SymbolsToStocks.put("AAPL", "Apple");
        SymbolsToStocks.put("7010.SR", "STC");
        SymbolsToStocks.put("7020.SR", "Mobily");
        SymbolsToStocks.put("1120.SR", "AlRajhi");
        SymbolsToStocks.put("1150.SR", "Alinma");
        SymbolsToStocks.put("2222.SR", "Aramco");
        try { 
            Class.forName("com.mysql.jdbc.Driver");
            con= DriverManager.getConnection("jdbc:mysql://localhost/bankusers?user=root&password=1234");
            UpdatePrices();
        } catch (Exception e) {
        }

        YourStocks.setItems(YourStocksList);

           
    }
     
   
    
    public void UpdatePrices() {
        try {
            StringBuilder result = new StringBuilder();
            
            try {
                URL url = new URL(TheURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // CHANGE APIKEY To your APIKEY , Get Your API Key from Yahoo Finance
                conn.setRequestProperty("x-api-key", "APIKEY");
                conn.setRequestMethod("GET");
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    for (String line; (line = reader.readLine()) != null; ) {
                        result.append(line);
                    }
                }
                
                
            }
            catch (Exception ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Handling Json Request 
            Object obj = new JSONParser().parse(result.toString());        
            JSONObject jo = (JSONObject) obj;
            Map address = ((Map)jo.get("quoteResponse"));
            
            JSONArray StocksJson = (JSONArray) address.get("result");
            TheStocks.clear();
            NamesToPrices.clear();
            for (int i =0;i<StocksJson.size();i++){
                JSONObject StockInfo = (JSONObject) StocksJson.get(i);
                TheStocks.add(SymbolsToStocks.get(StockInfo.get("symbol"))+":"+StockInfo.get("regularMarketPrice"));
                NamesToPrices.put((String) StockInfo.get("symbol"), (Number) StockInfo.get("regularMarketPrice"));
            }
            StockMarket.setItems(TheStocks);

            
            for (int i=0;i<NamesToPrices.size();i++){
                
            }
        }
        catch (ParseException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        GetInfo(this.username);
        
        
        
    }
    @FXML
    private void Logout(ActionEvent event) throws Exception {
        ((Node)event.getSource()).getScene().getWindow().hide();
        FXMLLoader load=new FXMLLoader(getClass().getResource("stage2.fxml"));
        Scene scene=new Scene(load.load());
        Stage stage=new Stage();
        stage.setTitle("AlSaedi Bank");
        stage.setScene(scene);
        stage.show();
    
    }
    
    
    public void SubtractMoney(String Amount){
           if( OwnAmount(Amount) ) {
            try {
                 PreparedStatement pr=con.prepareStatement("update bankusers set balance = balance-? where username = ?");
                 pr.setString(1, Amount);
                 pr.setString(2, this.username);
                 pr.execute();
                 GetInfo(this.username);


             } catch (SQLException ex) {
                 Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
             }
           }  
    }
    
    public void AddAmount(String Amount){
            try {
                 PreparedStatement pr=con.prepareStatement("update bankusers set balance = balance+? where username = ?");
                 pr.setString(1, Amount);
                 pr.setString(2, this.username);
                 pr.execute();
                 GetInfo(this.username);


             } catch (SQLException ex) {
                 Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
             }

    }
    
    
    @FXML
    private void BuyStock(ActionEvent event) {
        String[] StockInfo = StockMarket.getSelectionModel().getSelectedItem().split(":");
        String StockName = StockInfo[0];
        String Price = StockInfo[1];
        if (OwnAmount(Price)){
            try {
                SubtractMoney(Price);
                PreparedStatement pr2=con.prepareStatement("update bankusers set "+StockName+" = "+StockName+"+1 where username = ?" );
                pr2.setString(1,this.username);
                pr2.execute();
                
                PreparedStatement pr = con.prepareStatement("select * from bankusers where username = ?" );
                pr.setString(1, this.username);
                ResultSet rs = pr.executeQuery();
                rs.next();
                double TheAverageS = ( rs.getDouble(StockName+"_Average")*( rs.getInt(StockName)-1 ) + Double.parseDouble(Price) )/ ( rs.getInt(StockName) ) ;
                
                PreparedStatement pr3=con.prepareStatement("update bankusers set "+StockName+"_Average"+" = ? where username = ?" );
                pr3.setString(2,this.username);
                pr3.setDouble(1,TheAverageS);
                
                pr3.execute();
                
                Errorlabel.setText("Bought \""+StockName +"\" for : "+GetStockPrice(StockName));
                Errorlabel.setTextFill(Color.GREEN);
                

            } catch (SQLException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            GetInfo(this.username);
        }
    }
    
    private String GetStockPrice(String TheStockName){
        
        for (String i: StockMarket.getItems() ){
            String Name = i.split(":")[0];
            String Price = i.split(":")[1];
            
            
            if (Name.equalsIgnoreCase(TheStockName)){
                return Price;
            }
        }
        
        return "0";
    }
    
    
    @FXML
    private void SellStock(ActionEvent event) {
        String[] StockInfo = YourStocks.getSelectionModel().getSelectedItem().split(":");
        String StockName = StockInfo[0];
        String Howmany = StockInfo[1];
        if (Integer.parseInt(Howmany) >= 1 ){
            try {
                AddAmount(GetStockPrice(StockName));
                PreparedStatement pr2=con.prepareStatement("update bankusers set "+StockName+" = "+StockName+"-1 where username = ?" );
                pr2.setString(1,this.username);
                pr2.execute();
                Errorlabel.setText("Sold \""+StockName +"\" for : "+GetStockPrice(StockName));
                Errorlabel.setTextFill(Color.GREEN);

            } catch (SQLException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            GetInfo(this.username);
        }
    }

    
    private boolean OwnAmount(){
        return OwnAmount(Amount.getText());
    }
    
    private boolean OwnAmount(String m){
        if (Double.parseDouble(Balance.getText().split(":")[1]) >= Double.parseDouble(m)){
            
        }
        else{
            Errorlabel.setText("You dont have enough money");
            Errorlabel.setTextFill(Color.RED);
        }
        return Double.parseDouble(Balance.getText().split(":")[1]) >= Double.parseDouble(m);
    }
    
    
    @FXML
    private void Deposit(ActionEvent event) {
        if (isNumeric(Amount.getText())){
            AddAmount(Amount.getText());
            GetInfo(this.username);

            Errorlabel.setText(Amount.getText() +"$ where added into your account");
            Errorlabel.setTextFill(Color.GREEN);
        } 
        else{
            Errorlabel.setText("Please enter a number");
            Errorlabel.setTextFill(Color.RED);
        }
    }
    
    
    
    @FXML
    private void Tansfer(ActionEvent event) {
        if (isNumeric(Amount.getText())){
            if( OwnAmount() ) {
                boolean usernameUsed = false;

            try {
                
                Statement pr= con.createStatement();
                 ResultSet rs=pr.executeQuery("Select * from bankusers");
                 while(rs.next()){
                  if (UserToTransfer.getText().equalsIgnoreCase(rs.getString("username")))
                      usernameUsed=true;
                  }
                 
                 if (usernameUsed){
                    SubtractMoney(Amount.getText());

                   PreparedStatement pr2=con.prepareStatement("update bankusers set balance = balance+? where username = ?" );
                   pr2.setString(1, Amount.getText());
                   pr2.setString(2,UserToTransfer.getText());
                   pr2.execute();

                   Errorlabel.setText(Amount.getText() +"$ transferd to "+UserToTransfer.getText());
                   Errorlabel.setTextFill(Color.GREEN);

                   GetInfo(this.username);
                 }
                 else {
                    Errorlabel.setText("Username was not found!");
                    Errorlabel.setTextFill(Color.RED);
                 }
                
                


             } catch (SQLException ex) {
                 Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
             }
           }  
        }
        else{
            Errorlabel.setText("Please enter a number");
            Errorlabel.setTextFill(Color.RED);
        }
    }
    
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
       }
    
    @FXML
    private void Withdraw(ActionEvent event) {
        if (isNumeric(Amount.getText())){
            SubtractMoney(Amount.getText());
        }
    }

    @FXML
    private void RefreshPrices(ActionEvent event) {
        UpdatePrices();
    }
    
    
    public void GetInfo(String username){
        try {
            this.username = username;
            UsernameLabel.setText("Account Username: "+username);
            PreparedStatement pr = con.prepareStatement("select * from bankusers where username = ?" );
            pr.setString(1, this.username);
            ResultSet rs = pr.executeQuery();
            rs.next();
            Balance.setText("Balance:"+rs.getString("balance"));
            AccountNumberLabel.setText("Account Number:"+rs.getString("id"));
            
            YourStocksList.clear();
            YourStocksList.add("Apple:"+rs.getString("Apple"));
            YourStocksList.add("STC:"+rs.getString("STC"));
            YourStocksList.add("Mobily:"+rs.getString("Mobily"));
            YourStocksList.add("AlRajhi:"+rs.getString("AlRajhi"));
            YourStocksList.add("Alinma:"+rs.getString("Alinma"));
            YourStocksList.add("Aramco:"+rs.getString("Aramco"));
            YourStocks.setItems(YourStocksList);
            
            double StocksValue1 = 0;
            double StocksCost1 = 0;
            int Counter = 5;
            
            for(int i=0;i < StockMarket.getItems().size();i++){
                StocksValue1 = StocksValue1 + Double.parseDouble(StockMarket.getItems().get(i).split(":")[1]) * Double.parseDouble(YourStocksList.get(i).split(":")[1]);
            }
            
            for(int i=0;i < StockMarket.getItems().size();i++){
                StocksCost1 = StocksCost1 + rs.getDouble(StockMarket.getItems().get(i).split(":")[0]+"_Average") * Double.parseDouble( YourStocksList.get(i).split(":")[1] );
            }
            
 
         DecimalFormat  df = new DecimalFormat ("0.00");
         double TheNetWorth = Integer.parseInt(Balance.getText().split(":")[1]) + StocksValue1;
         StocksValue.setText("Stocks Value:"+df.format(StocksValue1));
         Networth.setText("Networth:"+ df.format(TheNetWorth));
         StocksCost2.setText("Stocks Cost:"+ df.format(StocksCost1));
   
        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       
       
    }
    

   
}
