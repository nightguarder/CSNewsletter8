package com.cyrils.csnewsletter;

import com.cyrils.csnewsletter.test.ActivatedJobMockFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.client.api.response.ActivatedJob;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NewsletterWorkerTest {

    private final NewsletterWorker worker = new NewsletterWorker();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testHandleFindArticles_withTopic() throws Exception {
        ActivatedJob job = ActivatedJobMockFactory.mockActivatedJob(123L, Map.of("topic", "Technology"));
        Map<String, Object> vars = worker.handleFindArticles(job, "Technology");
        assertTrue(vars.containsKey("articles"));
        String articlesJson = (String) vars.get("articles");
        List<String> articles = objectMapper.readValue(articlesJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        assertEquals(3, articles.size());
    }

    @Test
    void testHandleFindArticles_noTopic() throws Exception {
        ActivatedJob job = ActivatedJobMockFactory.mockActivatedJob(124L, Map.of());
        Map<String, Object> vars = worker.handleFindArticles(job, null);
        assertTrue(vars.containsKey("articles"));
        String articlesJson = (String) vars.get("articles");
        List<String> articles = objectMapper.readValue(articlesJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        assertEquals(1, articles.size());
        assertTrue(articles.get(0).toLowerCase().contains("no topic"));
    }

    @Test
    void testHandleFormatEmail_validJson() throws Exception {
        List<String> art = Arrays.asList("Article A", "Article B");
        String json = objectMapper.writeValueAsString(art);
        ActivatedJob job = ActivatedJobMockFactory.mockActivatedJob(125L, Map.of("articles", json));
        Map<String, Object> vars = worker.handleFormatEmail(job, json);
        assertTrue(vars.containsKey("emailBody"));
        String emailBody = (String) vars.get("emailBody");
        assertTrue(emailBody.contains("- Article A"));
        assertTrue(emailBody.contains("- Article B"));
    }

    @Test
    void testHandleFormatEmail_invalidJson() throws Exception {
        String raw = "Just a plain string";
        ActivatedJob job = ActivatedJobMockFactory.mockActivatedJob(126L, Map.of("articles", raw));
        Map<String, Object> vars = worker.handleFormatEmail(job, raw);
        assertTrue(vars.containsKey("emailBody"));
        String body = (String) vars.get("emailBody");
        assertTrue(body.contains(raw));
    }

    @Test
    void testHandleSendEmail_returnsEmptyMap() {
        ActivatedJob job = ActivatedJobMockFactory.mockActivatedJob(127L,
                Map.of("email", "test@example.com", "emailBody", "hi"));
        Map<String, Object> vars = worker.handleSendEmail(job, "test@example.com", "hi");
        assertNotNull(vars);
        assertTrue(vars.isEmpty());
    }

    @Test
    void testHandleLogSubscription_returnsEmptyMap() {
        ActivatedJob job = ActivatedJobMockFactory.mockActivatedJob(128L, Map.of("userID", "u1", "topic", "Tech"));
        Map<String, Object> vars = worker.handleLogSubscription(job, "u1", "Tech");
        assertNotNull(vars);
        assertTrue(vars.isEmpty());
    }

}
