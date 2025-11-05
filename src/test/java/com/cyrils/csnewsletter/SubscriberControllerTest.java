package com.cyrils.csnewsletter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestCamundaClientConfig.class)
@AutoConfigureMockMvc
public class SubscriberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository repository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createAndRetrieveSubscriber() throws Exception {
        User user = new User("cyril12", "cyril@example.com", "Technology");

        String json = objectMapper.writeValueAsString(user);

        // POST create
        mockMvc.perform(post("/api/subscribers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/subscribers/cyril12")));

        // assert repository contains the user
        Optional<User> found = repository.findById("cyril12");
        assertTrue(found.isPresent(), "User should be present in repository after POST");
        assertEquals("cyril@example.com", found.get().getEmail());

        // GET the user via controller
        mockMvc.perform(get("/api/subscribers/cyril12"))
                .andExpect(status().isOk());
    }
}
