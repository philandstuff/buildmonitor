package com.thoughtworks.go.monitor;

import javax.swing.*;
import java.awt.*;

public class BlameDisplay implements BuildMonitorListener {
    public BlameDisplay(String title) {
        panel = new JPanel();
        JPanel titleNamePanel = new JPanel();
        titleNamePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        userLabel = new JLabel("Blame Monitor");
        infoLabel = new JLabel("Blame Monitor");
        JLabel titleLabel = new JLabel(title + ": ");
        titleNamePanel.add(titleLabel);
        titleNamePanel.add(userLabel);
        titleNamePanel.setOpaque(false);
        panel.setLayout(new GridLayout(2,1));
        panel.add(titleNamePanel);
        panel.add(infoLabel);

        userLabel.setFont(new Font("Dialog", Font.PLAIN, 72));
        userLabel.setOpaque(false);
        titleLabel.setFont(new Font("Dialog", Font.PLAIN, 72));
        titleLabel.setOpaque(false);
        infoLabel.setFont(new Font("Dialog", Font.PLAIN, 40));
        infoLabel.setOpaque(false);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setVisible(true);
    }

    private JPanel panel;
    private JLabel userLabel;
    private JLabel infoLabel;

    public JPanel getPanel() {
        return panel;
    }

    private void showWrappedInfo(String info) {
        infoLabel.setText("<html>"+info+"</html>");
    }

    public void brokeTheBuild(String user, String info) {
        panel.setBackground(Color.RED);
        userLabel.setText(user);
        showWrappedInfo(info);
    }

    public void fixedTheBuild(String user, String info) {
        panel.setBackground(Color.GREEN);
        userLabel.setText(user);
        showWrappedInfo(info);
    }

    public void pushedWorkingBuild(String user, String info) {
        panel.setBackground(Color.GREEN);
        userLabel.setText(user);
        showWrappedInfo(info);
    }
}
