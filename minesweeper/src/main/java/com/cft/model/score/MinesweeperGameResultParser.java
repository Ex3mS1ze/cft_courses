package com.cft.model.score;

import com.google.common.primitives.Longs;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class MinesweeperGameResultParser {
    private static final int INDEX_OF_DIFFICULTY_NAME_IN_FILE = 0;
    private static final int INDEX_OF_GAME_TIME_IN_FILE = 1;
    private static final int INDEX_OF_GAME_DATE_IN_FILE = 2;
    private static final int INDEX_OF_PLAYER_NAME_IN_FILE = 3;

    @SuppressWarnings("UnstableApiUsage")
    List<MinesweeperGameResult> parseStringsToGameResults(List<String> linesFromResultFile) {
        List<MinesweeperGameResult> minesweeperGameResults = new ArrayList<>();

        for (int numberOfLine = 0; numberOfLine < linesFromResultFile.size(); numberOfLine++) {
            String[] wordsFromLine = linesFromResultFile.get(numberOfLine).split(MinesweeperGameResult.SEPARATOR);

            if (wordsFromLine.length == MinesweeperGameResult.QUANTITY_OF_MANDATORY_FIELDS) {
                Long parsedGameTime = Longs.tryParse(wordsFromLine[INDEX_OF_GAME_TIME_IN_FILE]);
                if (parsedGameTime == null) {
                    log.warn("Запись в файле результатов в строке {} пропущена, т.к. содержит некорретное время",
                             numberOfLine);
                    continue;
                }

                minesweeperGameResults.add(
                        new MinesweeperGameResult(wordsFromLine[INDEX_OF_DIFFICULTY_NAME_IN_FILE],
                                                  parsedGameTime,
                                                  wordsFromLine[INDEX_OF_GAME_DATE_IN_FILE],
                                                  wordsFromLine[INDEX_OF_PLAYER_NAME_IN_FILE]));
            }
        }

        return minesweeperGameResults;
    }
}
