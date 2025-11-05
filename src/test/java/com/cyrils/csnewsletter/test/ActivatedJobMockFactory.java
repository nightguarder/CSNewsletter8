package com.cyrils.csnewsletter.test;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.camunda.client.api.response.ActivatedJob;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Small factory to create mocked ActivatedJob instances for unit tests.
 *
 * Note: the Camunda Java client returns serialized variables (String) from
 * ActivatedJob#getVariables in the current client version used by this project,
 * so this factory serializes the provided variables map into JSON. If null is
 * passed, getVariables() will return null.
 */
public class ActivatedJobMockFactory {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ActivatedJob mockActivatedJob(long key, Map<String, Object> variables) {
        ActivatedJob job = mock(ActivatedJob.class);
        when(job.getKey()).thenReturn(key);

        if (variables == null) {
            when(job.getVariables()).thenReturn((String) null);
        } else {
            try {
                String json = MAPPER.writeValueAsString(variables);
                when(job.getVariables()).thenReturn(json);
            } catch (JsonProcessingException e) {
                // fallback to toString if serialization fails
                when(job.getVariables()).thenReturn(variables.toString());
            }
        }

        return job;
    }

}
