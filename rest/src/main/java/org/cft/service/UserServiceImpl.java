package org.cft.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cft.dto.UserDto;
import org.cft.entity.Review;
import org.cft.entity.User;
import org.cft.entity.UserDetailsImpl;
import org.cft.mapper.UserMapper;
import org.cft.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Transactional
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final ReviewService reviewService;
    private final RoleService roleService;
    private final ExtendedUserDetailsService userDetailsService;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    @SuppressWarnings("SpringElInspection")
    @CachePut(value = "usersById", condition = "#result ne null", key = "#result.id")
    @Caching(evict = {
            @CacheEvict(value = "allUsers", allEntries = true),
            @CacheEvict(value = "usersById")

    })
    public Optional<User> addUser(UserDto userDto) {
        log.info("Попытка добавить нового пользователя");
        Optional<User> userFromDb = userRepository.findByEmail(userDto.getEmail());

        if (userFromDb.isPresent()) {
            log.info("Пользователь с email = '{}' уже существует", userDto.getEmail());
            return Optional.empty();
        }

        User savedUser = userRepository.save(prepareNewUser(userDto));

        userDetailsService.addUserDetails(prepareUserDetails(userDto, savedUser));

        log.info("Добавлен новый пользователь с email = '{}'", savedUser.getEmail());

        return Optional.of(savedUser);
    }

    private User prepareNewUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setRegistrationDate(LocalDateTime.now());
        return user;
    }

    private UserDetailsImpl prepareUserDetails(UserDto userDto, User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl();

        userDetails.setUser(user);
        userDetails.setEnabled(true);
        userDetails.setLocked(false);
        userDetails.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDetails.setRoles(Set.of(roleService.getUserRole()));

        return userDetails;
    }

    @Override
    @Cacheable("usersById")
    public Optional<User> getUser(long id) {
        log.info("Поиск пользователя с id = {}", id);
        return userRepository.findById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "usersById", condition = "#result", key = "#userId"),
            @CacheEvict(value = "allUsers", allEntries = true)
    })
    public boolean editUser(long userId, UserDto userDto) {
        log.info("Попытка изменить данные пользователя с id = {}", userId);
        Optional<User> userById = getUser(userId);

        if (userById.isEmpty()) {
            log.info("Пользователя с id = '{}' не существует", userId);
            return false;
        }

        userById.get().setFirstName(userDto.getFirstName());
        userById.get().setSecondName(userDto.getSecondName());
        if (!isEmailUsed(userDto.getEmail())) {
            userById.get().setEmail(userDto.getEmail());
        }

        userRepository.save(userById.get());
        log.info("Изменения для пользователя с id = '{}' сохранены", userId);

        return true;
    }

    @Override
    @Cacheable(value = "allUsers", condition = "#pageable.pageSize eq 20")
    public List<User> getAllUsers(Pageable pageable) {
        Iterable<User> iterableUsers;

        if (pageable.isUnpaged()) {
            log.info("Нет информации для пагинация, возвращаются все пользователи");
            iterableUsers = userRepository.findAll();
        } else {
            log.info("Есть информация для пагинация, возвращается часть пользователей");
            iterableUsers = userRepository.findAll(pageable);
        }

        List<User> users = new ArrayList<>();
        iterableUsers.forEach(users::add);

        return users;
    }

    @Override
    @CacheEvict(value = "likedReviews", condition = "#result", key = "#userId")
    public boolean addReviewToLiked(long userId, long reviewId) {
        log.info("Попытка добавить рецензию id = {} пользователю id = {} в список понравившихся", reviewId, userId);
        Optional<User> userFromDb = getUser(userId);
        Optional<Review> reviewFromDb = reviewService.getReview(reviewId);

        if (reviewFromDb.isEmpty() || userFromDb.isEmpty()) {
            log.info("Не найден пользователь id = '{}' или обзор id = '{}'", userId, reviewId);
            return false;
        }

        if (!userFromDb.get().getLikedReviews().contains(reviewFromDb.get())) {
            userFromDb.get().getLikedReviews().add(reviewFromDb.get());
            userRepository.save(userFromDb.get());
            log.info("Обзор id = '{}' добавлен в список понравившихся пользователя id = '{}' ", reviewId, userId);
        }

        return true;
    }

    @Override
    @CacheEvict(value = "likedReviews", condition = "#result", key = "#userId")
    public boolean removeReviewFromLiked(long userId, long reviewId) {
        log.info("Попытка удалть рецензию id = {} пользователю id = {} из списка понравившихся", reviewId, userId);
        Optional<User> userFromDb = getUser(userId);
        Optional<Review> reviewFromDb = reviewService.getReview(reviewId);

        if (reviewFromDb.isEmpty() || userFromDb.isEmpty()) {
            log.info("Не найден пользователь id = '{}' или обзор id = '{}'", userId, reviewId);
            return false;
        }

        if (userFromDb.get().getLikedReviews().contains(reviewFromDb.get())) {
            userFromDb.get().getLikedReviews().remove(reviewFromDb.get());
            log.info("Обзор id = '{}' удален из списка понравившихся пользователя id = '{}' ", reviewId, userId);
        }

        return true;
    }

    @Override
    @Cacheable("likedReviews")
    public Set<Review> getLikedReviews(long userId) {
        log.info("Попытка получить список лайкнутых рецензий пользователя id = {}",  userId);
        Optional<User> userFromDb = getUser(userId);

        if (userFromDb.isEmpty()) {
            log.info("Пользователь с id = '{}' не найден", userId);
            return null;
        }

        Hibernate.initialize(userFromDb.get().getLikedReviews());
        log.info("Получены лайкнутые рецензии пользователя с id = {}", userId);

        return userFromDb.get().getLikedReviews();
    }

    @Override
    public boolean isEmailUsed(String email) {
        log.info("Проверка существования пользователя с email = {}", email);
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isExistById(long userId) {
        log.info("Проверка существования пользователя с id = {}", userId);
        return userRepository.existsById(userId);
    }
}
