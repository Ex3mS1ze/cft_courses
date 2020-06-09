package org.cft.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

@Getter
@Setter
public class CriticDto {
    @Min(value = 1, message = "can't be less than 1")
    private long userId;
    @URL(message = "invalid url")
    private String photoUrl;
}
