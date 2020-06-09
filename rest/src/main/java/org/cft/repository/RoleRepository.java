package org.cft.repository;

import org.cft.entity.RoleImpl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleImpl, Long> {
    RoleImpl getByName(String name);
}
