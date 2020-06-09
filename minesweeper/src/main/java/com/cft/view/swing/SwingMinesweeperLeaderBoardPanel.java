package com.cft.view.swing;

import com.cft.api.PojoGamerResult;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

class SwingMinesweeperLeaderBoardPanel extends JPanel {
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    private static final Object[] HEADER = new String[]{"Player", "Difficulty", "Time, s", "Date"};

    SwingMinesweeperLeaderBoardPanel(List<PojoGamerResult> highScores) {
        super(new FlowLayout(FlowLayout.CENTER));

        Object[][] data = formatGameResult(highScores);
        add(new JScrollPane(new JTable(data, HEADER)));
    }

    private Object[][] formatGameResult(List<PojoGamerResult> highScores) {
        Object[][] data = new String[highScores.size()][4];

        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            data[rowIndex][0] = highScores.get(rowIndex).getPlayerName();
            data[rowIndex][1] = highScores.get(rowIndex).getDifficultyName();
            data[rowIndex][2] = String.valueOf(highScores.get(rowIndex).getGameTime());
            data[rowIndex][3] = DATE_TIME_FORMATTER.format(highScores.get(rowIndex).getGameDate());
        }

        return data;
    }
}
