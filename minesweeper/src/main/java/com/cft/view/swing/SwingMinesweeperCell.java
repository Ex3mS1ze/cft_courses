package com.cft.view.swing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;

@Getter
@AllArgsConstructor
class SwingMinesweeperCell extends JLabel {
    private final int row;
    private final int column;
}
