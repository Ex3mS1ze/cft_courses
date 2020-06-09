package org.cft.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cft.dto.ReviewDto;
import org.cft.entity.Review;
import org.cft.mapper.ReviewMapper;
import org.cft.repository.ReviewRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Transactional
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    private final ReviewMapper reviewMapper;

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "allReviews", allEntries = true),
                    @CacheEvict(value = "writtenReviewsByCriticId", key = "#result.author.id")
            },
            put = @CachePut(value = "reviewsById", key = "#result.id")
    )
    @CacheEvict(value = {"allReviews", "reviewsById"}, allEntries = true)
    public Review addReview(ReviewDto reviewDto) {
        log.info("Попытка добавить рецензию");

        Review review = reviewMapper.toEntity(reviewDto);
        review.setDate(LocalDate.now());

        Review savedReview = reviewRepository.save(review);
        log.info("Новая рецензия добавлена id = {}", savedReview.getId());

        return savedReview;
    }

    @Override
    @Cacheable(value = "reviewsById")
    public Optional<Review> getReview(long reviewId) {
        log.info("Поиск рецензии с id = {}", reviewId);
        return reviewRepository.findById(reviewId);
    }

    @Override
    @Cacheable(value = "allReviews", condition = "#pageable.pageSize eq 20")
    public List<Review> getAllReviews(Pageable pageable) {
        Iterable<Review> iterableReviews;

        if (pageable.isUnpaged()) {
            log.info("Нет информации для пагинация, возвращаются все обзоры");
            iterableReviews = reviewRepository.findAll();
        } else {
            log.info("Есть информация для пагинация, возвращается часть обзоров");
            iterableReviews = reviewRepository.findAll(pageable);
        }

        List<Review> reviews = new ArrayList<>();
        iterableReviews.forEach(reviews::add);

        return reviews;
    }
}
