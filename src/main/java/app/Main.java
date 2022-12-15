package app;

import app.stage.impl.MusicPlayer;
import javafx.application.Application;

/**
 * Simple main class, must be configured like this for Jars to work
 */
public class Main {
    public static void main(String[] args) {
        Application.launch(MusicPlayer.class);
    }
}
