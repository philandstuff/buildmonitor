package com.thoughtworks.go.monitor;

import javax.swing.*;

public class BuildStatusMonitor {
    public static void main(String[] args) throws InterruptedException {
        BlameMonitor blameMonitor = new BlameMonitor();

        JFrame frame = new JFrame("Build Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
        frame.add(blameMonitor.createWindow());
        frame.setVisible(true);

        StageMonitor monitor = new StageMonitor("all", "build", "twu-ci", blameMonitor, new SongListener());

        while (true) {
            try {
                monitor.pollForNewCompletion();
                Thread.sleep(10000);
                System.out.println("Polling...");
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }
}
