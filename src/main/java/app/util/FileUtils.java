package app.util;

import app.SharedProperties;
import app.model.Song;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class FileUtils {

    private static final String DATA = System.getProperty("user.home") + "\\CS490\\music_player\\paths.txt";

    /**
     * Gets the file name with no extension.
     * @param path Path to file
     * @return file name without extension
     */
    public String getSimplePathName(Path path) {
        String name = path.getFileName().toString();
        return name.substring(0, name.lastIndexOf("."));
    }


    public Path getSuperDirectory(Path path) {
        String name = path.toString();
        return Path.of(name.substring(0, name.lastIndexOf("\\")));
    }

    public static boolean isMp3(Path path) {
        return path.getFileName().toString().endsWith(".mp3");
    }

    public static List<Song> convertToSongs(List<Path> songPaths) {
        return songPaths.stream().filter(Files::isRegularFile)
                .filter(FileUtils::isMp3)
                .map(Song::of)
                .toList();
    }

    @SneakyThrows({IOException.class})
    public static void loadAllSongs() {
        List<Path> allPaths = new ArrayList<>();
        Path p = Path.of(DATA);
        List<String> dirPaths = Files.readAllLines(p);

        for (String directory : dirPaths) {
            Path path = Path.of(directory);
            try (Stream<Path> folders = Files.walk(path)) {
                List<Path> songs = folders
                        .filter(Files::isRegularFile)
                        .filter(FileUtils::isMp3)
                        .collect(Collectors.toList());

                allPaths.addAll(songs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SharedProperties.getInstance().replaceSongPaths(allPaths);
    }

    @SneakyThrows({IOException.class})
    public static void writeSongs(String text) {
        Files.writeString(Path.of(DATA), text);
    }

    @SneakyThrows({IOException.class})
    public static List<String> readSongData() {
        return Files.readAllLines(Path.of(DATA));
    }
}
