package com.cft.view;

import com.cft.api.CellCode;
import com.cft.api.PojoDifficulty;
import com.cft.api.PojoGamerResult;

import java.util.List;

public interface MinesweeperView {
    void renderNewGame(int mineQuantity);

    void renderNameInput();

    void renderGameWon();

    void renderGameLose();

    void renderLeaderBoard(List<PojoGamerResult> highScores);

    void renderDifficultySettings(PojoDifficulty currentDifficulty);

    void updateCell(int row, int column, CellCode code);

    void updateStopwatch(long time);

    void updateQuantityOfRemainingMines(int quantityOfRemainingMines);

    void updateDifficulty(PojoDifficulty difficulty);

    void initialize(PojoDifficulty difficulty);
}
