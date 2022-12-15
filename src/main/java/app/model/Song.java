package app.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.ToString;

import java.nio.file.Path;
import java.util.Map;

@ToString
public class Song {

    private SimpleStringProperty artist;
    private SimpleStringProperty album;
    private SimpleStringProperty title;
    private SimpleStringProperty length;
    private SimpleIntegerProperty trackNumber;
    private SimpleObjectProperty<Path> filePath;

    private boolean isArtistLabel;
    private boolean isAlbumLabel;

    private final static int UNKNOWN_TRACK_VALUE = Integer.MAX_VALUE;

    public Song() {
        this.artist = new SimpleStringProperty();
        this.album = new SimpleStringProperty();
        this.title = new SimpleStringProperty();
        this.length = new SimpleStringProperty();
        this.trackNumber = new SimpleIntegerProperty(UNKNOWN_TRACK_VALUE);
        this.filePath = new SimpleObjectProperty<>();
    }

    public Song(String artist, String album, String title, String length) {
        this.artist = new SimpleStringProperty(artist);
        this.album = new SimpleStringProperty(album);
        this.title = new SimpleStringProperty(title);
        this.length = new SimpleStringProperty(length);
        this.trackNumber = new SimpleIntegerProperty(UNKNOWN_TRACK_VALUE);
        this.filePath = new SimpleObjectProperty<>();
    }

    public String getArtist() {
        if (!this.getLength().isEmpty() && (artist.get() == null || artist.get().isEmpty())) {
            return "[unknown]";
        }
        return artist.get();
    }

    public SimpleStringProperty artistProperty() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public String getAlbum() {
        if (!this.getLength().isEmpty() && (album.get() == null || album.get().isEmpty())) {
            return "[unknown]";
        }
        return album.get();
    }

    public SimpleStringProperty albumProperty() {
        return album;
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getLength() {
        return length.get();
    }

    public SimpleStringProperty lengthProperty() {
        return length;
    }

    public void setLength(String length) {
        this.length.set(length);
    }

    public int getTrackNumber() {
        return trackNumber.get();
    }

    public String getTrackString() {
        if (this.getTrackNumber() == UNKNOWN_TRACK_VALUE) {
            return "";
        }
        return "" + this.getTrackNumber();
    }

    public SimpleIntegerProperty trackNumberProperty() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber.set(trackNumber);
    }

    public Path getFilePath() {
        return filePath.get();
    }

    public SimpleObjectProperty<Path> filePathProperty() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath.set(filePath);
    }

    public boolean isArtistLabel() {
        return isArtistLabel;
    }

    public void setArtistLabel(boolean artistLabel) {
        isArtistLabel = artistLabel;
    }

    public boolean isAlbumLabel() {
        return isAlbumLabel;
    }

    public void setAlbumLabel(boolean albumLabel) {
        isAlbumLabel = albumLabel;
    }

    public static Song of(Path path) {
        Song song = new Song();
        try {
            Map<String, String> map = MetadataParser.parse(path);
            song.setFilePath(path);
            String artist = "";
            artist = map.get("albumArtist");
            if (artist == null) {
                artist = map.get("artist");
            }
            song.setAlbum(map.get("album"));
            String track = map.get("trackNumber");
            if (track != null && !track.isEmpty()) {
                song.setTrackNumber(Integer.parseInt(track));
            }

            song.setTitle(map.get("title"));
            song.setLength(convertStringToLength(map.get("duration")));
            song.setArtist(artist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return song;
    }

    private static String convertStringToLength(String s) {
        double fullMinutes = Double.parseDouble(s) / 60;
        int minutes = (int) fullMinutes;
        int seconds = (int)((fullMinutes - minutes) * 60);
        return "" + minutes + ":" + String.format("%02d", seconds);
    }



}
