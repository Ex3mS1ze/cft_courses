package org.cft.service;

import org.cft.dto.CredentialsDto;
import org.cft.entity.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;

public interface ExtendedUserDetailsService {
    UserDetails getPrincipal();

    UserDetailsImpl addUserDetails(UserDetailsImpl userDetails);

    boolean isIdBelongPrincipal(long id);

    boolean changePassword(long userId, CredentialsDto passwordDto);
}
