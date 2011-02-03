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
    private JFrame frame;
    private JLabel userLabel;
    private JLabel statusLabel;
    private JLabel emailLabel;

    public void createWindow() {
        frame = new JFrame("BlameMonitor");
        userLabel = new JLabel("Blame Monitor");
        statusLabel = new JLabel("Blame Monitor");
        emailLabel = new JLabel("Blame Monitor");
        frame.getContentPane().setLayout(new GridLayout(3,1));
        frame.getContentPane().add(userLabel);
        frame.getContentPane().add(emailLabel);
        frame.getContentPane().add(statusLabel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);

        userLabel.setFont(new Font("Dialog",Font.PLAIN, 100));
        userLabel.setOpaque(false);
        statusLabel.setFont(new Font("Dialog",Font.PLAIN, 100));
        statusLabel.setOpaque(false);
        emailLabel.setFont(new Font("Dialog",Font.PLAIN, 100));
        emailLabel.setOpaque(false);
        frame.setVisible(true);

    }

    public void brokeTheBuild(String user) {
        Container container = frame.getContentPane();
        container.setBackground(Color.RED);
        showUser(user);
        statusLabel.setText("Broke the build");
    }

    public void fixedTheBuild(String user) {
        Container container = frame.getContentPane();
        container.setBackground(Color.GREEN);
        showUser(user);
        statusLabel.setText("Fixed the build");
    }

    public void pushedWorkingBuild(String user) {
        Container container = frame.getContentPane();
        container.setBackground(Color.GREEN);
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
