package main;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author mjair
 */
public class HighScoreController implements Initializable {
        
    @FXML
    private Button bClose;
    @FXML
    private Label lblHighScore;
    @FXML
    private Label textAreaHighScore;
    /**
     * Initializes the controller class.
     * @param url
     * @param resb
     */
    @Override
    public void initialize(URL url, ResourceBundle resb) {
        try {
            setHighScoreFromFile();
        } catch (IOException ex) {
            Dialogs.create()
                .title("Error Blindtest - MusikRaten")
                .masthead(null)
                .message("An error occured while showing HighScores. Cause:"+ex.getMessage())
                .showInformation(); 
        }
    }    
    
    
    //show startfxml again
    @FXML
    public void handleBClose(ActionEvent event) throws Exception{
        try{
            MusicNavigator.loadVista(MusicNavigator.STARTFXML);
        } catch (Exception e) {
             Dialogs.create()
                .title("Error Blindtest - MusikRaten")
                .masthead(null)
                .message("Oupps! An error occured while closing High Scores. Cause:"+e.getMessage())
                .showInformation();
        }
    }
    
    
    //set player from Main class
    @FXML
    public void setHighScoreFromFile() throws IOException {
        String all ="";
        for (int i=0;i<4;i++){
            all=all+Main.highScore.get(i).toString()+'\n';
        }
        textAreaHighScore.setText(all);
    }
}
