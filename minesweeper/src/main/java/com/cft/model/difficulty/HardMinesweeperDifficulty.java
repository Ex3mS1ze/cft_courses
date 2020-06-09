package com.cft.model.difficulty;

public class HardMinesweeperDifficulty implements MinesweeperDifficulty {
    @Override
    public int getRowQuantity() {
        return DifficultyValuesAndConstraints.MAXIMUM_ROW_QUANTITY;
    }

    @Override
    public int getColumnQuantity() {
        return DifficultyValuesAndConstraints.MAXIMUM_COLUMN_QUANTITY;
    }

    @Override
    public int getMineQuantity() {
        return DifficultyValuesAndConstraints.MAXIMUM_MINE_QUANTITY;
    }

    @Override
    public String getName() {
        return DifficultyValuesAndConstraints.HARD_DIFFICULTY_NAME;
    }
}