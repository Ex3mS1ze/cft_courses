package org.cft.repository;

import org.cft.entity.UserDetailsImpl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository<UserDetailsImpl, Long> {
}
