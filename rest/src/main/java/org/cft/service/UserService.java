package org.cft.service;

import org.cft.dto.UserDto;
import org.cft.entity.Review;
import org.cft.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    Optional<User> addUser(UserDto user);

    Optional<User> getUser(long id);

    boolean editUser(long userId, UserDto user);

    List<User> getAllUsers(Pageable pageable);

    boolean addReviewToLiked(long userId, long reviewId);

    boolean removeReviewFromLiked(long userId, long reviewId);

    Set<Review> getLikedReviews(long userId);

    boolean isEmailUsed(String email);

    boolean isExistById(long userId);
}
