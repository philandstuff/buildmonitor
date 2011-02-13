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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PipelineReader {
    final String pipelineName;
    final String stageName;
    final String GO_SERVER;

    public PipelineReader(String pipelineName, String stageName, String go_server) {
        this.pipelineName = pipelineName;
        this.stageName = stageName;
        GO_SERVER = go_server;
    }

    Set<String> checkinUsersForThisStage(Stage stage) {
        Set<String> users = new HashSet<String>();
        for (Material material : stage.getPipeline().materials()) {
            for (Material.Change change : material.getChanges()) {
                users.add(change.getUser());
            }
        }
        return users;
    }

    public String commitsForThisStage(Stage stage) {
        StringBuilder commits = new StringBuilder();
        for (Material material : stage.getPipeline().materials()) {
            for (Material.Change change : material.getChanges()) {
                try {
                    Field commitMsg = Material.Change.class.getDeclaredField("message");
                    commitMsg.setAccessible(true);
                    commits.append(commitMsg.get(change) + "<br>");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return commits.toString();
    }

    Stage latestStage() {
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
}