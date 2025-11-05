package com.cyrils.csnewsletter;

import com.cyrils.csnewsletter.test.ActivatedJobMockFactory;
import io.camunda.client.api.response.ActivatedJob;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LogDuplicateWorkerTest {

    private final LogDuplicateWorker worker = new LogDuplicateWorker();

    @Test
    void testHandleLogDuplicate_completesSuccessfully() {
        ActivatedJob job = ActivatedJobMockFactory.mockActivatedJob(500L,
                Map.of("userID", "u-dup", "email", "dup@example.com", "topic", "Investment"));
        Map<String, Object> out = worker.handleLogDuplicate(job, "u-dup", "dup@example.com", "Investment");
        assertNotNull(out);
        assertTrue(out.isEmpty());
    }

}
