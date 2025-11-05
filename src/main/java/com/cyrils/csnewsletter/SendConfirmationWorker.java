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
public class SendConfirmationWorker {
    private static final Logger log = LoggerFactory.getLogger(SendConfirmationWorker.class);

    // This worker completes the 'send-confirmation-email' service task. It composes
    // a short
    // confirmation email and logs it; in a real app you'd integrate with an SMTP
    // service or
    // an external email provider.
    @JobWorker(type = "send-confirmation-email")
    public Map<String, Object> handleSendConfirmation(final ActivatedJob job,
            @Variable(name = "userID") String userID,
            @Variable(name = "email") String email,
            @Variable(name = "topic") String topic) {
        log.info("[send-confirmation-email] jobKey={} variables: {}", job.getKey(), job.getVariables());

        String to = email != null ? email : "(no-email)";
        StringBuilder body = new StringBuilder();
        body.append("Hi\n\n");
        body.append("Thanks for subscribing to the CSNewsletter");
        if (topic != null && !topic.isBlank()) {
            body.append(" for topic: ").append(topic).append(".");
        }
        body.append("\n\n");
        body.append("Your subscription id: ").append(userID != null ? userID : "(not provided)").append("\n\n");
        body.append("— CS Newsletter Team");

        log.info("<<<<< SIMULATING SENDING CONFIRMATION >>>>>");
        log.info("TO: {}", to);
        log.info("BODY:\n{}", body.toString());
        log.info("<<<<< =========================== >>>>>");

        // No new variables to publish; completing the job finishes the service task.
        return Collections.emptyMap();
    }

}
