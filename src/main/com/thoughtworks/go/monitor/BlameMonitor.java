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
    private JLabel label;

    public void createWindow() {
        frame = new JFrame("BlameMonitor");
        label = new JLabel("Blame Monitor");
        frame.getContentPane().add(label);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);

        label.setFont(new Font("Dialog",Font.PLAIN, 100));
        label.setOpaque(false);
        frame.setVisible(true);

    }

    public void brokeTheBuild(String user) {
        Container container = frame.getContentPane();
        container.setBackground(Color.RED);
        showUser(user);
    }

    public void fixedTheBuild(String user) {
        Container container = frame.getContentPane();
        container.setBackground(Color.GREEN);
        showUser(user);
    }

    public void pushedWorkingBuild(String user) {
        Container container = frame.getContentPane();
        container.setBackground(Color.GREEN);
        showUser(user);
    }

    private void showUser(String user) {
        label.setText(user);
    }


}
