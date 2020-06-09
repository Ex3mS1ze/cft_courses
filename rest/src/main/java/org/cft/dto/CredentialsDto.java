package org.cft.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CredentialsDto {
    @Email(message = "invalid email")
    private String email;
    @NotBlank(message = "can't be empty")
    private String oldPassword;
    @NotBlank(message = "can't be empty")
    @Pattern(regexp = "^\\w{2,}$", message = "at least 2 letters or digits, no whitespaces")
    private String newPassword;
}
