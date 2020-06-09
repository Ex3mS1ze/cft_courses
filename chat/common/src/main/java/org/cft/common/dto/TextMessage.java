package org.cft.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TextMessage implements Serializable {
    private Participant author;
    private String text;
    private LocalDateTime creationDate = LocalDateTime.now();

    public TextMessage(Participant author, String text) {
        this.author = author;
        this.text = text;
    }
}
