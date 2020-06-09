package org.cft.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.cft.dto.ReviewDto;
import org.cft.entity.Review;
import org.cft.entity.View;
import org.cft.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequestMapping(value = "/reviews")
@Controller
public class ReviewController {
    private static final String CONTROLLER_URI = "http://localhost:8080/reviews/";

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PreAuthorize("hasRole((T(org.cft.entity.AppRoles).CRITIC.getName()))")
    @PostMapping
    public ResponseEntity<String> addReview(@Valid @RequestBody ReviewDto reviewDto) {
        Review savedReview = reviewService.addReview(reviewDto);
        return ResponseEntity.created(URI.create(CONTROLLER_URI + savedReview.getId())).build();
    }

    @GetMapping(value = "/{reviewId}")
    @JsonView({View.BasicView.class})
    public ResponseEntity<Review> getReview(@PathVariable("reviewId") long reviewId) {
        Optional<Review> reviewFromDb = reviewService.getReview(reviewId);
        return reviewFromDb.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @JsonView({View.BasicView.class})
    public ResponseEntity<List<Review>> getAllReviews(Pageable pageable) {
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS).mustRevalidate()).body(reviewService.getAllReviews(pageable));
    }
}
