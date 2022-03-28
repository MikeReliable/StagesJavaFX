package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group group = new Group();
        Scene scene = new Scene(group);

        Parent content = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/sample/form.fxml")));
        BorderPane root = new BorderPane();
        root.setCenter(content);
        group.getChildren().add(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("SoSelector");
        primaryStage.getIcons().add(new Image("/sample/IconSoSelector.png"));

        primaryStage.show();
    }
}
