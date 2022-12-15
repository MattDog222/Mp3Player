package app.controllers;

import app.MusicPlayerHandler;
import app.PlayButtonHandler;
import app.SharedProperties;
import app.model.Song;
import app.stage.StageManager;
import app.util.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

public class PrimaryController implements Initializable, ListChangeListener<Path> {

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private VBox bottomPane;

    @FXML
    private BorderPane trackNavigation;

    @FXML
    private CheckBox loopCheckBox;

    @FXML
    private HBox seekBox;

    @FXML
    private ImageView backButton;

    @FXML
    private ImageView forwardButton;

    @FXML
    private ImageView playButton;

    @FXML
    private Slider volumeSlider;

    @FXML
    private MenuItem menuLibrarySelection;
    @FXML
    private MenuItem exitButton;

    @FXML
    private ProgressBar progressBar;

    @Getter
    @FXML
    private TreeTableView<Song> songTable;

    @FXML
    private TreeTableColumn<Song, String> artist;
    @FXML
    private TreeTableColumn<Song, String> album;
    @FXML
    private TreeTableColumn<Song, String> title;
    @FXML
    private TreeTableColumn<Song, String> length;
    @FXML
    private TreeTableColumn<Song, String> track;

    private PlayButtonHandler playButtonHandler;
    private MusicPlayerHandler musicPlayerHandler;

    private final Map<String, Image> images = new HashMap<>();

