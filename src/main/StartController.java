package main;

import java.io.File;
import java.io.PrintWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * @author mjair
 */
public class StartController {

    //Playernames
    @FXML
    private Label lblPlayer1;
    @FXML
    private Label lblPlayer2;
    @FXML
    private Label lblPlayer3;
    @FXML
    private Label lblPlayer4;
    //Auswahl Player 1 (left)
    @FXML
    private VBox vBoxLeft;
    @FXML
    ToggleGroup left;
    @FXML
    private RadioButton rbl1;
    @FXML
    private RadioButton rbl2;
    @FXML
    private RadioButton rbl3;
    @FXML
    private RadioButton rbl4;
    //Auswahl Player 2 (right)
    @FXML
    private VBox vBoxRight;
    @FXML
    ToggleGroup right;
    @FXML
    private RadioButton rbr1;
    @FXML
    private RadioButton rbr2;
    @FXML
    private RadioButton rbr3;
    @FXML
    private RadioButton rbr4;
    //other FXML elements
    @FXML
    private ImageView footerImg;
    @FXML
    private Label lblDir;  // label for Music files path
    @FXML
    Button BtnExit;
    //A toggle button with a text caption and an icon
    Image image1 = new Image(getClass().getResourceAsStream("/guisrc/player1.png"));
    Image image2 = new Image(getClass().getResourceAsStream("/guisrc/player2.png"));
    @FXML
    ToggleButton bt2Player = new ToggleButton("1 Player", new ImageView(image1));

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        //some more style to the togglebutton
        bt2Player.setGraphic(new ImageView(image1));
        bt2Player.setStyle("-fx-base: salmon;");
        //get state of toggleButton 1 or 2 Player from main.java   //on startup default is 1 Player - after playing in 2 Player this will keep 2 Player mode set
        if (Main.isPlayerMode2()) {  // 2 Player Mode
            bt2Player.setSelected(true);    //set ToggleButton pressed
            handlebt2Player();              //make changes for 2 Player mode
        } else {  // 1 Player Mode
            bt2Player.setSelected(false);   //set ToggleButton unpressed
            handlebt2Player();              //make changes for 1 Player mode
        }
        try {
            setPlayerNames();               //write names from player of main class into labels
            // getMusicPath();         //debugging
        } catch (Exception ex) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Error Blindtest - MusikRaten");
            dialog.setContentText("An error occured Cause:" + ex.getMessage());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    //set other player toggle if same is selected
    public void changeRightPlayer() {
        if (rbl1.isSelected() && rbr1.isSelected()) {
            rbr2.setSelected(true);
        } else if (rbl2.isSelected() && rbr2.isSelected()) {
            rbr1.setSelected(true);
        } else if (rbl3.isSelected() && rbr3.isSelected()) {
            rbr1.setSelected(true);
        } else if (rbl4.isSelected() && rbr4.isSelected()) {
            rbr1.setSelected(true);
        }
    }

    //set other player toggle if same is selected
    public void changeLeftPlayer() {
        if (rbl1.isSelected() && rbr1.isSelected()) {
            rbl2.setSelected(true);
        } else if (rbl2.isSelected() && rbr2.isSelected()) {
            rbl1.setSelected(true);
        } else if (rbl3.isSelected() && rbr3.isSelected()) {
            rbl1.setSelected(true);
        } else if (rbl4.isSelected() && rbr4.isSelected()) {
            rbl1.setSelected(true);
        }
    }

    //save selected Playernames to MainController
    public void saveSelectedPlayerNames() {
        if (rbl1.isSelected()) {
            Main.setPlayerLeft(Main.pl1);
        } else if (rbl2.isSelected()) {
            Main.setPlayerLeft(Main.pl2);
        } else if (rbl3.isSelected()) {
            Main.setPlayerLeft(Main.pl3);
        } else if (rbl4.isSelected()) {
            Main.setPlayerLeft(Main.pl4);
        }
        if (rbr1.isSelected()) {
            Main.setPlayerRight(Main.pl1);
        } else if (rbr2.isSelected()) {
            Main.setPlayerRight(Main.pl2);
        } else if (rbr3.isSelected()) {
            Main.setPlayerRight(Main.pl3);
        } else if (rbr4.isSelected()) {
            Main.setPlayerRight(Main.pl4);
        }
    }

    //show or hide path
    @FXML
    private void handleImageViewAction(MouseEvent e) {
        if (lblDir.isVisible() == true) {
            lblDir.setVisible(false);
        } else {
            lblDir.setVisible(true);
        }
    }

