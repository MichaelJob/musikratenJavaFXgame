package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
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
    //gameScores (points pro Set of 10 Songs)
    private static int gameScoreLeft = 0;
    private static int gameScoreRight = 0;
    private static int level = 1; //1=rookie (default), 2=nerd 3=expert
    private static int expertTime = 2;  //seconds song is played in expert level
    private static int nerdTime = 4;    //seconds song is played in nerd level
    private static int genreChooser = 1; //default is 1:= all hardcoded genres; 2:= only metal&rock; 3:=no metal&rock ; 4:=Indie&Alternative
    //boolean if Game play possible - will be turned off if less than 4 diffrent artist are selected in music collection
    private static boolean playable = true;
    private myDialog myD = new myDialog();

    @Override
    public void start(Stage stage) throws Exception {
        //import player data from file
        loadData();
        //import path data from file - path will be read to Variable dir
        loadPath();
        if (!dir.equals("")) {
            //this is not the first start, so we can import the gameSet
            importGameSet();
        } // else:  dir.equals("") (first start or file lost) go on without path - startcontroller will notice and handle it

        stage.setTitle("MusikRaten");
        //min window size
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.setResizable(true);
        //scene
        stage.setScene(
                createScene(
                        loadMainPane()
                )
        );
        //icon
        stage.getIcons().add(iconmusic);
        //get up and running
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
                //if loading fails (first start of game) or file got deleted
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
            myD.showDialog("loadData - An error occured while handling players. Cause:" + e.getMessage());
        }
    }

    public void loadPath() throws IOException {
        //get musicpath from file //throws IOException
        try {
            File temp = new File("musicpath.txt");
            try (Scanner scanner = new Scanner(temp)) {
                dir = scanner.nextLine();
                scanner.close();
            }
        } catch (IOException e) {
            /*will fail on first start on new system - set "" (StartController
            * will notice and disable Play Button; so user must choose path first
            * in DirectoryChooser)
             */
            dir = "";
        }
    }

    //reads in the GameSet from file
    public void importGameSet() throws Exception {
        List<Song> importSongList;
        Set<Song> importSet;
        try {
            //get songList from file 
            File fileSongList = new File("savedSongList.dat");
            ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(fileSongList));
            importSongList = (List<Song>) ois1.readObject();
            //get gameSet from file 
            File fileHashSet = new File("savedHashSet.dat");
            ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(fileHashSet));
            importSet = (HashSet<Song>) ois2.readObject();
            //call the SongManager and hand over imported data
            SongManager sm = new SongManager(importSet, importSongList);
        } catch (IOException | ClassNotFoundException e) {
            myD.showDialog("An error occured while importing the Songdata. Cause:" + e.getMessage());
            //if this fails create it new
            SongManager.getSongFiles();
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
            myD.showDialog("on stop save - An error occured while saving the data. Cause:" + e.getMessage());
        }

        //save songList to file
        try {
            String filename = "savedSongList.dat";
            // Serialize / save it
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(SongManager.songList);
        } catch (Exception e) {
            myD.showDialog("An error occured while saving the songList. Cause:" + e.getMessage());
        }

        //save gameSet to file
        try {
            String filename = "savedHashSet.dat";
            // Serialize / save it
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(SongManager.gameSet);
        } catch (Exception e) {
            myD.showDialog("An error occured while saving the gameSet. Cause:" + e.getMessage());
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

    //Getter
    public static int getGameScoreLeft() {
        return gameScoreLeft;
    }

    public static int getGameScoreRight() {
        return gameScoreRight;
    }

    //Add 1 Up
    public static void addGameScoreLeft() {
        gameScoreLeft++;
    }

    public static void addGameScoreRight() {
        gameScoreRight++;
    }

    //reSet to 0
    public static void resetGameScore() {
        //for both players
        gameScoreLeft = 0;
        gameScoreRight = 0;
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int l) {
        level = l;
    }

    public static int getExpertTime() {
        return expertTime;
    }

    public static void setExpertTime(int eT) {
        expertTime = eT;
    }

    public static int getNerdTime() {
        return nerdTime;
    }

    public static void setNerdTime(int nT) {
        nerdTime = nT;
    }

    public static int getGenreChooser() {
        return genreChooser;
    }

    public static void setGenreChooser(int genreChooser) {
        Main.genreChooser = genreChooser;
    }

    public static boolean isPlayable() {
        return playable;
    }

    public static void setPlayable(boolean playable) {
        Main.playable = playable;
    }
}
