package com.cft.model.difficulty;

public class EasyMinesweeperDifficulty implements MinesweeperDifficulty {
    @Override
    public int getRowQuantity() {
        return DifficultyValuesAndConstraints.MINIMUM_ROW_QUANTITY;
    }

    @Override
    public int getColumnQuantity() {
        return DifficultyValuesAndConstraints.MINIMUM_COLUMN_QUANTITY;
    }

    @Override
    public int getMineQuantity() {
        return DifficultyValuesAndConstraints.MINIMUM_MINE_QUANTITY;
    }

    @Override
    public String getName() {
        return DifficultyValuesAndConstraints.EASY_DIFFICULTY_NAME;
    }
}
