package app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

@Getter
public class SharedProperties {

    private final DoubleProperty volume;
    private final BooleanProperty looping;
    private final ObservableList<Path> songPaths = FXCollections.observableArrayList();

    private static SharedProperties instance = null;

    public static SharedProperties getInstance() {
        if (instance == null) {
            instance = new SharedProperties();
        }
        return instance;
    }

    public void replaceSongPaths(List<Path> songs) {
        this.songPaths.clear();
        this.songPaths.addAll(songs);
    }

    private SharedProperties() {
        volume = new SimpleDoubleProperty(0.25);
        looping = new SimpleBooleanProperty(false);
    }
}
