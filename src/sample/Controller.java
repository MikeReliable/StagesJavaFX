package sample;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {

    File file;
    FileChooser fileChooser = new FileChooser();
    SoSelection selection = new SoSelection();

    @FXML
    private TextField s01;

    @FXML
    private TextField s02;

    @FXML
    private TextField step;

    @FXML
    private TextArea text;

    @FXML
    private TextField index;

//    @FXML
//    private TextField location;

    @FXML
    void open(ActionEvent event) {
        file = fileChooser.showOpenDialog(new Stage());
    }

    @FXML
    void action(MouseEvent event) {
        String result = selection.find(Integer.parseInt(s01.getText()), Integer.parseInt(s02.getText()), Integer.parseInt(step.getText()), Double.parseDouble(index.getText()), file);
        text.clear();
        text.appendText(result);
    }

    @FXML
    void initialize() {
        fileChooser.setInitialDirectory(new File("src/sample/"));
        assert s01 != null : "fx:id=\"s01\" was not injected: check your FXML file 'form.fxml'.";
        assert s02 != null : "fx:id=\"s02\" was not injected: check your FXML file 'form.fxml'.";
        assert step != null : "fx:id=\"step\" was not injected: check your FXML file 'form.fxml'.";
        assert text != null : "fx:id=\"text\" was not injected: check your FXML file 'form.fxml'.";

    }
}
