package com.cyrils.csnewsletter;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.api.response.ActivatedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class LogDuplicateWorker {
    private static final Logger log = LoggerFactory.getLogger(LogDuplicateWorker.class);

    @JobWorker(type = "log-duplicate-attempt")
    public Map<String, Object> handleLogDuplicate(final ActivatedJob job,
            @Variable(name = "userID") String userID,
            @Variable(name = "email") String email,
            @Variable(name = "topic") String topic) {
        log.info("[log-duplicate-attempt] jobKey={} variables: {}", job.getKey(), job.getVariables());

        String idPart = userID != null ? userID : "(no-userId)";
        String emailPart = email != null ? email : "(no-email)";
        String topicPart = topic != null ? topic : "(no-topic)";

        // Log a concise duplicate message for auditing
        log.warn("Duplicate subscription attempt detected for userId={} email={} topic={}", idPart, emailPart,
                topicPart);

        // In future we could publish an incident or metrics; for now simply complete
        // the job.
        return Collections.emptyMap();
    }
}
