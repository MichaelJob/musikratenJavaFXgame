package main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author mjair
 */
public class GameController {

    private List<Song> Songs4Game = new ArrayList<>();
    private int correct;
    private int guess;
    private MediaPlayer mediaPlayer;
    private Media media;
    private URL URL = null;
    private File file = null;
    private int counter = 0;
    private boolean voted = false;  //set to true after first vote - to block further guesses

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
    @FXML
    Button btGuess1;
    @FXML
    Button btGuess2;
    @FXML
    Button btGuess3;
    @FXML
    Button btGuess4;
    @FXML
    Label lPlayer;
    @FXML
    Label lblCredit;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        try {
            lPlayer.setText(Main.getPlayerLeft().getName() + "," + '\n' + "make an educated guess:");
            Main.getPlayerLeft().add1plGame();
            playFirst();
        } catch (Exception ex) {
            Dialogs.create()
                    .title("Error Blindtest - MusikRaten")
                    .masthead(null)
                    .message("An error occured Cause:" + ex.getMessage())
                    .showInformation();
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
        correct = (int) (Math.random() * 4) + 1;  //random 1-4
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
        voted = false;
        lblCredit.setText("");
        //reset colors
        lblSong1.setTextFill(Color.LIGHTGREY);
        lblSong2.setTextFill(Color.LIGHTGREY);
        lblSong3.setTextFill(Color.LIGHTGREY);
        lblSong4.setTextFill(Color.LIGHTGREY);
        btGuess1.setOpacity(1);
        btGuess2.setOpacity(1);
        btGuess3.setOpacity(1);
        btGuess4.setOpacity(1);
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
        correct = (int) ((Math.random() * 4) + 1);  //random 1-4
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
        if (!voted) {                                //does only something if voted is false, after first vote it's obsolet
            mediaPlayer.setVolume(0.3);//lower volume
            getGuess(e);
            if (guess == correct) {
                //dem Player Punkt gutschreiben
                Main.getPlayerLeft().addScore1pl();
                //show correct song in Green and fade out others
                colorLabelsGREEN();
                lblCredit.setText("You scored!");
            } else {
                //show correct song in Green and wrong Guess in RED
                colorLabelsGREEN();
                colorLabelsRED();
                lblCredit.setText("fail!");
            }
            voted = true;
            btGuess1.setOpacity(0.3);
            btGuess2.setOpacity(0.3);
            btGuess3.setOpacity(0.3);
            btGuess4.setOpacity(0.3);
        }
    }

    private void getGuess(ActionEvent e) {
        //get clicked button //or typed number
        //actionevent get source  welcher knopf, welche taste (1-4)
        if (e.getSource() == btGuess1) {
            guess = 1; //acording to clicked button
        } else if (e.getSource() == btGuess2) {
            guess = 2;
        } else if (e.getSource() == btGuess3) {
            guess = 3;
        } else if (e.getSource() == btGuess4) {
            guess = 4;
        }

    }

    /*
    public void handle(ActionEvent e) {
       // int keyTyped = (int) (((KeyEvent)e).getCharacter());
        switch (e.getSource()==1) {
            case 1:
                guess = 1;
                break;
            case 2:
                guess = 1;
                break;
            case 3:
                guess = 1;
                break;
            case 4:
                guess = 1;
                break;
        }
    }
    */

    private void colorLabelsGREEN() {
        switch (correct) {
            case 1:
                lblSong1.setTextFill(Color.GREEN);
                break;
            case 2:
                lblSong2.setTextFill(Color.GREEN);
                break;
            case 3:
                lblSong3.setTextFill(Color.GREEN);
                break;
            case 4:
                lblSong4.setTextFill(Color.GREEN);
                break;
        }
    }

    private void colorLabelsRED() {
        switch (guess) {
            case 1:
                lblSong1.setTextFill(Color.RED);
                break;
            case 2:
                lblSong2.setTextFill(Color.RED);
                break;
            case 3:
                lblSong3.setTextFill(Color.RED);
                break;
            case 4:
                lblSong4.setTextFill(Color.RED);
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
