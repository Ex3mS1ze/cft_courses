package com.cft.api;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PojoGamerResult {
    private String difficultyName;
    private long gameTime;
    private LocalDateTime gameDate;
    private String playerName;
}
