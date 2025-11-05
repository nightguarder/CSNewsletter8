package com.cyrils.csnewsletter;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Using the io.camunda.client APIs
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.api.response.ActivatedJob;

@Component
public class NewsletterWorker {
    private static final Logger log = LoggerFactory.getLogger(NewsletterWorker.class);
    private final ObjectMapper objectMapper = new ObjectMapper(); // for finding Articles

    // Log subscription - just logs incoming variables and completes
    @JobWorker(type = "log-subscription")
    public Map<String, Object> handleLogSubscription(final ActivatedJob job,
            @Variable(name = "userID") String userID,
            @Variable(name = "topic") String topic) {
        log.info("[log-subscription] jobKey={} variables: {}", job.getKey(), job.getVariables());
        // no new variables to set
        return Collections.emptyMap();
    }

    // Find relevant articles - simulates a lookup and publishes an "articles"
    // variable (JSON string)
    @JobWorker(type = "find-articles")
    public Map<String, Object> handleFindArticles(final ActivatedJob job,
            @Variable(name = "topic") String topic) throws JsonProcessingException {
        log.info("Finding articles for topic: {} (job={})", topic, job.getKey());

        List<String> articles = findMockArticles(topic);
        String articlesJson = objectMapper.writeValueAsString(articles);

        return Collections.singletonMap("articles", articlesJson);
    }

    private List<String> findMockArticles(String topic) {
        if (topic == null)
            return Collections.singletonList("Article: No topic provided.");
        if ("Technology".equalsIgnoreCase(topic)) {
            return Arrays.asList("Article: Getting Started with Camunda 8", "Article: The Future of AI",
                    "Article: Quantum Computing Explained");
        } else if ("Medicine".equalsIgnoreCase(topic)) {
            return Arrays.asList("Article: Advances in mRNA Vaccines", "Article: The Importance of Sleep for Health");
        }
        return Collections.singletonList("Article: No new articles on your topic this week.");
    }

    // Format email - reads 'articles' (JSON string) and produces 'emailBody'
    @JobWorker(type = "format-email")
    public Map<String, Object> handleFormatEmail(final ActivatedJob job,
            @Variable(name = "articles") String articlesJson) throws JsonProcessingException {
        StringBuilder body = new StringBuilder();
        body.append("Dear user,\n\nWe have found these articles:\n");

        // try to parse JSON array, otherwise treat as plain string
        try {
            List<String> articles = objectMapper.readValue(articlesJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            for (String a : articles)
                body.append("- ").append(a).append("\n");
        } catch (Exception e) {
            body.append(String.valueOf(articlesJson)).append("\n");
        }

        body.append("\nBest regards,\nCS Newsletter");

        return Collections.singletonMap("emailBody", body.toString());
    }

    // Send email - reads 'emailBody' and simulates sending
    @JobWorker(type = "send-email")
    public Map<String, Object> handleSendEmail(final ActivatedJob job,
            @Variable(name = "email") String userEmail,
            @Variable(name = "emailBody") String emailBody) {
        // Simulate sending
        log.info("<<<<< SIMULATING SENDING EMAIL >>>>>");
        log.info("TO: {}", userEmail != null ? userEmail : "(no email)");
        log.info("BODY:\n{}", emailBody != null ? emailBody : "(no body)");
        log.info("<<<<< =========================== >>>>>");

        return Collections.emptyMap();
    }

}
