package app;

import app.controllers.PrimaryController;
import app.model.Song;
import javafx.scene.control.SelectionMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;

public class MusicPlayerHandler {
    private Song song = null;
    @Getter
    private MediaPlayer player = null;

    private final SharedProperties sharedProperties = SharedProperties.getInstance();
    private final PrimaryController controller;

    public MusicPlayerHandler(PrimaryController controller) {
        this.controller = controller;
    }

    public boolean isTrackSet() {
        return song != null;
    }

    public void playTrack(Song song) {
        this.song = song;
        if (player != null) {
            player.stop();
        }
        Media media = new Media(song.getFilePath().toUri().toString());
        player = new MediaPlayer(media);
        player.volumeProperty().bind(sharedProperties.getVolume());
        player.play();

        onEnd();
    }

    private void onEnd() {
        player.setOnEndOfMedia(() -> {
            if (sharedProperties.getLooping().get()) {
                player.seek(Duration.ZERO);
                player.play();
            } else {
                controller.getSongTable().getSelectionModel().selectNext();
                while (!controller.getSongTable().getSelectionModel().getSelectedItem().isLeaf()) {
                    controller.getSongTable().getSelectionModel().getSelectedItem().setExpanded(true);
                    controller.getSongTable().getSelectionModel().selectNext();
                }
                controller.getSongTable().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                controller.playTrack();
            }
        });
    }

    public void restartTrack() {
        player.seek(Duration.ZERO);
    }

    public void pauseTrack() {
        player.pause();
    }

    public void resumeTrack() {
        player.play();
    }
}
