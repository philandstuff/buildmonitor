package com.thoughtworks.go.monitor;

import javax.swing.*;
import java.awt.*;

public class BuildStatusMonitor {
    private static final String goServer = "twu-ci";

    public static void main(String[] args) throws InterruptedException {
        BlameDisplay buildDisplay = new BlameDisplay();
        BlameDisplay wordpressDisplay = new BlameDisplay();

        JFrame frame = createFrame();
        frame.setLayout(new GridLayout(2,1));
        frame.add(buildDisplay.createWindow());
        frame.add(wordpressDisplay.createWindow());
        frame.setVisible(true);

        StageMonitor buildMonitor = new StageMonitor("all", "build", goServer, buildDisplay, new SongListener());
        StageMonitor wordpressMonitor = new StageMonitor("wordpress-cms", "manual-deploy-to-staging-server", goServer, wordpressDisplay, new SongListener());

        while (true) {
            try {
                buildMonitor.pollForNewCompletion();
                wordpressMonitor.pollForNewCompletion();
                Thread.sleep(10000);
                System.out.println("Polling...");
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame("Build Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        return frame;
    }
}
