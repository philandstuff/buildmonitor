package com.thoughtworks.go.monitor;

import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class SongListener implements BuildMonitorListener {

    private static final String BROKEN_SONG = "failed.mp3";
    private static final String FIXED_SONG = "fixed.mp3";
    private static final String PASSED_SONG = "passed.mp3";

    public void playSong(String songPath) {
        try {
            System.out.println("Playing song " + songPath);
            FileInputStream soundFile = new FileInputStream(songPath);
            BufferedInputStream bis = new BufferedInputStream(soundFile);
            AdvancedPlayer player = new AdvancedPlayer(bis);
            player.play(700);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void brokeTheBuild(String user) {
        playSong(BROKEN_SONG);
    }

    public void fixedTheBuild(String user) {
        playSong(FIXED_SONG);
    }

    public void pushedWorkingBuild(String user) {
        playSong(PASSED_SONG);
    }
}