package com.thoughtworks.go.monitor;

public class BuildStatusMonitor {
    public static void main(String[] args) throws InterruptedException {
        BlameMonitor blameMonitor = new BlameMonitor();
        blameMonitor.createWindow();
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
