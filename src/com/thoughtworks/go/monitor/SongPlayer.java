package com.thoughtworks.go.monitor;

import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class SongPlayer {

    public static void playSong(String songPath) {
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
}