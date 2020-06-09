package com.cft.model.difficulty;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DifficultyValuesAndConstraints {
    public static final int MINIMUM_ROW_QUANTITY = 9;
    public static final int MINIMUM_COLUMN_QUANTITY = 9;
    public static final int MINIMUM_MINE_QUANTITY = 10;

    public static final int MAXIMUM_ROW_QUANTITY = 24;
    public static final int MAXIMUM_COLUMN_QUANTITY = 24;
    public static final int MAXIMUM_MINE_QUANTITY = 60;

    public static final String EASY_DIFFICULTY_NAME = "Easy";
    public static final String MEDIUM_DIFFICULTY_NAME = "Medium";
    public static final String HARD_DIFFICULTY_NAME = "Hard";
    public static final String CUSTOM_DIFFICULTY_NAME = "Custom";
}
