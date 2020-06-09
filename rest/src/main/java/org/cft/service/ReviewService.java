package org.cft.service;

import org.cft.dto.ReviewDto;
import org.cft.entity.Review;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review addReview(ReviewDto review);

    Optional<Review> getReview(long reviewId);

    List<Review> getAllReviews(Pageable pageable);
}
