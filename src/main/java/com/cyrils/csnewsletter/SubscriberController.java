package com.cyrils.csnewsletter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    private final static Logger log = LoggerFactory.getLogger(SubscriberController.class);

    private final UserRepository repository;

    public SubscriberController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        log.debug("POST /api/subscribers - payload: {}", user);
        if (user.getUserId() == null || user.getUserId().isBlank()) {
            log.info("Create subscriber rejected: missing userId");
            return ResponseEntity.badRequest().build();
        }
        User saved = repository.save(user);
        log.info("Subscriber created: {}", saved.getUserId());
        return ResponseEntity.created(URI.create("/api/subscribers/" + saved.getUserId())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable("id") String id) {
        log.debug("GET /api/subscribers/{}", id);
        Optional<User> u = repository.findById(id);
        if (u.isPresent()) {
            log.info("Subscriber retrieved: {}", id);
        } else {
            log.info("Subscriber not found: {}", id);
        }
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
