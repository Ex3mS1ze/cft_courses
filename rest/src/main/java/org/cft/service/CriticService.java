package org.cft.service;

import org.cft.dto.CriticDto;
import org.cft.entity.Critic;
import org.cft.entity.Review;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CriticService {
    Optional<Critic> addCritic(CriticDto critic);

    Optional<Critic> getCritic(long criticId);

    List<Critic> getAllCritics(Pageable pageable);

    List<Review> getWrittenReviews(long criticId);
}
