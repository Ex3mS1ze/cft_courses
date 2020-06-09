package com.cft.view.swing;

import javax.swing.*;

class SwingMinesweeperInfoPanel extends JPanel {
    private static final String MINES_LEFT_TEXT = "Mines left: ";
    private static final String TIME_TEXT = "Time: ";

    SwingMinesweeperInfoPanel(SwingMinesweeperView view) {
        setBorder(BorderFactory.createEmptyBorder(SwingMinesweeperView.DEFAULT_GAP, SwingMinesweeperView.DEFAULT_GAP,
                                                  SwingMinesweeperView.DEFAULT_GAP, SwingMinesweeperView.DEFAULT_GAP));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        addTextField(MINES_LEFT_TEXT, view.getMinesLeftLabel());
        addTextField(TIME_TEXT, view.getStopwatchLabel());
    }

    private void addTextField(String text, JLabel label) {
        JTextField textFieldForTimePassedInfo = new JTextField();
        textFieldForTimePassedInfo.setEditable(false);
        textFieldForTimePassedInfo.setBorder(BorderFactory.createEmptyBorder());
        textFieldForTimePassedInfo.setText(text);
        add(textFieldForTimePassedInfo);
        add(label);
        add(Box.createHorizontalStrut(SwingMinesweeperView.DEFAULT_GAP));
    }
}
