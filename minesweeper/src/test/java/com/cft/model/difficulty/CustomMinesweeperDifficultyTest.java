package com.cft.model.difficulty;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomMinesweeperDifficultyTest {
    @Test
    public void constructor_returnDifficultyWithMinimumRowAndColumnQuantity_whenPassedParametersOutOfBounds() {
        CustomMinesweeperDifficulty difficulty = new CustomMinesweeperDifficulty(-1, 0, 2);

        assertEquals(DifficultyValuesAndConstraints.MINIMUM_ROW_QUANTITY, difficulty.getRowQuantity());
        assertEquals(DifficultyValuesAndConstraints.MINIMUM_COLUMN_QUANTITY, difficulty.getColumnQuantity());
        assertEquals(DifficultyValuesAndConstraints.MINIMUM_MINE_QUANTITY, difficulty.getMineQuantity());
    }

    @Test
    public void constructor_returnDifficultyWithPassedParameters_whenPassedValidParameters() {
        int rowQuantity = 12;
        int columnQuantity = 12;
        int mineQuantity = 12;
        CustomMinesweeperDifficulty difficulty = new CustomMinesweeperDifficulty(rowQuantity, columnQuantity,
                                                                                 mineQuantity);

        assertEquals(rowQuantity, difficulty.getRowQuantity());
        assertEquals(columnQuantity, difficulty.getColumnQuantity());
        assertEquals(mineQuantity, difficulty.getMineQuantity());
    }
}