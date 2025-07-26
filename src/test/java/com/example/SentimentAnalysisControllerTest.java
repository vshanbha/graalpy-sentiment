package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class SentimentAnalysisControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${auth.username}")
    private String username;

    @Value("${auth.raw-password}")
    private String password;

    private TestRestTemplate authenticatedRestTemplate() {
        return restTemplate.withBasicAuth(username, password);
    }

    
    private String baseUrl() {
        return "http://localhost:" + port + "/analyze";
    }

    private String extractClassification(ResponseEntity<String> response) {

		System.out.println(">>> Status: " + response.getStatusCode());
		System.out.println(">>> Body: " + response.getBody());
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(response.getBody());
			return jsonNode.get("classification").asText();
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse response: " + response, e);
		}
	}
    
    @Test
    public void testGetSentiment_positive() {
    	ResponseEntity<String> response = this.authenticatedRestTemplate().getForEntity(baseUrl() + "?text=I love spring boot", String.class);
        assertEquals("positive", extractClassification(response));
    }

    @Test
    public void testPostSentiment_negative() {
    	ResponseEntity<String> response = this.authenticatedRestTemplate().postForEntity(baseUrl(), "I hate bugs", String.class);
        assertEquals("negative", extractClassification(response));
    }

    @Test
    public void testNeutralResponse() {
    	ResponseEntity<String> response = this.authenticatedRestTemplate().getForEntity(baseUrl() + "?text=live long and do something just enough to get by", String.class);
        assertEquals("neutral", extractClassification(response));
    }
}