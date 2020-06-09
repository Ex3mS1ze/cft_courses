package org.cft.view;

import lombok.Getter;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;

@Getter
class SwingMultipleInputPane extends JPanel {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 20;

    private static final Dimension DIMENSION = new Dimension(WIDTH, HEIGHT);

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "6666";

    private final JTextField hostField;
    private final JFormattedTextField portField;

    SwingMultipleInputPane() {
        super();

        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(numberInstance);
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0);


        portField = new JFormattedTextField(formatter);
        portField.setPreferredSize(DIMENSION);
        portField.setText(DEFAULT_PORT);
        hostField = new JTextField();
        hostField.setText(DEFAULT_HOST);
        hostField.setPreferredSize(DIMENSION);

        add(new JLabel("Host: "));
        add(hostField);
        add(new JLabel("Port: "));
        add(portField);
    }
}
