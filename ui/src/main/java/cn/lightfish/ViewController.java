package cn.lightfish;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;

import java.io.File;

public class ViewController {
    @FXML private Text actiontarget;
    @FXML private  TreeView<File> files=new TreeView<>();
    ViewController(){
        files.edit(new TreeItem<>(new File("c:/")));
    }
    @FXML protected void handleSubmitButtonAction(ActionEvent event) {

        actiontarget.setText("Sign in button pressed");
    }
    @FXML protected void exit(ActionEvent event) {
        System.exit(4);
    }
    @FXML protected void files(ActionEvent event) {
        System.exit(4);
    }
}
