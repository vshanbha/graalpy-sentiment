# Project Documentation

## Overview

This document provides an overview of the sentiment analysis project, its structure, and key components. It is intended to help developers understand the code base and contribute effectively.

## Key Files and Components

### 1. `SentimentAnalysisController.java`

This file is the main Spring Boot controller that handles sentiment analysis requests. It initializes the GraalVM context, loads the Python script, and exposes an endpoint to analyze the sentiment of a given text.

### 2. `application.properties`

This file contains the configuration properties for the Spring Boot application, including the server port, application name, and the path to the Python script.

### 3. `sentiment_analysis.py`

This Python script uses the `textblob` library to analyze the sentiment of a given text. It returns a sentiment score and a classification (positive, negative, or neutral).

### 4. `LoadtestSentimentScore.jmx`

This JMeter script is used to perform load testing on the sentiment analysis endpoint. It sends multiple requests to the `/analyze` endpoint with different text inputs from `sentiments.csv` and measures the response time and throughput.

### 5. `sentiments.csv`

This CSV file contains a list of sample sentiment texts used by the JMeter script for load testing.

### 6. `resource-config.json`

This file configures which resources should be included in the GraalVM native image. In this case, it includes relevant `.py` files.

### 7. `reachability-metadata.json`

This file contains resources that are required by the application at runtime and are made available through resources or reflection and thus not obvious for the GraalVM compiler. To provide hints to the compiler we generate this file using `native-image-agent`

```
$GRAALVM_HOME/bin/java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image/com.example/graalpy-sentiment -jar target/graalpy-sentiment-1.0-SNAPSHOT.jar
```

