package com.cyrils.csnewsletter;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.api.response.ActivatedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreateUserWorker {
    private static final Logger log = LoggerFactory.getLogger(CreateUserWorker.class);

    private final UserRepository repository;

    public CreateUserWorker(UserRepository repository) {
        this.repository = repository;
    }

    @JobWorker(type = "create-user")
    public Map<String, Object> handleCreateUser(final ActivatedJob job,
            @Variable(name = "userID") String userID,
            @Variable(name = "email") String email,
            @Variable(name = "topic") String topic) {
        log.info("[create-user] jobKey={} variables: {}", job.getKey(), job.getVariables());

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required to create a user");
        }

        // If a user already exists with this email, return the existing id and mark
        // userExists=true
        Optional<User> existing = repository.findByEmail(email);
        if (existing.isPresent()) {
            log.info("User already exists for email={}, id={}", email, existing.get().getUserId());
            Map<String, Object> out = new HashMap<>();
            out.put("userID", existing.get().getUserId());
            out.put("email", existing.get().getEmail());
            out.put("topic", existing.get().getTopic());
            out.put("userExists", true);
            return out;
        }

        // Generate an ID if caller didn't provide one
        if (userID == null || userID.isBlank()) {
            userID = UUID.randomUUID().toString();
        }

        User u = new User(userID, email, topic);
        repository.save(u);
        log.info("Created user id={} email={}", u.getUserId(), u.getEmail());

        Map<String, Object> out = new HashMap<>();
        out.put("userID", u.getUserId());
        out.put("email", u.getEmail());
        out.put("topic", u.getTopic());
        out.put("userExists", false);

        return out;
    }

}
