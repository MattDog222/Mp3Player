package app.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import app.stage.StageManager;
import app.util.FileUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;


public class LibrarySelectorController implements Initializable {
    @FXML
    private Button addLibraryPathButton, deleteLibraryPathButton, saveAndClose;

    @FXML
    private ListView<String> libraryList;

    private final StageManager stageManager = StageManager.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
        this.libraryList.setItems(FXCollections.observableArrayList(FileUtils.readSongData()));
    }

    private void addListeners() {
        saveAndClose.setOnMouseClicked( e -> {
            saveTextFile();
            FileUtils.loadAllSongs();
            stageManager.hide("libraryselector");
        });

        deleteLibraryPathButton.setOnMouseClicked( e -> {
            if (libraryList.getSelectionModel().getSelectedIndex() != -1) {
                libraryList.getItems().remove(libraryList.getSelectionModel().getSelectedIndex());
            }
        });

        addLibraryPathButton.setOnMouseClicked( e-> {
            DirectoryChooser dc = new DirectoryChooser();
            File selected = dc.showDialog(null);
            if (selected != null) {
                Path path = selected.toPath();
                libraryList.getItems().add(path.toString());
            }
        });

    }

    private void saveTextFile() {
        StringJoiner sj = new StringJoiner("\n");
        libraryList.getItems().forEach(sj::add);
        try {
            FileUtils.writeSongs(sj.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
