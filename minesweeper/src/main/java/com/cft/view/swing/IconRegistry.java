package com.cft.view.swing;

import com.cft.api.CellCode;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class IconRegistry {
    private final Map<CellCode, ImageIcon> cellIconMap = new HashMap<>();

    IconRegistry() {
        cellIconMap.put(CellCode.MINE, new ImageIcon(IconRegistry.class.getResource("/images/cells/mine.png")));
        cellIconMap.put(CellCode.RED_MINE, new ImageIcon(IconRegistry.class.getResource("/images/cells/red_mine.png")));
        cellIconMap.put(CellCode.GREEN_MINE, new ImageIcon(IconRegistry.class.getResource("/images/cells/green_mine.png")));
        cellIconMap.put(CellCode.MARKED_AS_MINE,
                        new ImageIcon(IconRegistry.class.getResource("/images/cells/marked_cell.png")));
        cellIconMap.put(CellCode.CLOSED,
                        new ImageIcon(IconRegistry.class.getResource("/images/cells/closed_cell.png")));
        cellIconMap.put(CellCode.ZERO_MINES,
                        new ImageIcon(IconRegistry.class.getResource("/images/cells/0_mines.png")));
        cellIconMap.put(CellCode.ONE_MINE, new ImageIcon(IconRegistry.class.getResource("/images/cells/1_mine.png")));
        cellIconMap.put(CellCode.TWO_MINES, new ImageIcon(IconRegistry.class.getResource("/images/cells/2_mines.png")));
        cellIconMap.put(CellCode.THREE_MINES,
                        new ImageIcon(IconRegistry.class.getResource("/images/cells/3_mines.png")));
        cellIconMap.put(CellCode.FOUR_MINES,
                        new ImageIcon(IconRegistry.class.getResource("/images/cells/4_mines.png")));
        cellIconMap.put(CellCode.FIVE_MINES,
                        new ImageIcon(IconRegistry.class.getResource("/images/cells/5_mines.png")));
        cellIconMap.put(CellCode.SIX_MINES, new ImageIcon(IconRegistry.class.getResource("/images/cells/6_mines.png")));
        cellIconMap.put(CellCode.SEVEN_MINES,
                        new ImageIcon(IconRegistry.class.getResource("/images/cells/7_mines.png")));
        cellIconMap.put(CellCode.EIGHT_MINES,
                        new ImageIcon(IconRegistry.class.getResource("/images/cells/8_mines.png")));

        for (Map.Entry<CellCode, ImageIcon> iconEntry : cellIconMap.entrySet()) {
            ImageIcon imageIcon = iconEntry.getValue();
            iconEntry.setValue(new ImageIcon(imageIcon.getImage()
                                                      .getScaledInstance(SwingMinesweeperView.CELL_WIDTH,
                                                                         SwingMinesweeperView.CELL_HEIGHT,
                                                                         Image.SCALE_SMOOTH)));
        }
    }

    Optional<ImageIcon> getImageForCell(CellCode code) {
        return Optional.ofNullable(cellIconMap.get(code));
    }
}
