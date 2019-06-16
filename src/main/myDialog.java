package main;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 *
 * @author mjob
 */
public class myDialog {
       

    public void showDialog(String msg){
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("MusikRaten JFX");
                dialog.setContentText(msg);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                dialog.showAndWait();  
    }

    
}

