# PrintScript Service

A Spring Boot service for executing, formatting, and linting PrintScript code.  
This service exposes REST endpoints for executing PrintScript code, applying formatting rules, and validating syntax or style through linting configurations.

---

## Features

- Code Execution — Execute PrintScript code and return the result.
- Code Formatting — Format code based on configurable style rules.
- Code Linting — Validate code against linting configurations.
- Health Check — Verify service health status.
- Docker Support — Ready for containerized deployment.

---

## Tech Stack

| Component | Technology |
|------------|-------------|
| Language | Kotlin (with Java interoperability) |
| Framework | Spring Boot |
| Build Tool | Gradle |
| API | REST (Spring Web) |
| Containerization | Docker |

---

## Getting Started

### Prerequisites
- JDK 17 or higher
- Gradle 7.0+
- Docker (optional, for containerized deployment)

### Local Development

```bash
git clone <repository-url>
cd printscript-service
./gradlew build
./gradlew bootRun
```

Access the service at:
- Application: http://localhost:8080  
- Health Check: http://localhost:8080/health

---

## Docker Deployment

### Using Docker Compose
```bash
docker-compose up --build
```

### Manual Docker build and run
```bash
docker build -f Dockerfile.local -t printscript-service .
docker run -p 8080:8080 printscript-service
```

---

## API Endpoints

### Execute Code

**POST** `/api/printscript/execute`  
**Content-Type:** multipart/form-data  

Parameters:
- `snippet`: file — PrintScript code file  
- `version`: string — PrintScript version (e.g., "1.0")

Example:
```bash
curl -X POST http://localhost:8080/api/printscript/execute   -F "snippet=@code.txt"   -F "version=1.0"
```

---

### Format Code

**POST** `/api/printscript/format`  
**Content-Type:** multipart/form-data  

Parameters:
- `snippet`: file — PrintScript code file  
- `config`: file — JSON configuration file  
- `version`: string — PrintScript version  

Format configuration example:
```json
{
  "enforce-spacing-around-equals": true
}
```

Example:
```bash
curl -X POST http://localhost:8080/api/printscript/format   -F "snippet=@code.txt"   -F "config=@format-config.json"   -F "version=1.0"
```

---

### Lint Code

**POST** `/api/printscript/verify`  
**Content-Type:** multipart/form-data  

Parameters:
- `snippet`: file — PrintScript code file  
- `config`: file — JSON configuration file  
- `version`: string — PrintScript version  

Lint configuration example:
```json
{
  "identifier_format": "camel case"
}
```

Example:
```bash
curl -X POST http://localhost:8080/api/printscript/verify   -F "snippet=@code.txt"   -F "config=@lint-config.json"   -F "version=1.0"
```

---

### Health Check

**GET** `/health`  
Returns service status.

---

## Configuration

### Application Properties

`src/main/resources/application.properties`
```properties
server.port=8080
spring.application.name=printscript-service
```

### Format Configuration Options

| Key | Type | Description |
|------|------|-------------|
| enforce-spacing-around-equals | Boolean | Adds spaces around equals signs |
| indent | Number | Sets indentation spaces |

### Lint Configuration Options

| Key | Type | Description |
|------|------|-------------|
| identifier_format | String | Variable naming style ("camel case" / "snake case") |

---

## Error Handling

| Code | Meaning |
|------|----------|
| 200 OK | Successful operation |
| 400 Bad Request | Invalid input or configuration |
| 500 Internal Server Error | Unexpected service error |

Error responses return a JSON body with details.

---


## Development

### Running Tests
```bash
./gradlew test
```

### Code Style
Follow Kotlin coding conventions and ensure formatting before committing.

### Building for Production
```bash
./gradlew bootJar
```
The JAR file will be located in `build/libs/`.

---

## Docker Configuration

Includes:
- `Dockerfile.local` — Local development build
- `docker-compose.yml` — Orchestration for local development

---

## Contributing

1. Fork this repository  
2. Create a new branch (`git checkout -b feature/amazing-feature`)  
3. Commit your changes (`git commit -m "Add amazing feature"`)  
4. Push to your branch (`git push origin feature/amazing-feature`)  
5. Open a Pull Request  

---

## License

[Specify license here, e.g. MIT License]
