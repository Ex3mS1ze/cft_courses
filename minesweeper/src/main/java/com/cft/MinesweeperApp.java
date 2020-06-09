package com.cft;

import com.cft.controller.MinesweeperController;
import com.cft.model.MinesweeperGame;
import com.cft.model.difficulty.EasyMinesweeperDifficulty;
import com.cft.model.difficulty.MinesweeperDifficulty;
import com.cft.view.MinesweeperView;
import com.cft.view.swing.SwingMinesweeperView;

public class MinesweeperApp {
    public static void main(String[] args) {
        MinesweeperDifficulty easyMinesweeperDifficulty = new EasyMinesweeperDifficulty();
        runMinesweeper(easyMinesweeperDifficulty);
    }

    private static void runMinesweeper(MinesweeperDifficulty difficulty) {
        MinesweeperGame minesweeperGame = new MinesweeperGame(difficulty);
        MinesweeperController minesweeperController = new MinesweeperController(minesweeperGame);
        MinesweeperView minesweeperView = new SwingMinesweeperView(minesweeperController);

        minesweeperGame.attachView(minesweeperView);
        minesweeperGame.startNewGame();
    }
}
