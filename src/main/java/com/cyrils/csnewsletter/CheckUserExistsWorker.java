package com.cyrils.csnewsletter;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.api.response.ActivatedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CheckUserExistsWorker {
    private static final Logger log = LoggerFactory.getLogger(CheckUserExistsWorker.class);

    private final UserRepository repository;

    public CheckUserExistsWorker(UserRepository repository) {
        this.repository = repository;
    }

    @JobWorker(type = "check-user-exists")
    public Map<String, Object> handleCheckUserExists(final ActivatedJob job,
            @Variable(name = "userID") String userID,
            @Variable(name = "email") String email) {
        log.info("[check-user-exists] jobKey={} variables: {}", job.getKey(), job.getVariables());

        boolean exists = false;
        String foundId = null;

        // Prefer checking by email when available (the form will submit email)
        if (email != null && !email.isBlank()) {
            Optional<User> byEmail = repository.findByEmail(email);
            if (byEmail.isPresent()) {
                exists = true;
                foundId = byEmail.get().getUserId();
            }
        } else if (userID != null && !userID.isBlank()) {
            Optional<User> byId = repository.findById(userID);
            if (byId.isPresent()) {
                exists = true;
                foundId = byId.get().getUserId();
            }
        }

        Map<String, Object> out = new HashMap<>();
        out.put("userExists", exists);
        if (foundId != null)
            out.put("userID", foundId);
        return out;
    }

}
