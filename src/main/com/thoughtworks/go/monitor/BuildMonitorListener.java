package com.thoughtworks.go.monitor;

public interface BuildMonitorListener {
    void brokeTheBuild(String user);

    void fixedTheBuild(String user);

    void pushedWorkingBuild(String user);
}
