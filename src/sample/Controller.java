package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Controller {

    File file;
    FileChooser fileChooser = new FileChooser();
    Logic selection = new Logic();

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

    @FXML
    private TextArea locat;

    @FXML
    void open(ActionEvent event) {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("data file","*.DAT"));
        fileChooser.setInitialDirectory(new File("C:\\"));
        file = fileChooser.showOpenDialog(new Stage());
        locat.clear();
        locat.appendText(file.getAbsolutePath());
    }

    @FXML
    void action(MouseEvent event) throws IOException {
        text.clear();
        if (file == null) {
            text.appendText("\nФайл не выбран!\n");
        } else {
            String result = selection.find(file);
            text.clear();
            text.appendText(result);
        }
    }

    @FXML
    void about(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("about.fxml")));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("about");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @FXML
//    void initialize() {
//        assert s01 != null : "fx:id=\"s01\" was not injected: check your FXML file 'form.fxml'.";
//        assert s02 != null : "fx:id=\"s02\" was not injected: check your FXML file 'form.fxml'.";
//        assert step != null : "fx:id=\"step\" was not injected: check your FXML file 'form.fxml'.";
//        assert text != null : "fx:id=\"text\" was not injected: check your FXML file 'form.fxml'.";
//
//    }
}
