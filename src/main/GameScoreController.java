package main;

import static java.lang.Math.random;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * GameScoreController.java FXML Controller class handling the scene showing the
 * scores after playing through a whiole set of 10 songs is not shown if you
 * exit play mode before
 *
 * @author Michael Job
 */
public class GameScoreController implements Initializable {

    @FXML
    private Button bClose;
    @FXML
    private Label lGameScore;
    @FXML
    BorderPane GameScorePane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param resb
     */
    @Override
    public void initialize(URL url, ResourceBundle resb) {
        try {
            setGameScore();
            show();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MusikRaten");
            alert.setHeaderText(null);
            alert.setContentText("An error occured while showing GameScores. Cause:" + ex.getMessage());
            alert.initOwner((Stage) bClose.getScene().getWindow());
            alert.showAndWait();  
        }
    }

    //show startfxml again
    @FXML
    public void handleBClose(ActionEvent event) throws Exception {
        try {
            MusicNavigator.loadVista(MusicNavigator.STARTFXML);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MusikRaten");
            alert.setHeaderText(null);
            alert.setContentText("Oupps! An error occured while closing Game Scores. Cause:" + e.getMessage());
            alert.initOwner((Stage) bClose.getScene().getWindow());
            alert.showAndWait();       
        }
    }
    
    //show startfxml again
    @FXML
    public void closeGameScore() {
        MusicNavigator.loadVista(MusicNavigator.STARTFXML);
    }
    
    

    //set gamescore in label
    @FXML
    public void setGameScore() {
        String Left = Main.PlayerLeft.getName();
        String Right = Main.PlayerRight.getName();
        int ScoreLeft = Main.getGameScoreLeft();
        int ScoreRight = Main.getGameScoreRight();
        String winner = "";
        String s = "";
        if (Main.isPlayerMode2()) {  //2 PlayerMode
            if (ScoreLeft > ScoreRight) {
                winner = Left;
            } else if (ScoreLeft == ScoreRight) {
                winner = "You're equal! Play another round!";
            } else {
                winner = Right;
            }
            s = "...and the Winner is: " + winner + '\n' + '\n'
                    + "Well done folks," + '\n'
                    + Left + " has got " + Integer.toString(ScoreLeft) + " Songs correct." + '\n'
                    + Right + " has got " + Integer.toString(ScoreRight) + " Songs out of 10.";
        } else {  //1Player Mode
            s = "Well done, " + Left + '\n' + "You've got " + Integer.toString(ScoreLeft) + " out of 10 Songs";
        }
        lGameScore.setText(s);
    }

    //fancy background animation - code source: oracle javaFX examples
    @FXML
    public void show() {
        Group circles = new Group();
        for (int i = 0; i < 100; i++) {
            Circle circle = new Circle(10, Color.web("white", 0.2));
            circle.setStrokeType(StrokeType.OUTSIDE);
            circle.setStroke(Color.web("white", 0.16));
            circle.setStrokeWidth(4);
            circles.getChildren().add(circle);
        }
        circles.setEffect(new BoxBlur(10, 10, 3));
        Rectangle colors = new Rectangle(1024, 620,
                
                new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new Stop[]{
                    new Stop(0, Color.web("#f8bd55")),
                    new Stop(0.14, Color.web("#c0fe56")),
                    new Stop(0.28, Color.web("#5dfbc1")),
                    new Stop(0.43, Color.web("#64c2f8")),
                    new Stop(0.57, Color.web("#be4af7")),
                    new Stop(0.71, Color.web("#ed5fc2")),
                    new Stop(0.85, Color.web("#ef504c")),
                    new Stop(1, Color.web("#f2660f")),}));
        
        Group blendModeGroup = new Group(new Group(new Rectangle(1024, 620, Color.TRANSPARENT), circles), colors);
        colors.setBlendMode(BlendMode.OVERLAY);
        GameScorePane.getChildren().add(blendModeGroup);
        Timeline timeline = new Timeline();
        circles.getChildren().stream().forEach((circle) -> {
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, // set start position at 0
                            new KeyValue(circle.translateXProperty(), 512),
                            new KeyValue(circle.translateYProperty(), 310)
                    ),
                    new KeyFrame(new Duration(30000), // set end position at 40s
                            new KeyValue(circle.translateXProperty(), (random() * 1024)),
                            new KeyValue(circle.translateYProperty(), (random() * 620))
                    )
            );
        });
        // play 30s of animation
        timeline.play();
        }
}