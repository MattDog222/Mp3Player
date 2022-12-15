package app.stage;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import app.stage.impl.LibrarySelectionView;

import java.util.*;

/**
 * Singleton class to show and hide and views
 */
public class StageManager {

    private final Map<String, Stage> stages = new HashMap<>();

    public Stage initStage(String key) {
        return stages.computeIfAbsent(key, this::createStage);
    }

    public Pane getStage(String key) {
        return StageData.get(key).getPane();
    }

    private Stage createStage(String key) {
        StageData data = StageData.get(key);
        Parent root = data.getPane();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setTitle(data.getWindowName());
        stage.setScene(scene);
        return stage;
    }

    public void show(String key) {
        Stage stage = initStage(key);
        show(stage);
    }

    public void hide(String key) {
        Stage stage = initStage(key);
        hide(stage);
    }

    public void show(Stage stage) {
        stage.show();
    }

    public void hide(Stage stage) {
        stage.hide();
    }

    public void switchVisibility(String key) {
        Stage stage = initStage(key);
        if (stage.isShowing()) {
            hide(stage);
        } else {
            show(stage);
        }
    }

    private static StageManager instance = null;

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }

        return instance;
    }

    private StageManager() {

    }

    @Getter
    @AllArgsConstructor
    private enum StageData {
        SELECTION_STAGE("libraryselector", new LibrarySelectionView(), "Manage Libraries");

        private final String key;
        private final Pane pane;
        private final String windowName;

        private static StageData get(String key) {
            return Arrays.stream(VALUES)
                    .filter(scene -> scene.getKey().equalsIgnoreCase(key))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }

        private static final StageData[] VALUES = values();
    }
}
