package com.cyrils.csnewsletter;

import com.cyrils.csnewsletter.test.ActivatedJobMockFactory;
import io.camunda.client.api.response.ActivatedJob;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SendConfirmationWorkerTest {

    private final SendConfirmationWorker worker = new SendConfirmationWorker();

    @Test
    void testHandleSendConfirmation_returnsEmptyMap() {
        ActivatedJob job = ActivatedJobMockFactory.mockActivatedJob(999L,
                Map.of("email", "x@y.com", "userID", "u1", "topic", "Technology"));
        Map<String, Object> out = worker.handleSendConfirmation(job, "u1", "x@y.com", "Technology");
        assertNotNull(out);
        assertTrue(out.isEmpty());
    }
}
