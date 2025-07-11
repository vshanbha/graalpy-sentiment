package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SentimentAnalysisControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/analyze";
    }

    private String extractClassification(String response) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(response);
			return jsonNode.get("classification").asText();
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse response: " + response, e);
		}
	}
    
    @Test
    public void testGetSentiment_positive() {
        String response = this.restTemplate.getForObject(baseUrl() + "?text=I love spring boot", String.class);
        assertEquals("positive", extractClassification(response));
    }

    @Test
    public void testPostSentiment_negative() {
        String response = this.restTemplate.postForObject(baseUrl(), "I hate bugs", String.class);
        assertEquals("negative", extractClassification(response));
    }

    @Test
    public void testNeutralResponse() {
        String response = this.restTemplate.getForObject(baseUrl() + "?text=live long and do something just enough to get by", String.class);
        assertEquals("neutral", extractClassification(response));
    }
}