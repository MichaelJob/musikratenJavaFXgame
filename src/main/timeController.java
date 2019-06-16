package main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * timeController.java FXML Controller class for time settings scene
 *
 * @author Michael Job
 */
public class timeController implements Initializable {

    @FXML
    private Button saveBtn;
    @FXML
    private TextField tfNerdTime;
    @FXML
    private TextField tfExpertTime;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param resb
     */
    @Override
    public void initialize(URL url, ResourceBundle resb) {
        try {
            getTimesFromMain();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MusikRaten");
            alert.setHeaderText(null);
            alert.setContentText("An error occured while setting times. Cause:" + ex.getMessage());
            alert.initOwner((Stage) saveBtn.getScene().getWindow());
            alert.showAndWait();

        }
    }

    //get times from main class
    @FXML
    public void getTimesFromMain() throws IOException {
        try {
            tfNerdTime.setText(Integer.toString(Main.getNerdTime()));
            tfExpertTime.setText(Integer.toString(Main.getExpertTime()));
        } catch (Exception e) {       //error msg if all fails
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MusikRaten");
            alert.setHeaderText(null);
            alert.setContentText("An error occured while getting times. Cause:" + e.getMessage());
            alert.initOwner((Stage) saveBtn.getScene().getWindow());
            alert.showAndWait();

        }
    }

    //save times in Main class and close stage  (nerd=level 2 // expert=level 3 - in GUI Level2=freak level3=nerd)
    @FXML
    private void handleBtnSave() {
        int nerd;
        int expert;
        try {
            nerd = Integer.parseInt(tfNerdTime.getText());
            expert = Integer.parseInt(tfExpertTime.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MusikRaten");
            alert.setHeaderText(null);
            alert.setContentText("Time in seconds must be numeric. Something like 1 to 10, you'll get the point.");
            alert.initOwner((Stage) saveBtn.getScene().getWindow());
            alert.showAndWait();

            //break
            return;
        }
        if (nerd > 0 && expert > 0 && nerd < 60 && expert < 60) {  //times shall be from 1 to 59
            try {
                Main.setNerdTime(nerd);
                Main.setExpertTime(expert);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("MusikRaten");
                alert.setHeaderText(null);
                alert.setContentText("An error occured while saving times. Cause:" + e.getMessage());
                alert.initOwner((Stage) saveBtn.getScene().getWindow());
                alert.showAndWait();

            } finally {
                // get a handle to the stage
                Stage stage = (Stage) saveBtn.getScene().getWindow();
                // do what you have to do
                stage.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MusikRaten");
            alert.setHeaderText(null);
            alert.setContentText("Time in seconds should be something like 1 to 10, you'll get the point.");
            alert.initOwner((Stage) saveBtn.getScene().getWindow());
            alert.showAndWait();

        }
    }
}
