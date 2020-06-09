package com.cft.model.difficulty;

public class MediumMinesweeperDifficulty implements MinesweeperDifficulty {
    @Override
    public int getRowQuantity() {
        return DifficultyValuesAndConstraints.MAXIMUM_ROW_QUANTITY / 2;
    }

    @Override
    public int getColumnQuantity() {
        return DifficultyValuesAndConstraints.MAXIMUM_COLUMN_QUANTITY / 2;
    }

    @Override
    public int getMineQuantity() {
        return DifficultyValuesAndConstraints.MAXIMUM_MINE_QUANTITY / 2;
    }

    @Override
    public String getName() {
        return DifficultyValuesAndConstraints.MEDIUM_DIFFICULTY_NAME;
    }

}