package com.thoughtworks.go.monitor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ppotter
 * Date: 02/02/2011
 * Time: 20:06
 * To change this template use File | Settings | File Templates.
 */
public class BlameMonitor implements BuildMonitorListener {
    public BlameMonitor() {
    }

    private JPanel panel;
    private JLabel userLabel;
    private JLabel statusLabel;
    private JLabel emailLabel;

    public JPanel createWindow() {
        panel = new JPanel();
        userLabel = new JLabel("Blame Monitor");
        statusLabel = new JLabel("Blame Monitor");
        emailLabel = new JLabel("Blame Monitor");
        panel.setLayout(new GridLayout(3, 1));
        panel.add(userLabel);
        panel.add(emailLabel);
        panel.add(statusLabel);

        userLabel.setFont(new Font("Dialog",Font.PLAIN, 100));
        userLabel.setOpaque(false);
        statusLabel.setFont(new Font("Dialog",Font.PLAIN, 100));
        statusLabel.setOpaque(false);
        emailLabel.setFont(new Font("Dialog",Font.PLAIN, 100));
        emailLabel.setOpaque(false);
        panel.setVisible(true);
        return panel;
    }

    public void brokeTheBuild(String user) {
        panel.setBackground(Color.RED);
        showUser(user);
        statusLabel.setText("Broke the build");
    }

    public void fixedTheBuild(String user) {
        panel.setBackground(Color.GREEN);
        showUser(user);
        statusLabel.setText("Fixed the build");
    }

    public void pushedWorkingBuild(String user) {
        panel.setBackground(Color.GREEN);
        showUser(user);
        statusLabel.setText("Pushed");
    }

    private void showUser(String user) {
        String[] splits = user.split("[<>]");
        for (String split : splits) { System.out.println(split); }
        userLabel.setText(splits[0]);
        emailLabel.setText(splits[1]);
    }


}
