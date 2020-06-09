package com.cft.util;

import com.cft.api.DifficultyCode;
import com.cft.api.PojoDifficulty;
import com.cft.model.difficulty.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DifficultyConverter {
    private static final BiMap<DifficultyCode, Class<? extends MinesweeperDifficulty>> CODE_TO_DIFFICULTY =
            EnumHashBiMap.create(DifficultyCode.class);

    static {
        CODE_TO_DIFFICULTY.put(DifficultyCode.EASY, EasyMinesweeperDifficulty.class);
        CODE_TO_DIFFICULTY.put(DifficultyCode.MEDIUM, MediumMinesweeperDifficulty.class);
        CODE_TO_DIFFICULTY.put(DifficultyCode.HARD, HardMinesweeperDifficulty.class);
        CODE_TO_DIFFICULTY.put(DifficultyCode.CUSTOM, CustomMinesweeperDifficulty.class);
    }

    public static PojoDifficulty toPojo(MinesweeperDifficulty minesweeperDifficulty) {
        PojoDifficulty pojoDifficulty = new PojoDifficulty();
        pojoDifficulty.setRowQuantity(minesweeperDifficulty.getRowQuantity());
        pojoDifficulty.setColumnQuantity(minesweeperDifficulty.getColumnQuantity());
        pojoDifficulty.setMineQuantity(minesweeperDifficulty.getMineQuantity());
        pojoDifficulty.setDifficultyCode(CODE_TO_DIFFICULTY.inverse().get(minesweeperDifficulty.getClass()));

        return pojoDifficulty;
    }

    public static MinesweeperDifficulty toModel(PojoDifficulty pojoDifficulty) {
        MinesweeperDifficulty minesweeperDifficulty;
        if (pojoDifficulty.getDifficultyCode().equals(DifficultyCode.CUSTOM)) {
            minesweeperDifficulty = new CustomMinesweeperDifficulty(pojoDifficulty.getRowQuantity(),
                                                                    pojoDifficulty.getColumnQuantity(),
                                                                    pojoDifficulty.getMineQuantity());
        } else {
            try {
                minesweeperDifficulty = CODE_TO_DIFFICULTY.get(pojoDifficulty.getDifficultyCode()).newInstance();
            } catch (InstantiationException | IllegalAccessException ignore) {
                log.info("Не удалось создать сложность из переданного pojoDifficulty, используется сложность Easy");
                minesweeperDifficulty = new EasyMinesweeperDifficulty();
            }
        }

        return minesweeperDifficulty;
    }
}