    private final SharedProperties sharedProperties = SharedProperties.getInstance();
    private final StageManager stageManager = StageManager.getInstance();


    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playButtonHandler = new PlayButtonHandler();
        musicPlayerHandler = new MusicPlayerHandler(this);
        loadImages();
        sharedProperties.getVolume().bind(volumeSlider.valueProperty());
        sharedProperties.getLooping().bind(loopCheckBox.selectedProperty());
        initInputListeners();
        menuLibrarySelection.setOnAction(e -> stageManager.show("libraryselector"));
        exitButton.setOnAction(e -> System.exit(0));
        sharedProperties.getSongPaths().addListener(this);
        FileUtils.loadAllSongs();
    }

    public void setSongList(List<Path> paths) {
        List<Song> songs = FileUtils.convertToSongs(paths);

        artist.setCellValueFactory((TreeTableColumn.CellDataFeatures<Song, String> param) ->
                new ReadOnlyStringWrapper(param.getValue().getValue() == null ? "" : param.getValue().getValue().getArtist())
        );
        album.setCellValueFactory((TreeTableColumn.CellDataFeatures<Song, String> param) ->
                new ReadOnlyStringWrapper(param.getValue().getValue() == null ? "" : param.getValue().getValue().getAlbum())
        );
        title.setCellValueFactory((TreeTableColumn.CellDataFeatures<Song, String> param) ->
                new ReadOnlyStringWrapper(param.getValue().getValue() == null ? "" : param.getValue().getValue().getTitle())
        );
        length.setCellValueFactory((TreeTableColumn.CellDataFeatures<Song, String> param) ->
                new ReadOnlyStringWrapper(param.getValue().getValue() == null ? "" : param.getValue().getValue().getLength())
        );
        track.setCellValueFactory((TreeTableColumn.CellDataFeatures<Song, String> param) ->
                new ReadOnlyStringWrapper(param.getValue().getValue() == null ? "" : param.getValue().getValue().getTrackString())
        );

        TreeItem<Song> treeRoot = new TreeItem<>(null);

        Map<String, List<Song>> artistGroups = songs.stream().collect(groupingBy(Song::getArtist));
        artistGroups.forEach((artist, songList) -> {
            Song artistLabel = new Song(artist, "", "", "");
            artistLabel.setArtistLabel(true);
            TreeItem<Song> artistRoot = new TreeItem<>(artistLabel);
            treeRoot.getChildren().add(artistRoot);

            Map<String, List<Song>> albumGroups = songList.stream().collect(groupingBy(Song::getAlbum));
            albumGroups.forEach((albumName, albumSongs) -> {
                Song albumLabel = new Song(albumName, "", "", "");
                albumLabel.setAlbumLabel(true);
                TreeItem<Song> albumRoot = new TreeItem<>(albumLabel);
                artistRoot.getChildren().add(albumRoot);
                albumSongs.sort(Comparator.comparing(Song::getTrackNumber));
                for (Song song : albumSongs) {
                    var leaf = new TreeItem<>(song);
                    albumRoot.getChildren().add(leaf);
                }
            });
        });

        songTable.setRoot(treeRoot);
        songTable.setShowRoot(false);
    }

    private void initInputListeners() {
        trackChangeHandler();
        trackSeekHandling();
        initTrackSeekHanders();
    }


    @SneakyThrows(URISyntaxException.class)
    private void loadImages() throws IOException {
        images.put("back", new Image(getClass().getResource("/images/playbuttons/back.png").toURI().toString()));
        images.put("back_hover", new Image(getClass().getResource("/images/playbuttons/back_hover.png").toURI().toString()));
        images.put("pause_button", new Image(getClass().getResource("/images/playbuttons/pause_button.png").toURI().toString()));
        images.put("pause_button_hover", new Image(getClass().getResource("/images/playbuttons/pause_button_hover.png").toURI().toString()));
        images.put("play_button", new Image(getClass().getResource("/images/playbuttons/play_button.png").toURI().toString()));
        images.put("play_button_hover", new Image(getClass().getResource("/images/playbuttons/play_button_hover.png").toURI().toString()));
        images.put("skip", new Image(getClass().getResource("/images/playbuttons/skip.png").toURI().toString()));
        images.put("skip_hover", new Image(getClass().getResource("/images/playbuttons/skip_hover.png").toURI().toString()));
    }

    public boolean playTrack() {
        Song selectedTrack = songTable.getSelectionModel().getSelectedItem().getValue();
        if (selectedTrack == null || !songTable.getSelectionModel().getSelectedItem().isLeaf()) {
            return false;
        }
        musicPlayerHandler.playTrack(selectedTrack);

        MediaPlayer player = musicPlayerHandler.getPlayer();
        player.currentTimeProperty().addListener((observable, oldValue, currentTime) -> {
            double currentProgress = currentTime.toMillis() / player.getStopTime().toMillis();
            progressBar.setProgress(currentProgress);
        });
        return true;
    }

    private boolean resumeTrack() {
        if (!musicPlayerHandler.isTrackSet()) {
            return false;
        }
        musicPlayerHandler.resumeTrack();
        return true;
    }

    private boolean pauseTrack() {
        if (!musicPlayerHandler.isTrackSet()) {
            return false;
        }
        musicPlayerHandler.pauseTrack();
        return true;
    }


    private void initTrackSeekHanders() {
        playPauseHandler();
        backButtonHandler();
        forwardButtonHandler();
    }

    private void trackChangeHandler() {
        songTable.getSelectionModel().selectedItemProperty().addListener((changed, oldVal, newVal) -> {
            if (newVal != null && newVal.isLeaf()) {
                playTrack();
                playButtonHandler.setState(PlayButtonHandler.State.PLAYING);
                playButton.setImage(images.get("pause_button"));
            }
        });

    }

    private void trackSeekHandling() {
        progressBar.setOnMouseClicked(e -> {
            if (musicPlayerHandler.getPlayer() == null)
                return;

            Duration length = musicPlayerHandler.getPlayer().getStopTime();
            double percentageIntoTrack = e.getX() / progressBar.getWidth();
            musicPlayerHandler.getPlayer().seek(
                    length.multiply(percentageIntoTrack)
            );
        });
    }

    private void backButtonHandler() {
        backButton.setOnMousePressed(event -> {
            if (songTable.getSelectionModel().getSelectedItem() == null) {
                return;
            }

            if (musicPlayerHandler.getPlayer() != null
                    && musicPlayerHandler.getPlayer().currentTimeProperty().getValue().toSeconds() > 5) {
                // restart track
                musicPlayerHandler.restartTrack();
            }
            else {
                Song original = songTable.getSelectionModel().getSelectedItem().getValue();
                songTable.getSelectionModel().selectPrevious();
                if (!isSongSelected()) {
                    if (original.isArtistLabel() || original.isAlbumLabel()) {
                        selectFirstFromCollapsed();
                        return;
                    }
                    songTable.getSelectionModel().selectPrevious();
                    if (songTable.getSelectionModel().getSelectedItem().getValue().isArtistLabel()) {
                        songTable.getSelectionModel().selectPrevious();
                    }
                    selectFirstFromCollapsed();
                }
            }

        });

        backButton.setOnMouseEntered(event ->
                backButton.setImage(images.get("back_hover"))
        );

        backButton.setOnMouseExited(event ->
                backButton.setImage(images.get("back"))
        );
    }

    private void selectFirstFromCollapsed() {
        while (!songTable.getSelectionModel().getSelectedItem().isLeaf()) {
            songTable.getSelectionModel().getSelectedItem().setExpanded(true);
            songTable.getSelectionModel().selectNext();
        }
    }

    private boolean isSongSelected() {
        return songTable.getSelectionModel().getSelectedItem().isLeaf();
    }


    private void forwardButtonHandler() {
        forwardButton.setOnMousePressed(event -> {
            if (songTable.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            songTable.getSelectionModel().selectNext();
            while (!songTable.getSelectionModel().getSelectedItem().isLeaf()) {
                songTable.getSelectionModel().getSelectedItem().setExpanded(true);
                songTable.getSelectionModel().selectNext();
            }
        });

        forwardButton.setOnMouseEntered(event ->
                forwardButton.setImage(images.get("skip_hover"))
        );

        forwardButton.setOnMouseExited(event ->
                forwardButton.setImage(images.get("skip"))
        );
    }

    private void playPauseHandler() {
        playButton.setOnMousePressed(event -> {
            // if we haven't selected a track then the selected index will be -1 therefore we're returning the NOT_PLAYING state
            if (songTable.getSelectionModel().getSelectedIndex() >= 0) {
                if (!isSongSelected()) {
                    this.selectFirstFromCollapsed();
                    playTrack();
                    playButtonHandler.setState(PlayButtonHandler.State.PLAYING);
                    playButton.setImage(images.get("pause_button_hover"));
                    return;
                }
                var x = playButtonHandler.toggle();
                switch (x) {
                    case PLAYING -> {
                        boolean successful = resumeTrack();
                        if (successful) {
                            playButton.setImage(images.get("pause_button_hover"));
                        }
                    }
                    case PAUSED -> {
                        boolean successful = pauseTrack();
                        if (successful) {
                            playButton.setImage(images.get("play_button_hover"));
                        }
                    }
                }
            }
        });

        playButton.setOnMouseEntered(event -> {

            Image image = switch (playButtonHandler.getState()) {
                case NOT_PLAYING, PAUSED -> images.get("play_button_hover");
                case PLAYING -> images.get("pause_button_hover");
            };

            playButton.setImage(image);
        });

        playButton.setOnMouseExited(event -> {

            Image image = switch (playButtonHandler.getState()) {
                case NOT_PLAYING, PAUSED -> images.get("play_button");
                case PLAYING -> images.get("pause_button");
            };

            playButton.setImage(image);
        });
    }

    // Observe file source change
    @Override
    public void onChanged(Change<? extends Path> c) {
        setSongList(sharedProperties.getSongPaths());
    }
}
