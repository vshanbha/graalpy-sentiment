# Sentiment Analysis Application

This project is a sentiment analysis application that provides an API for analyzing the sentiment of a given text. The application is built using Java with Spring Boot for the API and Python with the TextBlob library for performing sentiment analysis.

[![Build](https://github.com/vshanbha/graalpy-sentiment/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/vshanbha/graalpy-sentiment/actions/workflows/maven.yml)
[![CodeQL](https://github.com/vshanbha/graalpy-sentiment/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/vshanbha/graalpy-sentiment/actions/workflows/github-code-scanning/codeql)

## Project Structure

```
sentiment-analysis-app
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── example
│       │           └── SentimentAnalysisController.java
│       └── resources
│           ├── application.properties
│           └── sentiment_analysis.py
├── pom.xml
└── README.md
```

Notes about various files are available under [Project Documentation](project_documentation.md) 

## Requirements

- Java 17 or higher
- Maven
- Python 3.x
- TextBlob library for Python

## Setup Instructions

1. **Clone the repository:**

   ```
   git clone 
   
   cd sentiment-analysis-app
   ```

2. **Optional: Install Python dependencies:**

   This step is completely optional. However, if we want to modify the python code then it's a good idea to have a virtual environment for our Python project to manage dependencies:

   ```
   python3 -m venv .venv
   ```
   
   Then we need to activate the virtual environment:
   
   ```
   source .venv/bin/activate  # On Linux/macOS
   # .venv\Scripts\activate  # On Windows   
   ```
   
   The Python script sentiment_analysis.py uses the `textblob` library. 
   Therefore, we need to install it.  We can do this using pip:

   ```
   pip install textblob
   ```
   
   We can now run the python script inside the venv:
   
   ```
   python src/main/resources/sentiment_analysis.py
   ```

   The Java application uses Graalpy to run the Python part so the venv is not used at all. However it helps in editing and testing the Python code independent of the Java dependencies and its much faster to work with Python this way. 
   
   
3. **Build the Java application:**

   Use Maven to build the project:
   
   ```
   mvn clean install
   ```
   
   This creates the executable jar inside the target directory. The jar would be named as graalpy-sentiment-<version>.jar
   
   Now the jar can be executed as:
   
   ```
   java -jar graalpy-sentiment-<version>.jar
   ```
   
   Remember to replace the <version> place holder with the actual version number.

4. **Run the application:**

   We can run the Spring Boot application using:
   
   ```
   mvn spring-boot:run
   ```

5. **Access the API:**

   The API will be available at `http://localhost:8080/analyze` (or the port specified in `application.properties`). We can send a POST request with the text to analyze.
   
6. **Creating native executable:**

   We can run create a native executable file using the GraalVM. We need to ensure that we have the [GraalVM](https://www.oracle.com/uk/java/technologies/downloads/#graalvmjava24) installed on our build machine. Note that GraalVM native executable only works on the platform on which we built the executable.
   
   The native image build command itself is already included into the `pom.xml` courtesy Spring Boot:
   
   ```
   mvn -Pnative native:compile
   ```

   This produces a executable named `graalpy-sentiment` in the target folder. The compilation takes much longer time the jar compilation.
   
## Usage

To analyze sentiment, send a GET request to the `/analyze` endpoint with a JSON body containing the text. For example:

```
http://localhost:8080/analyze?text=I love programming!

```

The response will include the sentiment score and classification.

## Docker Instructions

### Build a Docker Image

We can build a Docker image using the Spring Boot Maven plugin:

```
mvn clean spring-boot:build-image
```

### Run the Docker Container

To run the Docker container, use the following command:

```
docker run -d -p 8080:8080 --name your-sentiment-app your-image-name 
```

This will start the application inside a Docker container, mapping port 8080 on our host to port 8080 in the container.

### Providing an External Python Script

To provide an external Python script to the Docker container, we can mount a volume and set the `JAVA_OPTS` environment variable:

```
docker run -d -p 8080:8080 \ -v /path/to/your/python/scripts:/opt/python_scripts \ -e JAVA_OPTS="-Dexternal.python.script=/opt/python_scripts/external_sentiment_analysis.py" \ your-image-name
```

Explanation:

-   `-v /path/to/your/python/scripts:/opt/python_scripts`: This mounts the local directory `/path/to/your/python/scripts` on our host machine to the directory `/opt/python_scripts` inside the container.
-   `-e JAVA_OPTS="-Dexternal.python.script=/opt/python_scripts/external_sentiment_analysis.py"`: This sets the `JAVA_OPTS` environment variable to include the `-Dexternal.python.script` system property, pointing to the location of the Python script inside the container.

Ensure that the Python script is accessible inside the container at the specified path (`/opt/python_scripts/external_sentiment_analysis.py` in this example).


## Contributing

Contributions are welcome! 
Feel free to submit issues or pull requests for improvements or bug fixes.

Please follow these guidelines for bug fixes:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Write tests for your changes.
4.  Submit a pull request.


## License

This project is licensed under the MIT License.
