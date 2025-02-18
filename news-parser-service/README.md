# News Parser Service

news-parser-service is a reactive-based microservice responsible for parsing news articles from various news sources and sending them to Kafka. The service supports multiple news parsers, which are selected dynamically based on the provided URL. It processes incoming requests, parses the content, and then serializes the data to be sent to Kafka for further processing.

## Features

- **Dynamic Parser Selection**: Automatically selects the appropriate parser for the provided URL.
- **Reactor-based**: Built with Spring WebFlux for non-blocking, reactive processing.
- **Kafka Integration**: Sends parsed articles to Kafka for further processing or storage.
- **JSON Parsing**: Parses JSON or HTML responses to extract articles from multiple sources.
- **Error Handling**: Logs and handles errors gracefully during parsing and Kafka communication.

## Technologies Used

- **Spring Boot**: For building the application with reactive programming support.
- **Spring WebFlux**: To handle requests reactively.
- **Kafka**: To send parsed articles to Kafka topics.
- **JSoup**: For parsing HTML from websites.
- **Jackson**: For JSON serialization and deserialization.
- **Reactor Core**: For reactive programming using Mono and Flux.

## Architecture Overview

1. **Parser Factory**: Based on the URL, selects the appropriate parser (e.g., RBC, TradingView, AlenkaCapital) to process the content.
2. **News Parsers**: Extract data from different news sources.
3. **Link Processing Service**: Coordinates the parsing process and sends data to Kafka.
4. **Controller**: Exposes endpoints for submitting URLs and retrieving parsed articles.

## Setup and Installation

### Prerequisites

- JDK 17 or later
- Docker (optional, for Kafka and other services)
- Apache Kafka (for sending parsed articles)

### Clone the repository

```bash
git clone https://github.com/yourusername/news-parser-service.git
cd news-parser-service
```

### Build the Project
If you're using Maven to build the project, you can run the following command:

```bash
./mvnw clean install
```

### Run the Project
You can run the service directly with:

```bash
./mvnw spring-boot:run
```

This will start the application on the default port (8080).

### Kafka Setup
The service assumes Kafka is running locally. You can run Kafka using Docker with the following command:

```bash
docker-compose -f docker/docker-compose.yml up
```

Ensure that Kafka is up and running before starting the service.

## Usage

### Parse Link API

#### Request
- Method: POST
- Endpoint: /api/parser/parse

Request Body:
```json
{
  "url": "https://www.rbc.ru/news/2023/02/15/635d6a1f9a794e001c0b99f1",
  "lastParsedTime": "2023-02-14T10:00:00"
}
```

Response:
- 200 OK: A list of parsed articles.

```json
[
  {
    "title": "Article Title 1",
    "publishedAt": "2023-02-15T12:30:00"
  },
  {
    "title": "Article Title 2",
    "publishedAt": "2023-02-15T13:00:00"
  }
]
```
- 500 Internal Server Error: If there is a failure in parsing or communication with Kafka.

### Service Flow

- The user sends a POST request with the URL to be parsed and the lastParsedTime.
- The service determines which parser is suitable based on the URL and parses the content.
- Articles are serialized and sent to Kafka for further processing.
- If parsing fails, an error is logged.

## Testing
### Unit Tests

Unit tests are written using JUnit 5 and Mockito. To run the tests:

```bash
./mvnw test
```

### Integration Tests
The integration tests are available in the src/test/java directory and test the integration between the parser service and Kafka.

## Running with Docker

If you want to run the service with Docker, you can build and start the service using Docker Compose:

Build the Docker image:

```bash
docker build -t news-parser-service .
```
Start the service with Docker Compose:
```bash
docker build -t news-parser-service .
```

This will start the service without Kafka in a containerized environment.


## Contributing

1. Fork this repository.
2. Create a new branch (`git checkout -b feature-name`).
3. Make your changes.
4. Commit your changes (`git commit -am 'Add new feature'`).
5. Push to your branch (`git push origin feature-name`).
6. Open a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
