package com.cft.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PojoDifficulty {
    private DifficultyCode difficultyCode;
    private int rowQuantity;
    private int columnQuantity;
    private int mineQuantity;
}

