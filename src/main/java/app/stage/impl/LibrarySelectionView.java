package app.stage.impl;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import app.controllers.LibrarySelectorController;

import java.io.IOException;


public class LibrarySelectionView extends AnchorPane {

    private final static String TITLE = "Manage Libraries"; // put this in the enum instead
    private final static String FXML_PATH = "/fxml/libSelector.fxml";
    private final static String CSS_PATH = "/css/darktheme.css";

    public LibrarySelectionView() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(FXML_PATH));
        loader.setRoot(this);
        getStylesheets().add(this.getClass().getResource(CSS_PATH).toExternalForm());
        loader.setController(new LibrarySelectorController());

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
