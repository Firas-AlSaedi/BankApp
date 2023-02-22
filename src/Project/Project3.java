package Project;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Project3 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Run the main menu and start it
        FXMLLoader load=new FXMLLoader(getClass().getResource("stage2.fxml"));
        Scene scene=new Scene(load.load());

        stage.setTitle("AlSaudi Bank");
        stage.setScene(scene);
        stage.show();
    }

    
    public static void main(String[] hhhh) {
         
       launch();
        

        
        

    }
    
}
