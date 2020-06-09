package org.cft.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserDto {
    @Email(message = "invalid email")
    private String email;
    @NotBlank(message = "can't be empty")
    private String firstName;
    @NotBlank(message = "can't be empty")
    private String secondName;
    @NotBlank(message = "can't be empty")
    @Pattern(regexp = "^\\w{2,}$", message = "at least 2 letters or digits, no whitespaces")
    private String password;
}