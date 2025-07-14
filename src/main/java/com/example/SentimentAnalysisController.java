package com.example;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.python.embedding.GraalPyResources;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@SpringBootApplication
@RestController
@RequestMapping("/analyze")
public class SentimentAnalysisController {
	static final String PYTHON = "python";

	private Context polyglotContext;

	private Value analyzeSentimentFunction;

	@org.springframework.beans.factory.annotation.Value("${python.script.path:sentiment_analysis.py}")
	private String pythonScriptPath;

	@org.springframework.beans.factory.annotation.Value("${external.python.script:}")
	private String externalPythonScript;

	private final ResourceLoader resourceLoader;

	public SentimentAnalysisController(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public static void main(String[] args) {
		SpringApplication.run(SentimentAnalysisController.class, args);
	}

	@PostConstruct
	public void initialize() {
		try {
			polyglotContext = GraalPyResources.createContext();
			String scriptPath = (externalPythonScript != null && !externalPythonScript.isEmpty()) ? externalPythonScript
					: pythonScriptPath;

			Source source;
			if (externalPythonScript != null && !externalPythonScript.isEmpty()) {
				// Load from external file
				File pythonFile = new File(scriptPath);
				source = Source.newBuilder(PYTHON, pythonFile).build();
			} else {
				// Load from classpath
				Resource resource = resourceLoader.getResource("classpath:" + scriptPath);
				try (Reader reader = new InputStreamReader(resource.getInputStream())) {
					source = Source.newBuilder(PYTHON, reader, scriptPath).build();
				}
			}

			polyglotContext.eval(source);
			analyzeSentimentFunction = polyglotContext.getBindings(PYTHON).getMember("analyze_sentiment");
			if (analyzeSentimentFunction == null) {
				System.err.println("Function 'analyze_sentiment' not found in Python script.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping
	public ResponseEntity<String> analyzeSentiment(@RequestParam String text) {
		return analyzeSentimentInternal(text);
	}

	@PostMapping
	public ResponseEntity<String> analyzeSentimentPost(@RequestBody String text) {
		return analyzeSentimentInternal(text);
	}

	private ResponseEntity<String> analyzeSentimentInternal(String text) {
		if (analyzeSentimentFunction == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Python function not initialized.");
		}

		try {
			Value result = analyzeSentimentFunction.execute(text);
			return ResponseEntity.ok(result.asString());
		} catch (Exception e) {
			System.err.println("Error analyzing sentiment: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while analyzing sentiment.");
		}
	}

	@PreDestroy
	public void close() {
		if (polyglotContext != null) {
			polyglotContext.close(true);
		}
	}
}
