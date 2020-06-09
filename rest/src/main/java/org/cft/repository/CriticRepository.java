package org.cft.repository;

import org.cft.entity.Critic;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CriticRepository extends PagingAndSortingRepository<Critic, Long> {
    boolean removeById(long id);
}
