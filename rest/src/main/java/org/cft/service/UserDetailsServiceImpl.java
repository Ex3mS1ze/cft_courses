package org.cft.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cft.dto.CredentialsDto;
import org.cft.entity.User;
import org.cft.entity.UserDetailsImpl;
import org.cft.repository.UserDetailsRepository;
import org.cft.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService, ExtendedUserDetailsService {
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails getPrincipal() {
        return (UserDetails)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Cacheable("userDetailsByEmail")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Попытка получить UserDetails для пользователя с email = {}", username);

        Optional<User> userFromDb = userRepository.findByEmail(username);

        if (userFromDb.isEmpty()) {
            log.warn("Пользователь с email = '{}' не был найден", username);
            throw new UsernameNotFoundException(username);
        }

        log.info("Получены UserDetails для пользователя с email = {}", username);
        return userFromDb.get().getUserDetails();
    }

    @CachePut(value = "userDetailsByEmail", key = "#result.username")
    @Override
    public UserDetailsImpl addUserDetails(UserDetailsImpl userDetails) {
        log.info("Добавление UserDetails для пользователя с email = {}", userDetails.getUsername());
        return userDetailsRepository.save(userDetails);
    }

    @Override
    public boolean isIdBelongPrincipal(long id) {
        log.info("Проверка принадлежности id = {} текущему пользователю", id);
        return ((UserDetailsImpl) getPrincipal()).getId() == id;
    }

    @CacheEvict(value = "userDetailsByEmail", condition = "#result", key = "#passwordDto.email")
    @Override
    public boolean changePassword(long userId, CredentialsDto passwordDto) {
        log.info("Попытка сменить пароль для пользователя с id = {}", userId);
        UserDetailsImpl userDetailsFromDb = (UserDetailsImpl) loadUserByUsername(passwordDto.getEmail());

        if (passwordEncoder.matches(passwordDto.getOldPassword(), userDetailsFromDb.getPassword()) &&
            !passwordDto.getNewPassword().equals(passwordDto.getOldPassword())) {
            userDetailsFromDb.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));

            log.info("Пароль для пользователя {} изменен", userDetailsFromDb.getUsername());
            userDetailsRepository.save(userDetailsFromDb);

            return true;
        }

        log.info("Попытка сменить пароль отвергнута - передан неправильный старый пароль");
        return false;
    }
}
