# Sentiment Analysis Application

This project is a sentiment analysis application that provides an API for analyzing the sentiment of a given text. The application is built using Java with Spring Boot for the API and Python with the TextBlob library for performing sentiment analysis.

## Project Structure

```
sentiment-analysis-app
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── SentimentAnalysisController.java
│   │   └── resources
│   │       └── application.properties
│   └── python
│       └── sentiment_analysis.py
├── pom.xml
└── README.md
```

## Requirements

- Java 11 or higher
- Maven
- Python 3.x
- TextBlob library for Python

## Setup Instructions

1. **Clone the repository:**
   ```
   git clone <repository-url>
   cd sentiment-analysis-app
   ```

2. **Install Python dependencies:**

   It's a good idea to have a virtual environment for your Python project to manage dependencies:

   ```
   python3 -m venv .venv
   ```
   
   Then we need to activate the virtual environment:
   
   ```
   source .venv/bin/activate  # On Linux/macOS
   # .venv\Scripts\activate  # On Windows   
   ```
   
   The Python script sentiment_analysis.py uses the `textblob` library. 
   Therefore, you need to install it.  You can do this using pip:

   ```
   pip install textblob
   ```


3. **Build the Java application:**
   Use Maven to build the project:
   ```
   mvn clean install
   ```

4. **Run the application:**
   You can run the Spring Boot application using:
   ```
   mvn spring-boot:run
   ```

5. **Access the API:**
   The API will be available at `http://localhost:8080/analyze` (or the port specified in `application.properties`). You can send a POST request with the text to analyze.

## Usage

To analyze sentiment, send a GET request to the `/analyze` endpoint with a JSON body containing the text. For example:

```
http://localhost:8080/analyze?text=I love programming!

```

The response will include the sentiment score and classification.

## Docker Instructions

### Build a Docker Image

You can build a Docker image using the Spring Boot Maven plugin:

```
mvn clean spring-boot:build-image
```

### Run the Docker Container

To run the Docker container, use the following command:

```
docker run -d -p 8080:8080 your-image-name
```

This will start the application inside a Docker container, mapping port 8080 on your host to port 8080 in the container.

### Providing an External Python Script

To provide an external Python script to the Docker container, you can mount a volume and set the `JAVA_OPTS` environment variable:

```
docker run -d -p 8080:8080 \ -v /path/to/your/python/scripts:/opt/python_scripts \ -e JAVA_OPTS="-Dexternal.python.script=/opt/python_scripts/external_sentiment_analysis.py" \ your-image-name
```

Explanation:

-   `-v /path/to/your/python/scripts:/opt/python_scripts`: This mounts the local directory `/path/to/your/python/scripts` on your host machine to the directory `/opt/python_scripts` inside the container.
-   `-e JAVA_OPTS="-Dexternal.python.script=/opt/python_scripts/external_sentiment_analysis.py"`: This sets the `JAVA_OPTS` environment variable to include the `-Dexternal.python.script` system property, pointing to the location of the Python script inside the container.

Ensure that the Python script is accessible inside the container at the specified path (`/opt/python_scripts/external_sentiment_analysis.py` in this example).


## Contributing

Feel free to submit issues or pull requests for improvements or bug fixes.

## License

This project is licensed under the MIT License.