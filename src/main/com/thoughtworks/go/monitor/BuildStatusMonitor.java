package com.thoughtworks.go.monitor;

import com.thoughtworks.go.TalkToGo;
import com.thoughtworks.go.TalkToGo2Dot1;
import com.thoughtworks.go.domain.FeedEntry;
import com.thoughtworks.go.domain.Material;
import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.visitor.StageVisitor;
import com.thoughtworks.go.visitor.criteria.MatchingStageVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuildStatusMonitor {
    private final String pipelineName;
    private final String stageName;
    private final String GO_SERVER;

    Set<String> visitedStages = new HashSet<String>();

    List<BuildMonitorListener> listeners = new ArrayList<BuildMonitorListener>();

    private BlameMonitor blameMonitor = new BlameMonitor();
    private SongListener songListener = new SongListener();

    private boolean borked = false;

    public BuildStatusMonitor(String pipelineName, String stageName, String goServer) {
        this.pipelineName = pipelineName;
        this.stageName = stageName;
        this.GO_SERVER = goServer;

        BlameMonitor blameMonitor = new BlameMonitor();
        blameMonitor.createWindow();
        listeners.add(blameMonitor);
        listeners.add(new SongListener());

        blameMonitor.createWindow();
    }

    public void pollForNewCompletion() {
        Stage stage = latestStage();
        if (stage != null && !alreadySeen(stage)) {
            System.out.println("Found new stage with result " + stage.getResult());
            reportNewStage(stage);
        }
    }

    void reportNewStage(Stage stage) {
        visitedStages.add(stage.getStageLocator());

        List<String> users = checkinUsersForThisStage(stage);
        String user = users.get(0);

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

    private List<String> checkinUsersForThisStage(Stage stage) {
        Pipeline pipeline = stage.getPipeline();
        List<String> users = new ArrayList<String>();
        for (Material material : pipeline.materials()) {
            for (Material.Change change : material.getChanges()) {
                users.add(change.getUser());
            }
        }
        return users;
    }

    private Stage latestStage() {
        final List<Stage> stageListHack = new ArrayList<Stage>();
        TalkToGo talkToGo = new TalkToGo2Dot1(pipelineName, new HttpClientWrapper(GO_SERVER, 8153), false);

        talkToGo.visitStages(new StageVisitor() {
            public void visitStage(Stage stage) {
                if (!stage.getResult().equals("Cancelled")) {
                    stageListHack.add(stage);
                }
            }

            public void visitPipeline(Pipeline pipeline) {
            }
        }, new StopAfterFirstMatchCriteria(stageName));
        return stageListHack.isEmpty() ? null : stageListHack.get(0);
    }

    private class StopAfterFirstMatchCriteria extends MatchingStageVisitor {

        private boolean visitedOnce;

        public StopAfterFirstMatchCriteria(String stageName) {
            super(stageName);
        }

        @Override
        public boolean shouldVisit(FeedEntry feedEntry) {
            return visitedOnce = super.shouldVisit(feedEntry);
        }

        @Override
        public boolean shouldContinue() {
            return !visitedOnce;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(100);
        BuildStatusMonitor monitor = new BuildStatusMonitor("all", "build", "twu-ci");
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
