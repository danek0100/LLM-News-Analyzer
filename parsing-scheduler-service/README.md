# Link Parsing Scheduler Service

This service is designed to manage and schedule links for parsing. It interacts with a Kafka messaging system for communication, Redis for caching, and uses a scheduling mechanism to handle periodic tasks. The service sends link data (URL and last checked timestamp) to Kafka topics for further processing and updates the status in Redis.

## Features
- **Link Management**: Manage links for parsing and tracking last checked timestamps.
- **Kafka Integration**: Send link data to Kafka topics for further processing.
- **Redis Caching**: Store link information in Redis for fast access and updates.
- **Scheduling**: Periodic task execution for managing and sending links to Kafka.
  
## Technologies Used
- **Spring Boot**: The backbone of the service, providing the application structure.
- **Reactor Kafka**: For asynchronous message sending and receiving via Kafka.
- **Spring Data Redis**: For managing link data storage in Redis.
- **Kafka**: A distributed streaming platform used for managing data flow.
- **Docker**: For containerization of the service and dependencies like Kafka and Redis.
- **TestContainers**: For integration tests using embedded Kafka and Redis.

## Setup Instructions

### 1. Clone the repository
Clone the repository to your local machine:

```bash
git clone https://github.com/yourusername/link-parsing-scheduler-service.git
cd link-parsing-scheduler-service
```

### 2. Docker Setup
Make sure Docker is installed and running on your machine. Use Docker Compose to set up Kafka, Redis, and your service:

```bash
docker-compose up -d
```

This will start the Kafka, Redis, and the service containers.

### 3. Run the Service
After Docker containers are up, you can run the service using:

```bash
./mvnw spring-boot:run
```

Alternatively, you can build the project and run it directly:

```bash
mvn clean install
java -jar target/link-parsing-scheduler-service.jar
```

### 4. Configuration
You can configure the service by modifying the `application.properties` and `application-develop.properties` files.

- Kafka Configuration: `spring.kafka.bootstrap-servers`
- Redis Configuration: `spring.data.redis.host`, `spring.data.redis.port`
- Server Configuration: `server.port`

### 5. Testing
The service includes unit and integration tests using `JUnit 5` and `Mockito`. You can run tests using:

```bash
./mvnw test
```

For integration testing, embedded Kafka and Redis are used.

## API Endpoints

- **GET `/api/v1/links/`**: Retrieve all links.
- **GET `/api/v1/links/search?url={url}`**: Retrieve a specific link by URL.
- **POST `/api/v1/links/`**: Add a new link.
- **PUT `/api/v1/links/`**: Update the "lastChecked" timestamp for a link.
- **DELETE `/api/v1/links/`**: Delete a link by URL.

## Example Usage

### Adding a link:
```bash
curl -X POST "http://localhost:8080/api/v1/links/?url=http://example.com"
```

### Fetching all links:
```bash
curl -X GET "http://localhost:8080/api/v1/links/"
```

### Fetching a link by URL:
```bash
curl -X GET "http://localhost:8080/api/v1/links/search?url=http://example.com"
```

### Updating the "lastChecked" timestamp:
```bash
curl -X PUT "http://localhost:8080/api/v1/links/?url=http://example.com"
```

### Deleting a link:
```bash
curl -X DELETE "http://localhost:8080/api/v1/links/?url=http://example.com"
```

## Contributing

1. Fork this repository.
2. Create a new branch (`git checkout -b feature-name`).
3. Make your changes.
4. Commit your changes (`git commit -am 'Add new feature'`).
5. Push to your branch (`git push origin feature-name`).
6. Open a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
