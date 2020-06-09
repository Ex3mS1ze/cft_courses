package com.cft.model.cell;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GameCell {
    private final int row;
    private final int column;

    private boolean isMined;
    private boolean isFlagged;
    private boolean isRevealed;

    private List<GameCell> surroundingCells;

    public GameCell(int row, int column, boolean isMined) {
        this.row = row;
        this.column = column;
        this.isMined = isMined;
    }
}
