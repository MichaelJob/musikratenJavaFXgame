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
public class GameController {

    private List<Song> Songs4Game = new ArrayList<>();
    private int correct;
    private int guess;
    private MediaPlayer mediaPlayer;
    private Media media;
    private URL URL = null;
    private File file = null;
    private int counter = 0;    //how many songs are played in this set
    private boolean voted = false;  //set to true after first vote - to block further guesses
    private BufferedImage img;
    private Image noCover = new Image("/guisrc/noCover.jpg");

//FXML Elements
    @FXML
    Button BtnExit;
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
    @FXML
    ImageView imgCover1;
    @FXML
    ImageView imgCover2;
    @FXML
    ImageView imgCover3;
    @FXML
    ImageView imgCover4;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        try {
            lPlayer.setText(Main.getPlayerLeft().getName() + "," + '\n' + "make an educated guess:");
            //reset game Scores for this Gameplay
            Main.resetGameScore();
            //play first Song
            playFirst();
        } catch (Exception ex) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Error Blindtest - MusikRaten");
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
        muteLevel();
        //add counter
        counter++;
        /*request focus to play next button - default was on guess button and hitting
         * the space bar "clicked" it, which was not lucky
         * needs to be "run" later otherwise not working (javaFX default focus would override it)
         */
        Platform.runLater(() -> {
            btNext.requestFocus();
        });
    }

    public void playNext() {
        //set focus to play next button as a default
        Platform.runLater(() -> {
            btNext.requestFocus();
        });
        //go to next Song only works if user has voted!
        if (voted) {
            //stop previous song
            mediaPlayer.stop();
            //end Game if counter is 10 (10 songs played)
            if (counter > 9) {
                handleBtnExit();
                return;
            }
            //reset voted
            voted = false;
            guess = 0;   //overriding old guess with magic number
            lblCredit.setText("");      //color of label credit can stay like before, cause there is no text shown
            //reset colors
            lblSong1.setTextFill(Color.LIGHTGREY);
            lblSong2.setTextFill(Color.LIGHTGREY);
            lblSong3.setTextFill(Color.LIGHTGREY);
            lblSong4.setTextFill(Color.LIGHTGREY);
            imgCover1.setImage(null);
            imgCover2.setImage(null);
            imgCover3.setImage(null);
            imgCover4.setImage(null);
            btGuess1.setDisable(false);
            btGuess2.setDisable(false);
            btGuess3.setDisable(false);
            btGuess4.setDisable(false);
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
            //get the correct song playing
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
        } else if (Main.getLevel() == 2) {
            //if still running stop it first (if voted faster than song was stopped)
            mediaPlayer.stop();
            //start new
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(0.7);//lower volume
            mediaPlayer.setStartTime(Duration.seconds(Main.getNerdTime()));//start where we stopped
            mediaPlayer.play();
        } else if (Main.getLevel() == 3) {
            //if still running stop it first (if voted faster than song was stopped)
            mediaPlayer.stop();
            //start new
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(0.7);//lower volume
            //start from scratch again
            mediaPlayer.play();
        }
    }

    //check answer
    @FXML
    private void checkAnswer(ActionEvent e) {
        if (!voted) {                                //does only something if voted is false, after first vote it's obsolet
            getGuess(e);
            resolveAnswer();
        }
    }

    //check answer guess is either set by buttons or typed keys already
    @FXML
    private void resolveAnswer() {
        //lower volume and make sure you can listen to music again...
        playAgain();
        if (guess == correct) {
            //dem Player Punkt gutschreiben
            Main.getPlayerLeft().addScore1pl();
            Main.addGameScoreLeft();
            //show correct song in Green and fade out others
            colorLabelsGREEN();
            lblCredit.setTextFill(Color.MEDIUMSPRINGGREEN);
            lblCredit.setText("You scored!");
        } else {
            //show correct song in Green and wrong Guess in RED
            colorLabelsGREEN();
            colorLabelsRED();
            lblCredit.setTextFill(Color.RED);
            lblCredit.setText("fail!");
        }
        voted = true;
        //Buttons Disable
        disableButtons(guess);
    }

    @FXML
    public void onKeyPressed(KeyEvent ae) {
        //checks which guess was made by keystroke
        if (!voted) {
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

    public void getGuess(ActionEvent e) {
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
        }
    }

    private void disableButtons(int guess) {
        //disable other buttons (not the clicked one) so, clicking other buttons is of no action and style settings are taking place
        //guess is clicked button
        if (guess == 1) {
            btGuess2.setDisable(true);
            btGuess3.setDisable(true);
            btGuess4.setDisable(true);
        } else if (guess == 2) {
            btGuess1.setDisable(true);
            btGuess3.setDisable(true);
            btGuess4.setDisable(true);
        } else if (guess == 3) {
            btGuess1.setDisable(true);
            btGuess2.setDisable(true);
            btGuess4.setDisable(true);
        } else if (guess == 4) {
            btGuess1.setDisable(true);
            btGuess2.setDisable(true);
            btGuess3.setDisable(true);
        }
    }

    //correct song : add title and album info and color it green
    private void colorLabelsGREEN() {
        switch (correct) {
            case 1:
                lblSong1.setTextFill(Color.MEDIUMSPRINGGREEN);
                lblSong1.setText("Song: " + Songs4Game.get(0).getTitle() + '\n' + "Artist: " + Songs4Game.get(0).getArtist() + '\n' + "Album: " + Songs4Game.get(0).getAlbum());
                break;
            case 2:
                lblSong2.setTextFill(Color.MEDIUMSPRINGGREEN);
                lblSong2.setText("Song: " + Songs4Game.get(1).getTitle() + '\n' + "Artist: " + Songs4Game.get(1).getArtist() + '\n' + "Album: " + Songs4Game.get(1).getAlbum());
                break;
            case 3:
                lblSong3.setTextFill(Color.MEDIUMSPRINGGREEN);
                lblSong3.setText("Song: " + Songs4Game.get(2).getTitle() + '\n' + "Artist: " + Songs4Game.get(2).getArtist() + '\n' + "Album: " + Songs4Game.get(2).getAlbum());
                break;
            case 4:
                lblSong4.setTextFill(Color.MEDIUMSPRINGGREEN);
                lblSong4.setText("Song: " + Songs4Game.get(3).getTitle() + '\n' + "Artist: " + Songs4Game.get(3).getArtist() + '\n' + "Album: " + Songs4Game.get(3).getAlbum());
                break;
        }
        //and show album art - cover pictures
        getAlbumArt();
    }

    //get album art - cover
    private void getAlbumArt() {
        BufferedImage bf = null;
        WritableImage wr = null;
        //Byte Array of mp3 song gives us a bufferd image
        //to show in JavaFX we need a real image - Pixelwriter paints WritableImage
        // (extends Image) out of buffered image
        //Loop for all 4 songs:
        for (int i = 0; i < 4; i++) {
            try {
                bf = ImageIO.read(new ByteArrayInputStream(Songs4Game.get(i).getCover()));
            } catch (Exception e) {
                bf = null;
            }
            if (bf != null) {
                wr = new WritableImage(bf.getWidth(), bf.getHeight());
                PixelWriter pw = wr.getPixelWriter();
                for (int x = 0; x < bf.getWidth(); x++) {
                    for (int y = 0; y < bf.getHeight(); y++) {
                        pw.setArgb(x, y, bf.getRGB(x, y));
                    }
                }
                //set Image to GUI
                switch (i) {
                    case 0:
                        imgCover1.setImage(wr);
                        break;
                    case 1:
                        imgCover2.setImage(wr);
                        break;
                    case 2:
                        imgCover3.setImage(wr);
                        break;
                    case 3:
                        imgCover4.setImage(wr);
                        break;
                }

            } else {
                //if no cover art is in mp3 tag - set default no album art cover pic
                switch (i) {
                    case 0:
                        imgCover1.setImage(noCover);
                        break;
                    case 1:
                        imgCover2.setImage(noCover);
                        break;
                    case 2:
                        imgCover3.setImage(noCover);
                        break;
                    case 3:
                        imgCover4.setImage(noCover);
                        break;
                }
            }
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
        if (counter > 8) {
            Main.getPlayerLeft().add1plGame();     //add games played count
            //show Game Score Scene
            MusicNavigator.loadVista(MusicNavigator.GAMESCOREFXML);
        } else {
            //show Start Scene again without Gamescore //user stoped before 10 Songs were guessed
            MusicNavigator.loadVista(MusicNavigator.STARTFXML);
        }
    }

}
