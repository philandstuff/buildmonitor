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

/**
 * @understands Checking if a given stage broke.
 */
public class BuildStatusMonitor {
    private String pipelineName;
    private String stageName;
    Set<String> visitedStages = new HashSet<String>();
    private BlameMonitor blameMonitor = new BlameMonitor();
    private final String GO_SERVER = "twu-ci";
    private boolean borked = false;

    public BuildStatusMonitor(String pipelineName, String stageName) {
        this.pipelineName = pipelineName;
        this.stageName = stageName;
        blameMonitor.createWindow();
    }

    public boolean pollForNewCompletion() {
        Stage stage = latestStage();
        if (stage != null && !visitedStages.contains(stage.getStageLocator())) {
            System.out.println("Found new stage with result " + stage.getResult());
            visitedStages.add(stage.getStageLocator());

            if (!isBroken(stage) && !wasFixed(stage)) {
                return false;
            }

            List<String> users = checkinUsersForThisStage(stage);

            blameMonitor.blame(stage.getResult(), checkinUsersForThisStage(stage).get(0));
            SongPlayer.playSong(new NameToSongMapping(stage.getResult()).songForUser(users));

            borked = isBroken(stage);
            return true;
        }
        return false;
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
        BuildStatusMonitor monitor = new BuildStatusMonitor("all", "build");
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
