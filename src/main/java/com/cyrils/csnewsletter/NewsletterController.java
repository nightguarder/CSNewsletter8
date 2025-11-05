package com.cyrils.csnewsletter;

import io.camunda.client.CamundaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/newsletter")
public class NewsletterController {
    private final static Logger log = LoggerFactory.getLogger(NewsletterController.class);

    // Using the new Client instead of ZeebeClient
    @Autowired
    private CamundaClient camundaClient;

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeToNewsletter(@RequestBody SubscriptionRequest request) {
        log.info("Received subscription request for user '{}' on topic '{}'", request.getUserID(), request.getTopic());

        // Create a new map to hold the requested variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("userID", request.getUserID());
        variables.put("email", request.getEmail());
        variables.put("topic", request.getTopic());

        // Start the process instance with the given variables
        camundaClient.newCreateInstanceCommand()
                .bpmnProcessId("newsletter_subscription")
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        // For now just return OK
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscribe/new-user")
    public ResponseEntity<String> subscribeNewUser(@RequestBody SubscriptionRequest request) {
        log.info("Starting new-user-subscription-process for email='{}' topic='{}'", request.getEmail(),
                request.getTopic());

        Map<String, Object> variables = new HashMap<>();
        variables.put("email", request.getEmail());
        variables.put("topic", request.getTopic());
        // optionally accept userID if provided
        if (request.getUserID() != null && !request.getUserID().isBlank()) {
            variables.put("userID", request.getUserID());
        }

        camundaClient.newCreateInstanceCommand()
                .bpmnProcessId("new-user-subscription-process")
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        return ResponseEntity.ok().build();
    }

}
