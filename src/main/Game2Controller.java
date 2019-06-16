package main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javax.imageio.ImageIO;

/**
 *
 * @author mjair
 */
public class Game2Controller {

    private List<Song> Songs4Game = new ArrayList<>();  //random set of 4 songs
    private int correct;    //random song which is played
    private int guess = 0;  //what user/s clicked or typed
    private MediaPlayer mediaPlayer;
    private Media media;
    private URL URL = null;
    private File file = null;
    private int counter = 0;        //counts how many songs you play in on go - you can end via button or it ends after 10 songs by itself
    private boolean votedLeft = false;  //set to true after first vote - to block further guesses Left Player
    private boolean votedRight = false;  //set to true after first vote - to block further guesses Right Player
    private int gameScoreLeft = 0;  //LeftPlayer Score in this game only (will not be saved) - allover Scores are stored in Player
    private int gameScoreRight = 0;  //RightPlayer Score in this game only (will not be saved) - allover Scores are stored in Player
    private Image noCover = new Image("/guisrc/noCover.jpg"); //no album art cover picture
 
//FXML Elements
    @FXML
    Button btExit;
    @FXML
    Button btNext;
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
    @FXML
    ImageView imgCover;

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
            //reset game Scores for this Gameplay
            Main.resetGameScore();
            //play first song
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
        muteLevel();
        //add counter
        counter++;
        Platform.runLater(() -> {
            btNext.requestFocus();
        });
    }


   public void playNext() {
        Platform.runLater(() -> {
            btNext.requestFocus();
        });
        //go to next Song only works if user has voted!
        if (votedLeft && votedRight) {
            //reset voted
            guess = 0;
            imgCover.setImage(null);
            votedLeft = false;
            votedRight = false;
            lblCredit.setText("");
            //reset colors
            lblSong1.setTextFill(Color.LIGHTGREY);
            lblSong2.setTextFill(Color.LIGHTGREY);
            lblSong3.setTextFill(Color.LIGHTGREY);
            lblSong4.setTextFill(Color.LIGHTGREY);
            //restore (enable) buttons
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
            muteLevel();
            //add counter
            counter++;
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent ae) {
        //checks which guess was made by keystroke
        if (!votedLeft | !votedRight) {                  //at least one can still vote
            if (null != ae.getCode()) {
                switch (ae.getCode()) {
                    case DIGIT1:
                    case NUMPAD1:
                        guess = 1;
                        break;
                    case DIGIT2:
                    case NUMPAD2:
                        guess = 2;
                        break;
                    case DIGIT3:
                    case NUMPAD3:
                        guess = 3;
                        break;
                    case DIGIT4:
                    case NUMPAD4:
                        guess = 4;
                        break;
                    case DIGIT6:
                    case NUMPAD6:

                        guess = 6;
                        break;
                    case DIGIT7:
                    case NUMPAD7:
                        guess = 7;
                        break;
                    case DIGIT8:
                    case NUMPAD8:
                        guess = 8;
                        break;
                    case DIGIT9:
                    case NUMPAD9:
                        guess = 9;
                        break;
                    default:
                        guess = 0;
                        break;
                }
                if (guess != 0) {
                    resolveAnswer();
                }
            }
        }
    }

    //check answer
    @FXML
    private void checkAnswer(ActionEvent e) {
        if (!votedLeft | !votedRight) {          //at least one can still vote
            guessWhat(e);    //set guessed number
            resolveAnswer();
        }
    }

    public void resolveAnswer() {
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
          //set focus to play next button as a default
              Platform.runLater(() -> {
                 btNext.requestFocus();
               });
        solveGame();    //if game solved show it
    }

    public void guessWhat(ActionEvent e) {
        //checks which guess was made
        //get clicked button
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
    }

    private void solveGame() {
        //if both have voted - solve game
        if (votedLeft & votedRight) {
            //lower volume and or play again
            playAgain();
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
        btGuess1.setDisable(true);
        btGuess2.setDisable(true);
        btGuess3.setDisable(true);
        btGuess4.setDisable(true);
    }

    public void rightButtonsFadeOut() {
        btGuess6.setDisable(true);
        btGuess7.setDisable(true);
        btGuess8.setDisable(true);
        btGuess9.setDisable(true);
    }

    public void ButtonsRestore() {
        btGuess1.setDisable(false);
        btGuess2.setDisable(false);
        btGuess3.setDisable(false);
        btGuess4.setDisable(false);
        btGuess6.setDisable(false);
        btGuess7.setDisable(false);
        btGuess8.setDisable(false);
        btGuess9.setDisable(false);
    }

    //the correct Song will be shown with Songtitel and Album in Green
    private void colorLabelsGREEN() {
        switch (correct) {
            case 1:
            case 6:
                lblSong1.setTextFill(Color.MEDIUMSPRINGGREEN);
                lblSong1.setText("Song: " + Songs4Game.get(0).getTitle() + '\n' + "Artist: " + Songs4Game.get(0).getArtist() + '\n' + "Album: " + Songs4Game.get(0).getAlbum());
                 imgCover.setImage(getAlbumArt(correct));
                break;
            case 2:
            case 7:
                lblSong2.setTextFill(Color.MEDIUMSPRINGGREEN);
                lblSong2.setText("Song: " + Songs4Game.get(1).getTitle() + '\n' + "Artist: " + Songs4Game.get(1).getArtist() + '\n' + "Album: " + Songs4Game.get(1).getAlbum());
                 imgCover.setImage(getAlbumArt(correct));
                break;
            case 3:
            case 8:
                lblSong3.setTextFill(Color.MEDIUMSPRINGGREEN);
                lblSong3.setText("Song: " + Songs4Game.get(2).getTitle() + '\n' + "Artist: " + Songs4Game.get(2).getArtist() + '\n' + "Album: " + Songs4Game.get(2).getAlbum());
                 imgCover.setImage(getAlbumArt(correct));
                break;
            case 4:
            case 9:
                lblSong4.setTextFill(Color.MEDIUMSPRINGGREEN);
                lblSong4.setText("Song: " + Songs4Game.get(3).getTitle() + '\n' + "Artist: " + Songs4Game.get(3).getArtist() + '\n' + "Album: " + Songs4Game.get(3).getAlbum());
                 imgCover.setImage(getAlbumArt(correct));
                break;
        }
    }

     //close stage
    @FXML
    private void handleBtnExit() {
        //end game play
        mediaPlayer.stop();
        if (counter > 8) {
            //count games played - only if 10 Songs got played, otherwise not worthy
            Main.getPlayerLeft().add2plGame();
            Main.getPlayerRight().add2plGame();
            //show Game Score Scene
            MusicNavigator.loadVista(MusicNavigator.GAMESCOREFXML);
        } else {
            //show Start Scene again without Gamescore //user stoped before 10 Songs were guessed
            MusicNavigator.loadVista(MusicNavigator.STARTFXML);
        }
    }
    
    
      //stop song after * seconds according to level
    private void muteLevel() {
        //if (Main.getLevel() == 1) do nothing - Rookies can listen forever
        if (Main.getLevel() == 2) {
            mediaPlayer.setStopTime(Duration.seconds(Main.getNerdTime()));
        } else if (Main.getLevel() == 3) {
           mediaPlayer.setStopTime(Duration.seconds(Main.getExpertTime()));
        }
        
    }
    
     //play Song again after voted (only in nerd and expert mode)
    private void playAgain() {
        if (Main.getLevel() == 1) {
            mediaPlayer.setVolume(0.7);//lower volume
        }else if (Main.getLevel() == 2) {
            //if still running stop it first (if voted faster than song was stopped)
            mediaPlayer.stop();
            //start new
            mediaPlayer = new MediaPlayer(media);  
            mediaPlayer.setVolume(0.7);//lower volume
            mediaPlayer.setStartTime(Duration.seconds(Main.getNerdTime()));//start where we stopped
            mediaPlayer.play();
        }else if (Main.getLevel() == 3) {
            //if still running stop it first (if voted faster than song was stopped)
            mediaPlayer.stop();
            //start new
            mediaPlayer = new MediaPlayer(media);  
            mediaPlayer.setVolume(0.7);//lower volume
            //start from scratch again
            mediaPlayer.play();
        }
    }
   //get album art - cover
    private Image getAlbumArt(int correct) {
        BufferedImage bf = null;
        WritableImage wr = null;
        //Byte Array of mp3 song gives us a bufferd image
        try {
            bf = ImageIO.read(new ByteArrayInputStream(Songs4Game.get(correct-1).getCover()));
        } catch (Exception ex) {
            bf=null;
        }
        //to show in JavaFX we need a real image - Pixelwriter paints WritableImage (extends Image) out of buffered image
        if (bf != null) {
            wr = new WritableImage(bf.getWidth(), bf.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bf.getWidth(); x++) {
                for (int y = 0; y < bf.getHeight(); y++) {
                    pw.setArgb(x, y, bf.getRGB(x, y));
                }
            }
            return wr;
        }else {
            return noCover;
        }
    }

}
