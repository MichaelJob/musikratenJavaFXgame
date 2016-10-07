package main;



import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author mjair
 */
public class PlayerController implements Initializable {

    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField name1;
    @FXML
    private TextField name2;
    @FXML
    private TextField name3;
    @FXML
    private TextField name4;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param resb
     */
    @Override
    public void initialize(URL url, ResourceBundle resb) {
        try {
            setPlayerFromFile();
        } catch (IOException ex) {
            Dialogs.create()
                    .title("Error Blindtest - MusikRaten")
                    .masthead(null)
                    .message("An error occured while setting player names. Cause:" + ex.getMessage())
                    .showInformation();
        }
    }

    //get player names from main class
    @FXML
    public void setPlayerFromFile() throws IOException {
        try {
            name1.setText(Main.pl1.getName());
            name2.setText(Main.pl2.getName());
            name3.setText(Main.pl3.getName());
            name4.setText(Main.pl4.getName());
        } catch (Exception e) {       //error msg if all fails
            Dialogs.create()
                    .title("Error Blindtest - MusikRaten")
                    .masthead(null)
                    .message("An error occured while setting player names. Cause:" + e.getMessage())
                    .showInformation();
        }
    }

    //close stage without saving something
    @FXML
    private void handleBtnCancel() {
        // get a handle to the stage
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    //save names to player in Main class and close stage
    @FXML
    private void handleBtnSave() {
        //write names in file   
        if (name1.getLength() > 0 && name2.getLength() > 0 && name3.getLength() > 0 && name4.getLength() > 0) {  //player names shall not be null
            try {
                Main.pl1.setName(name1.getText());
                Main.pl2.setName(name2.getText());
                Main.pl3.setName(name3.getText());
                Main.pl4.setName(name4.getText());
            } catch (Exception e) {
                Dialogs.create()
                        .title("Error Blindtest - MusikRaten")
                        .masthead(null)
                        .message("An error occured while saving player names. Cause:" + e.getMessage())
                        .showInformation();
            } finally {
                // get a handle to the stage
                Stage stage = (Stage) saveBtn.getScene().getWindow();
                // do what you have to do
                stage.close();
            }
        } else {
            //inform user if one or more fields are left blank
            Dialogs.create()
                    .title("Error Blindtest - MusikRaten")
                    .masthead(null)
                    .message("Player names can not be null!")
                    .showInformation();
        }
    }

}
