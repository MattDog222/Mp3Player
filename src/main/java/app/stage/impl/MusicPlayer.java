package app.stage.impl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class to launch the primary application view
 */
public class MusicPlayer extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/player.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/css/darktheme.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("CS498 Music Player");
        primaryStage.show();
    }
}
