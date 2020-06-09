package org.cft.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cft.entity.AppRoles;
import org.cft.entity.RoleImpl;
import org.cft.repository.RoleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Cacheable(value = "roles", condition = "#result ne null", key = "#result.id")
    public RoleImpl getUserRole() {
        log.info("Получение роли 'USER'");
        return roleRepository.getByName(AppRoles.USER.getName());
    }

    @Cacheable(value ="roles", condition = "#result ne null", key = "#result.id")
    public RoleImpl getCriticRole() {
        log.info("Получение роли 'CRITIC'");
        return roleRepository.getByName(AppRoles.CRITIC.getName());
    }
}
