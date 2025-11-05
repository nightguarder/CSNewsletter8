package com.cyrils.csnewsletter;

import com.cyrils.csnewsletter.test.ActivatedJobMockFactory;
import io.camunda.client.api.response.ActivatedJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@org.springframework.context.annotation.Import(TestCamundaClientConfig.class)
class CreateUserWorkerTest {

    @Autowired
    CreateUserWorker worker;

    @Autowired
    UserRepository repository;

    @BeforeEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void testCreateUser_createsNewUser() {
        ActivatedJob job = ActivatedJobMockFactory.mockActivatedJob(100L,
                Map.of("email", "new@example.com", "topic", "Technology"));

        Map<String, Object> out = worker.handleCreateUser(job, null, "new@example.com", "Technology");

        assertNotNull(out);
        assertTrue(out.containsKey("userID"));
        assertEquals(false, out.get("userExists"));

        String id = (String) out.get("userID");
        assertTrue(repository.findById(id).isPresent());
        User saved = repository.findById(id).get();
        assertEquals("new@example.com", saved.getEmail());
        assertEquals("Technology", saved.getTopic());
    }

    @Test
    void testCreateUser_existingEmail_returnsExisting() {
        // pre-insert a user
        User u = new User("u-1", "same@example.com", "Medicine");
        repository.save(u);

        ActivatedJob job = ActivatedJobMockFactory.mockActivatedJob(101L,
                Map.of("email", "same@example.com", "topic", "Medicine"));
        Map<String, Object> out = worker.handleCreateUser(job, null, "same@example.com", "Medicine");

        assertNotNull(out);
        assertEquals(true, out.get("userExists"));
        assertEquals("u-1", out.get("userID"));
    }

}
