package main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

/**
 * genreController.java FXML Controller class for genre settings scene
 *
 * @author Michael Job
 */
public class genreController implements Initializable {

    @FXML
    private Button saveBtn;
    @FXML
    private Slider genreSlider;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param resb
     */
    @Override
    public void initialize(URL url, ResourceBundle resb) {
        try {
            getGenreFromMain();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MusikRaten init");
            alert.setHeaderText(null);
            alert.setContentText("An error occured while setting genres. Cause:" + ex.getMessage());
            alert.initOwner((Stage) saveBtn.getScene().getWindow());
            alert.showAndWait();
        }
    }

    //get genre from main class
    @FXML
    public void getGenreFromMain() throws IOException {
        try {
            genreSlider.setValue(Main.getGenreChooser());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MusikRaten getGenreFromMain");
            alert.setHeaderText(null);
            alert.setContentText("An error occured while getting genre. Cause:" + e.getMessage());
            alert.initOwner((Stage) saveBtn.getScene().getWindow());
            alert.showAndWait();
        }
    }

    //save genre in Main class and close stage
    @FXML
    private void handleBtnSave() {
        int genreValue;
        //getValuefromSliderGuitar
        genreValue = (int) genreSlider.getValue();
        //check if changed settings
        if (Main.getGenreChooser() != genreValue) {
            //set new genreValuetoMain.class
            Main.setGenreChooser(genreValue);
            //call createGameSet again if value changed (to get the genres)
            Platform.runLater(() -> {
                SongManager.createGameSet();
            });
            //close this window
            closeStage();
        } else {
            closeStage();
        }

    }

    //close stage
    private void closeStage() {
        // get a handle to the stage
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }

}
