package com.cft.util;

import com.cft.api.PojoGamerResult;
import com.cft.model.score.MinesweeperGameResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameResultConverter {
    public static PojoGamerResult toPojo(MinesweeperGameResult minesweeperGameResult) {
        PojoGamerResult pojoGamerResult = new PojoGamerResult();
        pojoGamerResult.setDifficultyName(minesweeperGameResult.getGameDifficultyName());
        pojoGamerResult.setGameDate(minesweeperGameResult.getGameDate());
        pojoGamerResult.setGameTime(minesweeperGameResult.getGameTime());
        pojoGamerResult.setPlayerName(minesweeperGameResult.getPlayerName());

        return pojoGamerResult;
    }
}