    //change Playermode 1 or 2 - Togglebutton
    @FXML
    private void handlebt2Player() {
        if (bt2Player.isSelected()) {
            //ToggleButton selected = 2 Playermode
            Main.setPlayerMode2();
            bt2Player.setText("2 Player");
            bt2Player.setGraphic(new ImageView(image2));
            vBoxRight.setVisible(true);
        } else {
            //default - ToggleButton NOT selected = 1 Playermode
            Main.setPlayerMode1();
            bt2Player.setText("1 Player");
            bt2Player.setGraphic(new ImageView(image1));
            vBoxRight.setVisible(false);
        }

    }

    //close application
    @FXML
    private void handleBtnExit() {
        // get a handle to the stage
        Stage stage = (Stage) BtnExit.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    //method for calling HighScoresfxml
    @FXML
    private void handleMenuHighScore(ActionEvent event) throws Exception {
        try {
            MusicNavigator.loadVista(MusicNavigator.HIGHSCORESFXML);
        } catch (Exception e) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Error Blindtest - MusikRaten");
            dialog.setContentText("Bummer! An error occured while opening High Scores. Cause:" + e.getMessage());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    //set path to folder containing mp3 files and write it into label in start and write it into file
    @FXML
    private void handleMenuMusicFolder() {
        try {
            Stage stage = (Stage) BtnExit.getScene().getWindow();
            DirectoryChooser dc = new DirectoryChooser();
            File selectedDirectory = dc.showDialog(stage);
            if (Main.getDir().equals(selectedDirectory.getAbsolutePath())) {
                //still the same selected - do nothing
                return;
            } else {
                if (selectedDirectory == null) {
                    //lblDir.setText("No Directory selected"); for Debugging only
                } else {
                    Main.setDir(selectedDirectory.getAbsolutePath());
                    //lblDir.setText(Main.getDir()); 
                    //walk through dir again
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setTitle("Error Blindtest - MusikRaten");
                    dialog.setContentText("A new music path folder is set. It can take a while to search&seek all your mp3 Files. Be patient");
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                    dialog.showAndWait();
                    SongManager.walk(Main.getDir());
                    SongManager.createSongList();
                    SongManager.createGameSet();
                }

            }
            //write path in file
            try {
                PrintWriter out = new PrintWriter("musicpath.txt");
                out.print(Main.getDir());
                out.close();
            } catch (Exception e) {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Error Blindtest - MusikRaten");
                dialog.setContentText("An error occured while saving music path. Cause:" + e.getMessage());
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                dialog.showAndWait();
            }
        } catch (Exception e) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Error Blindtest - MusikRaten");
            dialog.setContentText("An error occured in the DirectoryChooser. Try again. Cause:" + e.getMessage());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    //about info dialog
    @FXML
    private void handleButtonAboutAction(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("About Blindtest - MusikRaten");
        dialog.setContentText("This is a music guessing game. Be as quick as you can, if you want to compede in real life. Build with NetBeans, Java8, JavaFX. Michael Job 4.2015 - ported to Java12 JavaFX12 6.2019");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();
    }

    //open window to set player names
    @FXML
    private void openPlayerNames(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/main/Player.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.setTitle("Blindtest - MusikRaten - Set Player names...");
            stage.showAndWait();

        } catch (Exception e) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Error Blindtest - MusikRaten");
            dialog.setContentText("An error occured while opening Player names. Cause:" + e.getMessage());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
            System.out.print(e.toString());
        }
    }

    //method for calling fxmlgame loader
    @FXML
    private void startGame(ActionEvent event) throws Exception {
        saveSelectedPlayerNames();
        try {
            if (bt2Player.isSelected()) {
                //ToggleButton selected = 2 Playermode
                MusicNavigator.loadVista(MusicNavigator.GAME2FXML);
            } else {
                //ToggleButton NOT selected = 1 Playermode
                MusicNavigator.loadVista(MusicNavigator.GAMEFXML);
            }
        } catch (Exception e) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Error Blindtest - MusikRaten");
            dialog.setContentText("An error occured while opening game mode. Cause:" + e.getMessage());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    //refresh player names after gaining focus again (should be invoked after closing player fxml
    @FXML
    public void gotFocusAgain() throws Exception {
        setPlayerNames();

    }

    //set player names from main class
    @FXML
    public void setPlayerNames() {
        try {
            lblPlayer1.setText(Main.pl1.getName());
            lblPlayer2.setText(Main.pl2.getName());
            lblPlayer3.setText(Main.pl3.getName());
            lblPlayer4.setText(Main.pl4.getName());
        } catch (Exception e) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Error Blindtest - MusikRaten");
            dialog.setContentText("An error occured in handling player names. Cause:" + e.getMessage());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    /* for debugging only
    //get music path to folder containing mp3 files from Main class into label in start
    @FXML
    public void getMusicPath() {
             lblDir.setText(Main.getDir());
    }
     */
}
