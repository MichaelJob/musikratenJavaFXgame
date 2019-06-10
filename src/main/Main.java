package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main application class.
 */
public class Main extends Application {

    //icon
    public Image iconmusic = new Image("/guisrc/ico.jpg");
    //selected Playernames and Playermode 1 or 2 Player
    public static boolean playerMode2 = false;    //false=1 Player ; true=2 Player - will be set here to keep pref in startfxml
    //in 2 player mode - one is left, one is right
    public static PlayerUser PlayerLeft;
    public static PlayerUser PlayerRight;
    //the four players who are currently in the game
    public static PlayerUser pl1;
    public static PlayerUser pl2;
    public static PlayerUser pl3;
    public static PlayerUser pl4;
    //List highScore contains Objects PlayerUser
    public static List<PlayerUser> highScore = new ArrayList<>();
    //music path and files...
    private static String dir;

    @Override
    public void start(Stage stage) throws Exception {
        //import player data from file
        loadData();
        //import path data from file
        loadPath();
        if (dir != null) {

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Info Blindtest - MusikRaten");
            dialog.setContentText("It can take a while to search&seek all your mp3 Files. Be patient");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
            dialog.showAndWait();

            //walk through directory and look for mp3 files
            SongManager.walk(dir);
            SongManager.createSongList();
            SongManager.createGameSet();
        }
        //get up and running
        stage.setTitle("MusikRaten");

        stage.setScene(
                createScene(
                        loadMainPane()
                )
        );
        stage.getIcons().add(iconmusic);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Loads the main fxml layout. Sets up the vista switching VistaNavigator.
     * Loads the first vista into the fxml layout.
     *
     * @return the loaded pane.
     * @throws IOException if the pane could not be loaded.
     */
    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane mainPane = (Pane) loader.load(
                getClass().getResourceAsStream(
                        MusicNavigator.MAIN
                )
        );
        MainController mainController = loader.getController();
        MusicNavigator.setMainController(mainController);
        MusicNavigator.loadVista(MusicNavigator.STARTFXML);
        return mainPane;
    }

    /**
     * Creates the main application scene.
     *
     * @param mainPane the main application layout.
     * @return the created scene.
     */
    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(
                mainPane
        );
        scene.getStylesheets().setAll(
                getClass().getResource("/guisrc/blindtest.css").toExternalForm()
        );
        return scene;
    }

    //
    public static void main(String[] args) {
        launch(args);
    }

    //load Data from File on startup of application
    public void loadData() throws IOException {
        try {
            try {
                FileInputStream fis = new FileInputStream("highScoreFile.txt");
                try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                    try {
                        highScore = (List<PlayerUser>) ois.readObject();
                        //get players out of list into current players
                        pl1 = highScore.get(0);
                        pl2 = highScore.get(1);
                        pl3 = highScore.get(2);
                        pl4 = highScore.get(3);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                //if loading fails (first start of game) of file got deleted
                try {
                    //create new PlayerUser objects and add them to List 
                    pl1 = new PlayerUser("Player 1");
                    highScore.add(pl1);
                    pl2 = new PlayerUser("Player 2");
                    highScore.add(pl2);
                    pl3 = new PlayerUser("Player 3");
                    highScore.add(pl3);
                    pl4 = new PlayerUser("Player 4");
                    highScore.add(pl4);
                } catch (Exception e) {
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setTitle("Error Blindtest - MusikRaten");
                    dialog.setContentText("Def erst - An error occured while handling default players. Cause:" + e.getMessage());
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                    dialog.showAndWait();
                }
            }
        } catch (Exception e) {       //error msg if all fails
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Error Blindtest - MusikRaten");
            dialog.setContentText("loadData - An error occured while handling players. Cause:" + e.getMessage());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    public void loadPath() throws IOException {
        //get musicpath from file
        try {
            File temp = new File("musicpath.txt");
            try (Scanner scanner = new Scanner(temp)) {
                dir = scanner.nextLine();
            }
        } catch (IOException e) {
            //will fail on first start on new system - create file with windows pc default path
            try {
                try (PrintWriter out = new PrintWriter("musicpath.txt")) {
                    out.print("C:/");
                }
                dir = "C:/";
            } catch (IOException e3) {
                //if this fails as well let user know to set a path
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Error Blindtest - MusikRaten");
                dialog.setContentText("No music directory set. Please select a path." + e.getMessage() + e3.getMessage());
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                dialog.showAndWait();
            }
        }
    }

    //on Application close save Players and Scores...
    @Override
    public void stop() throws java.lang.Exception {
        //save List to file
        try {
            File file = new File("highScoreFile.txt");
            FileOutputStream f = new FileOutputStream(file);
            try (ObjectOutputStream s = new ObjectOutputStream(f)) {
                s.writeObject(highScore);
            }
        } catch (IOException e) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Error Blindtest - MusikRaten");
            dialog.setContentText("on stop save - An error occured while saving the data. Cause:" + e.getMessage());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    //Getter and Setter
    public static String getDir() {
        return dir;
    }

    public static void setDir(String s) {
        dir = s;
    }

    public static boolean isPlayerMode2() {
        return playerMode2;
    }

    public static void setPlayerMode2() {
        playerMode2 = true;
    }

    public static void setPlayerMode1() {
        playerMode2 = false;
    }

    public static PlayerUser getPlayerLeft() {
        return PlayerLeft;
    }

    public static void setPlayerLeft(PlayerUser pl) {
        PlayerLeft = pl;
    }

    public static PlayerUser getPlayerRight() {
        return PlayerRight;
    }

    public static void setPlayerRight(PlayerUser pl) {
        PlayerRight = pl;
    }
}
