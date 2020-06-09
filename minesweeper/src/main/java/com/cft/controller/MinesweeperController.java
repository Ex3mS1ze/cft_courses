package com.cft.controller;

import com.cft.api.PojoDifficulty;
import com.cft.model.MinesweeperGame;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MinesweeperController {
    private final MinesweeperGame minesweeperGame;

    public void handleLeftMouseButtonClickOnCell(int row, int column) {
        minesweeperGame.openCell(row, column);
    }

    public void handleMiddleMouseButtonClickOnCell(int row, int column) {
        minesweeperGame.openSurroundingCells(row, column);
    }

    public void handleRightMouseButtonClickOnCell(int row, int column) {
        minesweeperGame.switchCellFlag(row, column);
    }

    public void handlePressedF2() {
        minesweeperGame.startNewGame();
    }

    public void handleLeftMouseButtonClickOnNewGame() {
        minesweeperGame.startNewGame();
    }

    public void handleChangeDifficulty(PojoDifficulty difficulty) {
        minesweeperGame.changeDifficulty(difficulty);
    }

    public void handleLeftAndRightButtonsClickOnCell(int row, int column) {
        minesweeperGame.openSurroundingCells(row, column);
    }

    public void handleCellEnteredWithLeftMousePressed(int row, int column) {
        minesweeperGame.hoverCell(row, column);
    }

    public void handleCellExitedWithLeftMousePressed(int row, int column) {
        minesweeperGame.unhoverCell(row, column);
    }

    public void handleCellEnteredWithMiddleMousePressed(int row, int column) {
        minesweeperGame.hoverCellWithNeighbors(row, column);
    }

    public void handleCellExitedWithMiddleMousePressed(int row, int column) {
        minesweeperGame.unhoverCellWithNeighbors(row, column);
    }

    public void handleDifficultySettingsClicked() {
        minesweeperGame.openDifficultySettings();
    }

    public void handleLeaderBoardClicked() {
        minesweeperGame.openLeaderBoard();
    }

    public void handlePlayerSubmitName(String playerName) {
        minesweeperGame.setPlayerName(playerName);
    }
}
