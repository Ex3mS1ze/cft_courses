package com.cft.model.score;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
public class MinesweeperGameResult {
    public static final int QUANTITY_OF_MANDATORY_FIELDS = 4;
    public static final String SEPARATOR = ";";

    private final String gameDifficultyName;
    private final long gameTime;
    private final LocalDateTime gameDate;
    private final String csvView;
    private final String playerName;

    public MinesweeperGameResult(String gameDifficultyName, long gameTime, LocalDateTime gameDate, String playerName) {
        this.gameDifficultyName = gameDifficultyName;
        this.gameTime = gameTime;
        this.gameDate = gameDate;
        this.playerName = playerName;
        this.csvView = makeCsvView(gameDifficultyName, gameTime, gameDate, playerName);
    }

    MinesweeperGameResult(String gameDifficultyName, long gameTime, String gameDate, String playerName) {
        this(gameDifficultyName, gameTime, LocalDateTime.parse(gameDate), playerName);
    }

    private String makeCsvView(String gameDifficultyName, long gameTime, LocalDateTime gameDate, String playerName) {
        return gameDifficultyName + SEPARATOR + gameTime + SEPARATOR + gameDate + SEPARATOR + playerName;
    }
}
