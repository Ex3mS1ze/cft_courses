package org.cft.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AppRoles {
    USER("ROLE_USER"),
    CRITIC("ROLE_CRITIC"),
    ADMIN("ROLE_ADMIN");

    @Getter
    private final String name;
}
