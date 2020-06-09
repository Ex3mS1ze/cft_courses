package org.cft.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.cft.dto.CriticDto;
import org.cft.entity.Critic;
import org.cft.entity.Review;
import org.cft.entity.View;
import org.cft.service.CriticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/critics")
@Controller
public class CriticController {
    private static final String CONTROLLER_URI = "http://localhost:8080/critics/";

    private final CriticService criticService;

    @Autowired
    public CriticController(CriticService criticService) {
        this.criticService = criticService;
    }

    @PreAuthorize("hasRole((T(org.cft.entity.AppRoles).ADMIN.getName()))")
    @PostMapping
    public ResponseEntity<String> addCritic(@Valid @RequestBody CriticDto criticDto) {
        Optional<Critic> savedCritic = criticService.addCritic(criticDto);

        if (savedCritic.isPresent()) {
            return ResponseEntity.created(URI.create(CONTROLLER_URI + savedCritic.get().getId())).build();
        }

        return ResponseEntity.badRequest().body("not found user with passed id = " + criticDto.getUserId());
    }

    @GetMapping(value = "/{criticId}")
    @JsonView(value = View.BasicView.class)
    public ResponseEntity<Critic> getCritic(@PathVariable("criticId") long criticId) {
        Optional<Critic> criticFromDb = criticService.getCritic(criticId);

        return criticFromDb.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @JsonView(value = View.BasicView.class)
    public ResponseEntity<List<Critic>> getAllCritics(Pageable pageable) {
        return ResponseEntity.ok(criticService.getAllCritics(pageable));
    }

    @GetMapping(value = "/{criticId}/writtenReviews")
    @JsonView({View.BasicView.class})
    public ResponseEntity<List<Review>> getWrittenReviews(@PathVariable("criticId") long criticId) {
        List<Review> writtenReviews = criticService.getWrittenReviews(criticId);

        if (writtenReviews == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(writtenReviews);
    }
}