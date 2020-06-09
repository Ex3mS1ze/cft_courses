package org.cft.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import org.cft.dto.CredentialsDto;
import org.cft.dto.UserDto;
import org.cft.entity.Review;
import org.cft.entity.User;
import org.cft.entity.View;
import org.cft.service.ExtendedUserDetailsService;
import org.cft.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private static final String CONTROLLER_URI = "http://localhost:8080/users/";

    private final UserService userService;
    private final ExtendedUserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<String> addUser(@Valid @RequestBody UserDto newUser) {
        Optional<User> savedUser = userService.addUser(newUser);

        if (savedUser.isEmpty()) {
            return ResponseEntity.badRequest().body("email already used");
        }

        return ResponseEntity.created(URI.create(CONTROLLER_URI + savedUser.get().getId())).build();
    }

    @GetMapping(value = "/{userId}")
    @JsonView(value = View.BasicView.class)
    public ResponseEntity<User> getUser(@PathVariable("userId") long userId) {
        Optional<User> userFromDb = userService.getUser(userId);
        return userFromDb.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    @JsonView(value = View.BasicView.class)
    public ResponseEntity<List<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PreAuthorize("hasRole((T(org.cft.entity.AppRoles).USER.getName())) && @userDetailsServiceImpl.isIdBelongPrincipal(#userId)")
    @PutMapping(value = "/{userId}")
    public ResponseEntity<Object> editUser(@PathVariable("userId") long userId, @Valid @RequestBody UserDto userDto) {
          if (userService.editUser(userId, userDto)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasRole((T(org.cft.entity.AppRoles).USER.getName())) && @userDetailsServiceImpl.isIdBelongPrincipal(#userId)")
    @PostMapping(value = "/{userId}/likedReviews/{reviewId}")
    public ResponseEntity<Object> addReviewToLiked(@PathVariable("userId") long userId, @PathVariable("reviewId") long reviewId) {
        if (userService.addReviewToLiked(userId, reviewId)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole((T(org.cft.entity.AppRoles).USER.getName())) && @userDetailsServiceImpl.isIdBelongPrincipal(#userId)")
    @DeleteMapping(value = "/{userId}/likedReviews/{reviewId}")
    public ResponseEntity<Object> removeReviewFromLiked(@PathVariable("userId") long userId, @PathVariable("reviewId") long reviewId) {
        if (userService.removeReviewFromLiked(userId, reviewId)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{userId}/likedReviews")
    @JsonView({View.BasicView.class})
    public ResponseEntity<Set<Review>> getLikedReviews(@PathVariable("userId") long userId) {
        Set<Review> likedReviews = userService.getLikedReviews(userId);

        if (likedReviews == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(likedReviews);

    }
    @PreAuthorize("hasRole((T(org.cft.entity.AppRoles).USER.getName())) && @userDetailsServiceImpl.isIdBelongPrincipal(#userId)")
    @PostMapping(value = "/{userId}/password")
    public ResponseEntity<String> changePassword(@PathVariable("userId") long userId, @RequestBody CredentialsDto dto) {
        if (!userService.isExistById(userId)) {
            return ResponseEntity.notFound().build();
        }

        if (userDetailsService.changePassword(userId, dto)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("wrong old password");
    }
}
