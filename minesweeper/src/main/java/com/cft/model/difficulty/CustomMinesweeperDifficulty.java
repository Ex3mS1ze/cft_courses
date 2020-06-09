package com.cft.model.difficulty;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class CustomMinesweeperDifficulty implements MinesweeperDifficulty {
    private static final String NAME_TEMPLATE = DifficultyValuesAndConstraints.CUSTOM_DIFFICULTY_NAME + "(%sx%s, %s)";

    private final int rowQuantity;
    private final int columnQuantity;
    private final int mineQuantity;
    private final String name;

    public CustomMinesweeperDifficulty(Integer rowQuantity, Integer columnQuantity, Integer mineQuantity) {
        if (isValidVariables(rowQuantity, columnQuantity, mineQuantity)) {
            this.rowQuantity = rowQuantity;
            this.columnQuantity = columnQuantity;
            this.mineQuantity = mineQuantity;
        } else {
            this.rowQuantity = DifficultyValuesAndConstraints.MINIMUM_ROW_QUANTITY;
            this.columnQuantity = DifficultyValuesAndConstraints.MINIMUM_COLUMN_QUANTITY;
            this.mineQuantity = DifficultyValuesAndConstraints.MINIMUM_MINE_QUANTITY;

            log.info("Переданы некорректные параметры для сложности: rowQuantity = {}, columnQuantity = {}, mineQuantity = {}, используются минимальные значения",
                     rowQuantity, columnQuantity, mineQuantity);
        }

        this.name = String.format(NAME_TEMPLATE, this.rowQuantity, this.columnQuantity, this.mineQuantity);
    }

    private boolean isValidVariables(int rowQuantity, int columnQuantity, int mineQuantity) {
        return checkUpAndDownBounds(rowQuantity, columnQuantity, mineQuantity) &&
               rowQuantity * columnQuantity > mineQuantity;
    }

    private boolean checkUpAndDownBounds(int rowQuantity, int columnQuantity, int mineQuantity) {
        return rowQuantity <= DifficultyValuesAndConstraints.MAXIMUM_ROW_QUANTITY &&
               rowQuantity >= DifficultyValuesAndConstraints.MINIMUM_ROW_QUANTITY &&
               columnQuantity <= DifficultyValuesAndConstraints.MAXIMUM_COLUMN_QUANTITY &&
               columnQuantity >= DifficultyValuesAndConstraints.MINIMUM_COLUMN_QUANTITY &&
               mineQuantity <= DifficultyValuesAndConstraints.MAXIMUM_MINE_QUANTITY &&
               mineQuantity >= DifficultyValuesAndConstraints.MINIMUM_MINE_QUANTITY;
    }

    @Override
    public int getRowQuantity() {
        return rowQuantity;
    }

    @Override
    public int getColumnQuantity() {
        return columnQuantity;
    }

    @Override
    public int getMineQuantity() {
        return mineQuantity;
    }

    @Override
    public String getName() {
        return name;
    }
}
