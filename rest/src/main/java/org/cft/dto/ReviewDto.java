package org.cft.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ReviewDto {
    @Min(value = 1, message = "can't be less than 1")
    private long authorId;
    @NotBlank(message = "can't be empty")
    private String subject;
    @NotBlank(message = "can't be empty")
    private String text;
    @Min(value = 1, message = "can't be less than 1")
    @Max(value = 10, message = "can't be greater than 10")
    private Double rating;
}
