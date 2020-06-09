package org.cft.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cft.dto.CriticDto;
import org.cft.entity.Critic;
import org.cft.entity.Review;
import org.cft.entity.User;
import org.cft.mapper.CriticMapper;
import org.cft.repository.CriticRepository;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Transactional
@Service
public class CriticServiceImpl implements CriticService {
    private final CriticRepository criticRepository;

    private final UserService userService;
    private final RoleService roleService;

    private final CriticMapper criticMapper;

    @Override
    @SuppressWarnings("SpringElInspection")
    @CacheEvict(value = "allCritics", allEntries = true)
    @CachePut(value = "criticsById", condition = "#result ne null", key = "#result.id")
    public Optional<Critic> addCritic(CriticDto criticDto) {
        log.info("Попытка добавить нового критика");

        Critic critic = criticMapper.toEntity(criticDto);

        Optional<User> user = userService.getUser(critic.getUser().getId());

        if (user.isEmpty()) {
            log.info("Пользователя с id = '{}' не существует", critic.getUser().getId());
            return Optional.empty();
        }

        Hibernate.initialize(user);
        user.get().getUserDetails().addRole(roleService.getCriticRole());
        critic.setUser(user.get());
        Critic savedCritic = criticRepository.save(critic);
        log.info("Добавлен новый критик с id = '{}'", savedCritic.getId());

        return Optional.of(savedCritic);
    }

    @Override
    @Cacheable(value = "criticsById")
    public Optional<Critic> getCritic(long criticId) {
        log.info("Поиск критика с id = {}", criticId);
        return criticRepository.findById(criticId);
    }

    @Override
    @Cacheable(value = "allCritics")
    public List<Critic> getAllCritics(Pageable pageable) {
        Iterable<Critic> iterableCritics;

        if (pageable.isUnpaged()) {
            log.info("Нет информации для пагинация, возвращаются все пользователи");
            iterableCritics = criticRepository.findAll();
        } else {
            log.info("Есть информация для пагинация, возвращается часть пользователей");
            iterableCritics = criticRepository.findAll(pageable);
        }

        List<Critic> critics = new ArrayList<>();
        iterableCritics.forEach(critics::add);

        return critics;
    }

    @Override
    @Cacheable("writtenReviewsByCriticId")
    public List<Review> getWrittenReviews(long criticId) {
        log.info("Попыка получить рецензии критика с id = {}", criticId);
        Optional<Critic> criticFromDb = criticRepository.findById(criticId);

        if (criticFromDb.isEmpty()) {
            log.info("Критик с id = '{}' не найден", criticId);
            return null;
        }

        Hibernate.initialize(criticFromDb.get().getWrittenReviews());
        log.info("Получены все рецензии критика с id = {}", criticId);

        return criticFromDb.get().getWrittenReviews();
    }
}
