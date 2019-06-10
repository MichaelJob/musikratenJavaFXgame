package main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

/**
 *
 * @author mjair
 */
public class game2Controller {

    private List<Song> Songs4Game = new ArrayList<Song>();  //random set of 4 songs
    private int correct;    //random song which is played
    private int guess;  //what user/s clicked or typed
    private MediaPlayer mediaPlayer;
    private Media media;
    private URL URL = null;
    private File file = null;
    private int counter = 0;        //counts how many songs you play in on go - you can end via button or it ends after 10 songs by itself
    private boolean votedLeft = false;  //set to true after first vote - to block further guesses Left Player
    private boolean votedRight = false;  //set to true after first vote - to block further guesses Right Player
    private int gameScoreLeft = 0;  //LeftPlayer Score in this game only (will not be saved) - allover Scores are stored in Player
    private int gameScoreRight = 0;  //RightPlayer Score in this game only (will not be saved) - allover Scores are stored in Player

//FXML Elements
    @FXML
    Button BtnExit;
    @FXML
    Label lblSong1;
    @FXML
    Label lblSong2;
    @FXML
    Label lblSong3;
    @FXML
    Label lblSong4;
    //for Player Left
    @FXML
    Button btGuess1;
    @FXML
    Button btGuess2;
    @FXML
    Button btGuess3;
    @FXML
    Button btGuess4;
    //for Player Right
    @FXML
    Button btGuess6;
    @FXML
    Button btGuess7;
    @FXML
    Button btGuess8;
    @FXML
    Button btGuess9;
    @FXML
    Label lblPlayerLeft;
    @FXML
    Label lblPlayerRight;
    @FXML
    Label lblCredit;
    @FXML
    Label lblScores;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        try {
            //get Player names
            lblPlayerLeft.setText(Main.getPlayerLeft().getName());
            lblPlayerRight.setText(Main.getPlayerRight().getName());
            //count games played
            Main.getPlayerLeft().add2plGame();
            Main.getPlayerRight().add2plGame();
            //play default mp3 on startup
            playFirst();
        } catch (Exception ex) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Error Blindtest - MusikRaten");
            dialog.setContentText("An error occured Cause:" + ex.getMessage());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    public void playFirst() {
        lblSong1.setTextFill(Color.LIGHTGREY);
        lblSong2.setTextFill(Color.LIGHTGREY);
        lblSong3.setTextFill(Color.LIGHTGREY);
        lblSong4.setTextFill(Color.LIGHTGREY);
        //get 4 Songs
        Songs4Game = SongManager.get4Songs();
        lblSong1.setText(Songs4Game.get(0).getArtist());
        lblSong2.setText(Songs4Game.get(1).getArtist());
        lblSong3.setText(Songs4Game.get(2).getArtist());
        lblSong4.setText(Songs4Game.get(3).getArtist());
        //set correct to a random pick
        correct = (int) ((Math.random() * 4) + 1);  //random 1-4
        //get it playing
        file = new File(Songs4Game.get(correct - 1).getPath());       //array has index 0-3
        try {
            URL = file.toURI().toURL();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        media = new Media(URL.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        //add counter
        counter++;
    }

    public void playNext() {
        //reset voted
        votedLeft = false;
        votedRight = false;
        lblCredit.setText("");
        //reset colors
        lblSong1.setTextFill(Color.LIGHTGREY);
        lblSong2.setTextFill(Color.LIGHTGREY);
        lblSong3.setTextFill(Color.LIGHTGREY);
        lblSong4.setTextFill(Color.LIGHTGREY);
        ButtonsRestore();
        //stop previour song
        mediaPlayer.stop();
        //end Game if couter is 10
        if (counter > 9) {
            handleBtnExit();
            return;
        }
        //do it again, Sam
        //get 4 Songs
        Songs4Game = SongManager.get4Songs();
        lblSong1.setText(Songs4Game.get(0).getArtist());
        lblSong2.setText(Songs4Game.get(1).getArtist());
        lblSong3.setText(Songs4Game.get(2).getArtist());
        lblSong4.setText(Songs4Game.get(3).getArtist());
        //set correct to a random pick
        correct = (int) (Math.random() * 4) + 1;  //random 1-4
        //System.out.println(Integer.toString(correct));
        //get it playing
        file = new File(Songs4Game.get(correct - 1).getPath());       //array has index 0-3
        try {
            URL = file.toURI().toURL();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        media = new Media(URL.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(1.0);
        mediaPlayer.play();
        //add counter
        counter++;
    }

    //check answer
    @FXML
    private void checkAnswer(ActionEvent e) {
        if (!votedLeft | !votedRight) {          //at least one can still vote
            guess = guessWhat(e);    //get guessed number
            //who was it? left or right player?
            if (guess > 5 & !votedRight) {                       //if Player Right has guessed the first time evaluate it - after this it's over for Player right
                //Player Right voted
                votedRight = true;
                if ((guess - 5) == correct) {         //the correct answer has come - guess-5 equals correct
                    //if correct answer is given - vote is over - so let's say left voted too...
                    votedLeft = true;
                    //add Score in this game
                    gameScoreRight++;
                    //add Score to Player alltime
                    Main.getPlayerRight().addScore2pl();
                    //inform players
                    lblCredit.setText(lblPlayerRight.getText() + " has scored!");
                } else {    //if player right failed - player left can still vote (only one time of course)
                    lblCredit.setText(lblPlayerRight.getText() + " failed!");
                }
            } else if (guess < 5 & !votedLeft) {               //if Player Left has guessed the first time evaluate it - after this it's over for Player left
                //Player Left voted
                votedLeft = true;
                if (guess == correct) {         //the correct answer has come - guess equals correct
                    //if correct answer is given - vote is over - so let's say right voted too...
                    votedRight = true;
                    //add Score in this game
                    gameScoreLeft++;
                    //add Score to Player alltime
                    Main.getPlayerLeft().addScore2pl();
                    //inform players
                    lblCredit.setText(lblPlayerLeft.getText() + " has scored!");
                } else {    //if player left failed - player right can still vote (only one time of course)
                    lblCredit.setText(lblPlayerLeft.getText() + " failed!");
                }
            }
            solveGame();    //if game solved show it
        }
    }

    public int guessWhat(ActionEvent e) {
        //checks which guess was made
        //get clicked button //or typed number
        if (((Button) e.getSource()).getText().contains("1")) {
            guess = 1; //acording to clicked button
        } else if (((Button) e.getSource()).getText().contains("2")) {
            guess = 2; //acording to clicked button
        } else if (((Button) e.getSource()).getText().contains("3")) {
            guess = 3; //acording to clicked button
        } else if (((Button) e.getSource()).getText().contains("4")) {
            guess = 4; //acording to clicked button
        } else if (((Button) e.getSource()).getText().contains("6")) {
            guess = 6; //acording to clicked button
        } else if (((Button) e.getSource()).getText().contains("7")) {
            guess = 7; //acording to clicked button
        } else if (((Button) e.getSource()).getText().contains("8")) {
            guess = 8; //acording to clicked button
        } else if (((Button) e.getSource()).getText().contains("9")) {
            guess = 9; //acording to clicked button
        }
        return guess;
    }

    private void solveGame() {
        //if both have voted - solve game
        if (votedLeft & votedRight) {
            //lower volume
            mediaPlayer.setVolume(0.3);
            //show correct song in Green
            colorLabelsGREEN();
            leftButtonsFadeOut();
            rightButtonsFadeOut();
        } else if (votedLeft & !votedRight) {
            leftButtonsFadeOut();
        } else if (votedRight & !votedLeft) {
            rightButtonsFadeOut();
        }
        //show score in any case
        lblScores.setText(Integer.toString(gameScoreLeft) + " : " + Integer.toString(gameScoreRight));
    }

    public void leftButtonsFadeOut() {
        btGuess1.setOpacity(0.3);
        btGuess2.setOpacity(0.3);
        btGuess3.setOpacity(0.3);
        btGuess4.setOpacity(0.3);
    }

    public void rightButtonsFadeOut() {
        btGuess6.setOpacity(0.3);
        btGuess7.setOpacity(0.3);
        btGuess8.setOpacity(0.3);
        btGuess9.setOpacity(0.3);
    }

    public void ButtonsRestore() {
        btGuess1.setOpacity(1);
        btGuess2.setOpacity(1);
        btGuess3.setOpacity(1);
        btGuess4.setOpacity(1);
        btGuess6.setOpacity(1);
        btGuess7.setOpacity(1);
        btGuess8.setOpacity(1);
        btGuess9.setOpacity(1);
    }

    private void colorLabelsGREEN() {
        switch (correct) {
            case 1:
            case 6:
                lblSong1.setTextFill(Color.GREEN);
                break;
            case 2:
            case 7:
                lblSong2.setTextFill(Color.GREEN);
                break;
            case 3:
            case 8:
                lblSong3.setTextFill(Color.GREEN);
                break;
            case 4:
            case 9:
                lblSong4.setTextFill(Color.GREEN);
                break;
        }
    }

    //close stage
    @FXML
    private void handleBtnExit() {
        //end game play
        mediaPlayer.stop();
        //show Start Scene again
        MusicNavigator.loadVista(MusicNavigator.STARTFXML);
    }

}
