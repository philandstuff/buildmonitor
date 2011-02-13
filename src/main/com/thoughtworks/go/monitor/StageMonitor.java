package com.thoughtworks.go.monitor;

import com.thoughtworks.go.domain.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StageMonitor {
    Set<String> visitedStages = new HashSet<String>();
    List<BuildMonitorListener> listeners = new ArrayList<BuildMonitorListener>();
    boolean borked = false;
    final PipelineReader pipelineReader;

    public StageMonitor(String pipelineName, String stageName, String goServer, BuildMonitorListener... listeners) {
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
        String commit = pipelineReader.commitsForThisStage(stage);
        String user = trim(users.iterator().next());

        for (BuildMonitorListener listener : listeners) {
            if (isBroken(stage)) {
                listener.brokeTheBuild(user, commit);
            } else if (wasFixed(stage)) {
                listener.fixedTheBuild(user, commit);
            } else {
                listener.pushedWorkingBuild(user, commit);
            }
        }

        borked = isBroken(stage);
    }

    private String trim(String userAndEmail) {
        return userAndEmail.substring(0,userAndEmail.indexOf("<"));
    }

    boolean alreadySeen(Stage stage) {
        return visitedStages.contains(stage.getStageLocator());
    }

    boolean wasFixed(Stage stage) {
        return borked && stage.getResult().equals("Passed");
    }

    boolean isBroken(Stage stage) {
        return stage.getResult().equals("Failed");
    }
}