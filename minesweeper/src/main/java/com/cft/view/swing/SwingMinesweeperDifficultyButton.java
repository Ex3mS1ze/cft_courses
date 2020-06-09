package com.cft.view.swing;

import com.cft.api.DifficultyCode;
import lombok.Getter;

import javax.swing.*;

class SwingMinesweeperDifficultyButton extends JRadioButton {
    @Getter
    private DifficultyCode difficultyCode;

    SwingMinesweeperDifficultyButton(String name, DifficultyCode difficultyCode) {
        super(name);
        this.difficultyCode = difficultyCode;
    }
}
