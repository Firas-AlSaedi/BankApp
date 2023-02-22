/*
This is the javacode for the main menu 

We will hash passwords and store them in a datastore
 */
package Project;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
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



import java.math.BigInteger; 
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 
import javafx.scene.control.Slider;


public class stage2Controller implements Initializable {


    ObservableList<String> BankUsernames = FXCollections.observableArrayList();
    ObservableList<String> BankPasswords = FXCollections.observableArrayList();
    private ListView<String> listview;
    private ListView<String> listview1;
    private ListView<String> listview2;
    @FXML
    private TextField NameField;
    @FXML
    private TextField PasswordField;
    @FXML
    private Label MessageLabel;
    @FXML
    private Label MessageLabel1;
    @Override
   
    
    public void initialize(URL url, ResourceBundle rb) {
       // System.out.println(ContactName.get(0));
        try{
              Class.forName("com.mysql.jdbc.Driver");
              Connection con= DriverManager.getConnection("jdbc:mysql://localhost/bankusers?user=root&password=1234");
              Statement st=con.createStatement();
              ResultSet rs=st.executeQuery("Select * from bankusers");
              while(rs.next()){
                  BankUsernames.add(rs.getString("username") );
                  BankPasswords.add(rs.getString("password"));
              }
              
           
           
           }catch(Exception e){
               e.printStackTrace();
           }

           
    }    
    
    private void Close(ActionEvent event) throws Exception {
        ((Node)event.getSource()).getScene().getWindow().hide();
        FXMLLoader load=new FXMLLoader(getClass().getResource("Stocks.fxml"));
        Scene scene=new Scene(load.load());
        //Lab3Controller scene1=load.getController();
       // scene1.getinfo(ContactName, ContactNumber);
        Stage stage=new Stage();
        stage.setTitle("Address Book");
        stage.setScene(scene);
        stage.show();

    }

    
     public byte[] getSHA(String input) throws NoSuchAlgorithmException
    { 
        // Static getInstance method is called with hashing SHA 
        MessageDigest md = MessageDigest.getInstance("SHA-256"); 
  
        // digest() method called 
        // to calculate message digest of an input 
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8)); 
    }
    
    public String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation 
        BigInteger number = new BigInteger(1, hash); 
  
        // Convert message digest into hex value 
        StringBuilder hexString = new StringBuilder(number.toString(16)); 
  
        // Pad with leading zeros
        while (hexString.length() < 32) 
        { 
            hexString.insert(0, '0'); 
        } 
  
        return hexString.toString(); 
    }
    
    private boolean areFieldsEmpty(){
       return NameField.getText().equals("") || PasswordField.getText().equals("") ;
    }
    
    private String HashThePassword(String username,String password){
        String HashedPassword;
        try 
        {
        HashedPassword= toHexString( getSHA(password + username) ) ;
        }
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            System.out.println("Exception thrown for incorrect algorithm: " + e); 
            HashedPassword = "Null";
        } 
        
        return HashedPassword;
    }
    
    private String getHashedPassword(String username){
        if (BankUsernames.indexOf(username) != -1)
            return BankPasswords.get(BankUsernames.indexOf(username));
        else
            return "NoUser";
    }
    
    
    @FXML
    private void Register(ActionEvent event) {
        
        boolean usernameUsed = false;
        if(areFieldsEmpty()){
                 MessageLabel.setText("Please enter a username and a password");
                 return;
        }
        String username = NameField.getText();
        String HashedPassword = HashThePassword(username,PasswordField.getText());
       try{
              Class.forName("com.mysql.jdbc.Driver");

              Connection con= DriverManager.getConnection("jdbc:mysql://localhost/bankusers?user=root&password=1234");
              Statement pr= con.createStatement();
              ResultSet rs=pr.executeQuery("Select * from bankusers");
              while(rs.next()){
                  if (username.equalsIgnoreCase(rs.getString("username")))
                      usernameUsed=true;
              }

              if (usernameUsed)
                 MessageLabel.setText("Username already used!");
              else if(!HashedPassword.equalsIgnoreCase("Null")) {
                PreparedStatement pr2= con.prepareStatement("insert into bankusers(username,password,balance,STC,Apple,Aramco,Mobily,AlRajhi,Alinma,STC_Average,Apple_Average,Aramco_Average,Alinma_Average,AlRajhi_Average,Mobily_Average) values(?,?,0,0,0,0,0,0,0,0,0,0,0,0,0)");
                pr2.setString(1, username);
                pr2.setString(2, HashedPassword);
                pr2.execute();
                BankUsernames.add(username);
                BankPasswords.add(HashedPassword);
                MessageLabel.setText("Registerd succeflly!");
              }
 
           }catch(Exception e){
               e.printStackTrace();
           }
        
        
    }

    @FXML
    private void Login(ActionEvent event) throws Exception {
        String username = NameField.getText();
        String password = PasswordField.getText();
        if(areFieldsEmpty()){
            MessageLabel.setText("Please enter a username and a password");
            return;
        }
        else {
                if (getHashedPassword(username).equalsIgnoreCase(HashThePassword(username,password)) )   {
                    ((Node)event.getSource()).getScene().getWindow().hide();
                    FXMLLoader load=new FXMLLoader(getClass().getResource("Stocks.fxml"));
                    Scene scene=new Scene(load.load());
                    Controller scene1=load.getController();
                    scene1.GetInfo(username);
                    Stage stage=new Stage();
                    stage.setTitle("AlSaedi Bank");
                    stage.setScene(scene);
                    stage.show();
                }
                else if(getHashedPassword(username).equalsIgnoreCase("NoUser"))
                {
                    MessageLabel.setText("Username doesn't exists");
                }
                else
                {
                    MessageLabel.setText("Incorrect Password");
                }
                
                 
          }
       
        
        
  

    }
    
    
    
    
    
}
