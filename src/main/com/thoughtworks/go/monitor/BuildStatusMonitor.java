package com.thoughtworks.go.monitor;

import com.thoughtworks.go.domain.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuildStatusMonitor {

    Set<String> visitedStages = new HashSet<String>();

    List<BuildMonitorListener> listeners = new ArrayList<BuildMonitorListener>();

    private boolean borked = false;
    private final PipelineReader pipelineReader;

    public BuildStatusMonitor(String pipelineName, String stageName, String goServer, BuildMonitorListener... listeners) {
        pipelineReader = new PipelineReader(pipelineName, stageName, goServer);

        for (BuildMonitorListener listener : listeners) {
            this.listeners.add(listener);
        }

    }

    public void pollForNewCompletion() {
        Stage stage = pipelineReader.latestStage();
        if (stage != null && !alreadySeen(stage)) {
            System.out.println("Found new stage with result " + stage.getResult());
            reportNewStage(stage);
        }
    }

    void reportNewStage(Stage stage) {
        visitedStages.add(stage.getStageLocator());

        Set<String> users = pipelineReader.checkinUsersForThisStage(stage);
        String user = users.iterator().next();

        for (BuildMonitorListener listener : listeners) {
            if (isBroken(stage)) {
                listener.brokeTheBuild(user);
            }
            else if (wasFixed(stage)) {
                listener.fixedTheBuild(user);
            }
            else {
                listener.pushedWorkingBuild(user);
            }
        }

        borked = isBroken(stage);
    }

    private boolean alreadySeen(Stage stage) {
        return visitedStages.contains(stage.getStageLocator());
    }

    private boolean wasFixed(Stage stage) {
        return borked && stage.getResult().equals("Passed");
    }

    private boolean isBroken(Stage stage) {
        return stage.getResult().equals("Failed");
    }

    public static void main(String[] args) throws InterruptedException {
        BlameMonitor blameMonitor = new BlameMonitor();
        blameMonitor.createWindow();
        BuildStatusMonitor monitor = new BuildStatusMonitor("all", "build", "twu-ci", blameMonitor, new SongListener());
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
