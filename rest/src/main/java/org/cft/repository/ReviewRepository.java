package org.cft.repository;

import org.cft.entity.Review;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReviewRepository  extends PagingAndSortingRepository<Review, Long> {
}
