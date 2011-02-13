package com.thoughtworks.go.monitor;

public interface BuildMonitorListener {
    void brokeTheBuild(String user, String info);

    void fixedTheBuild(String user, String info);

    void pushedWorkingBuild(String user, String info);
}
