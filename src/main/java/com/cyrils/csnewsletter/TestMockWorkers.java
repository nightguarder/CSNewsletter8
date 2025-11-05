package com.cyrils.csnewsletter;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.exception.BpmnError;

@Component
public class TestMockWorkers {
    private static final Logger log = LoggerFactory.getLogger(TestMockWorkers.class);

    private final UserRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TestMockWorkers(UserRepository repository) {
        this.repository = repository;
    }

    // Retrieve user profile - mock implementation backed by in-memory repository
    @JobWorker(type = "get-user-profile")
    public Map<String, Object> handleGetUserProfile(final ActivatedJob job,
            @Variable(name = "userID") String userID) {
        log.info("[get-user-profile] jobKey={} variables: {}", job.getKey(), job.getVariables());

        // if userID not provided via annotated variable, try to parse variables JSON
        if (userID == null || userID.isBlank()) {
            String varsJson = job.getVariables();
            if (varsJson != null && !varsJson.isBlank()) {
                try {
                    var map = objectMapper.readValue(varsJson, new TypeReference<Map<String, Object>>() {
                    });
                    // Accept both "userID" and "userId" @JsonAlias
                    Object u = map.get("userID");
                    if (u == null) {
                        u = map.get("userId");
                    }
                    if (u != null)
                        userID = String.valueOf(u);
                } catch (Exception e) {
                    log.debug("Could not parse job variables json", e);
                }
            }
        }

        if (userID == null || userID.isBlank()) {
            log.error("User profile not found for userID: {}", userID);
            throw new BpmnError("USER_NOT_FOUND", "User profile not found");
        }

        Optional<User> found = repository.findById(userID);
        if (found.isEmpty()) {
            log.error("User profile not found in repository for userID: {}", userID);
            throw new BpmnError("USER_NOT_FOUND", "User profile not found");
        }

        User u = found.get();
        return Map.of("userID", u.getUserId(), "email", u.getEmail(), "topic", u.getTopic());
    }
}
