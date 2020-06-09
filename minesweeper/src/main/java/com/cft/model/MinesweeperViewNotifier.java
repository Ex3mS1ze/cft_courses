package com.cft.model;

import com.cft.api.CellCode;
import com.cft.api.PojoDifficulty;
import com.cft.api.PojoGamerResult;
import com.cft.view.MinesweeperView;

import java.util.ArrayList;
import java.util.List;

class MinesweeperViewNotifier {
    private final List<MinesweeperView> minesweeperViews = new ArrayList<>();

    void attachView(MinesweeperView minesweeperView) {
        minesweeperViews.add(minesweeperView);
    }

    void detachView(MinesweeperView minesweeperView) {
        minesweeperViews.remove(minesweeperView);
    }

    void notifyViewsNewGame(int mineQuantity) {
        minesweeperViews.forEach(minesweeperView -> minesweeperView.renderNewGame(mineQuantity));
    }

    void notifyViewAboutCellStatusChanged(int row, int column, CellCode code) {
        minesweeperViews.forEach(minesweeperView -> minesweeperView.updateCell(row, column, code));
    }

    void notifyViewAboutGameWon() {
        minesweeperViews.forEach(MinesweeperView::renderGameWon);
    }

    void notifyViewAboutGameLose() {
        minesweeperViews.forEach(MinesweeperView::renderGameLose);
    }

    void notifyViewAboutTimeChange(long time) {
        minesweeperViews.forEach(minesweeperView -> minesweeperView.updateStopwatch(time));
    }

    void notifyViewAboutQuantityOfRemainingMinesChanged(int quantityOfRemainingMines) {
        minesweeperViews.forEach(
                minesweeperView -> minesweeperView.updateQuantityOfRemainingMines(quantityOfRemainingMines));
    }

    void notifyViewAboutDifficultyChanged(PojoDifficulty difficulty) {
        minesweeperViews.forEach(minesweeperView -> minesweeperView.updateDifficulty(difficulty));
    }

    void notifyViewAboutCurrentDifficulty(PojoDifficulty currentDifficulty) {
        minesweeperViews.forEach(minesweeperView -> minesweeperView.renderDifficultySettings(currentDifficulty));
    }

    void notifyViewAboutGameInitialized(PojoDifficulty difficulty) {
        minesweeperViews.forEach(minesweeperView -> minesweeperView.initialize(difficulty));
    }

    void notifyViewAboutHighScores(List<PojoGamerResult> gameResults) {
        minesweeperViews.forEach(minesweeperView -> minesweeperView.renderLeaderBoard(gameResults));
    }

    void notifyViewAboutPlayerNameRequested() {
        minesweeperViews.forEach(MinesweeperView::renderNameInput);
    }
}
