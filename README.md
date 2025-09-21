# Student Monitor Application

A Spring Boot REST API application for managing students and their performance records.

## Features

- **Student Management**: Create, read, update, and delete student records
- **Performance Tracking**: Track student performance across different subjects
- **H2 Database**: In-memory database for development and testing
- **Validation**: Input validation with meaningful error messages
- **Sample Data**: Pre-loaded demo data for testing

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use the included Maven wrapper)

## How to Run

### Option 1: Using Maven Wrapper (Recommended)
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

### Option 2: Using IDE
1. Import the project into your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Run the `StudentMonitorApplication.java` main class

### Option 3: Using Maven (if installed)
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Student Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/students` | Get all students |
| GET | `/api/students/{id}` | Get student by ID |
| POST | `/api/students` | Create new student |
| PUT | `/api/students/{id}` | Update student |
| DELETE | `/api/students/{id}` | Delete student |

### Performance Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/students/{studentId}/performances` | Get performances for a student |
| POST | `/api/students/{studentId}/performances` | Create performance record for student |
| GET | `/api/performances/{id}` | Get performance by ID |
| PUT | `/api/performances/{id}` | Update performance record |
| DELETE | `/api/performances/{id}` | Delete performance record |

## Sample API Requests

### 1. Create a Student
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Johnson",
    "email": "alice.johnson@example.com",
    "dateOfBirth": "2001-03-10"
  }'
```

### 2. Get All Students
```bash
curl -X GET http://localhost:8080/api/students
```

### 3. Get Student by ID
```bash
curl -X GET http://localhost:8080/api/students/1
```

### 4. Add Performance Record for Student
```bash
curl -X POST http://localhost:8080/api/students/1/performances \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "Computer Science",
    "score": 94.5,
    "date": "2024-09-25",
    "remarks": "Excellent programming skills"
  }'
```

### 5. Get Performance Records for Student
```bash
curl -X GET http://localhost:8080/api/students/1/performances
```

### 6. Update Student
```bash
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe.updated@example.com",
    "dateOfBirth": "2000-05-15"
  }'
```

### 7. Update Performance Record
```bash
curl -X PUT http://localhost:8080/api/performances/1 \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "Advanced Mathematics",
    "score": 88.0,
    "date": "2024-09-15",
    "remarks": "Improved understanding of calculus"
  }'
```

### 8. Delete Performance Record
```bash
curl -X DELETE http://localhost:8080/api/performances/1
```

### 9. Delete Student
```bash
curl -X DELETE http://localhost:8080/api/students/1
```

## H2 Database Console

The application includes an H2 in-memory database with a web console for easy data inspection.

### Access Information
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:file:./data/studentdb`
- **Username**: `sa`
- **Password**: (leave empty)

### Using the Console
1. Start the application
2. Open http://localhost:8080/h2-console in your browser
3. Use the credentials above to connect
4. You can view and query the `STUDENT` and `PERFORMANCE_RECORD` tables

## Sample Data

The application automatically creates sample data on startup:

### Students
- **John Doe** (john.doe@example.com) - Born May 15, 2000
- **Jane Smith** (jane.smith@example.com) - Born August 22, 1999

### Performance Records
- Multiple performance records for both students across subjects like Mathematics, Physics, Chemistry, English Literature, and History

## Data Models

### Student
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "dateOfBirth": "2000-05-15"
}
```

### Performance Record
```json
{
  "id": 1,
  "subject": "Mathematics",
  "score": 85.5,
  "date": "2024-09-15",
  "remarks": "Good understanding of algebra",
  "studentId": 1
}
```

## Error Handling

The API returns structured error responses:

### Validation Error (400 Bad Request)
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2025-09-21T10:30:00",
  "fieldErrors": {
    "email": "Email should be valid",
    "firstName": "First name is required"
  }
}
```

### Resource Not Found (404 Not Found)
```json
{
  "status": 404,
  "message": "Student not found with id: 123",
  "timestamp": "2025-09-21T10:30:00"
}
```

## Technology Stack

- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **H2 Database**
- **Spring Validation**
- **Spring Web MVC**
- **Java 17**
- **Maven**

## Project Structure

```
src/main/java/com/example/studentmonitor/
├── StudentMonitorApplication.java          # Main application class
├── config/
│   └── DataInitializer.java               # Sample data initialization
├── controller/
│   ├── StudentController.java             # Student REST endpoints
│   └── PerformanceController.java         # Performance REST endpoints
├── dto/
│   ├── StudentDTO.java                    # Student data transfer object
│   └── PerformanceDTO.java               # Performance data transfer object
├── exception/
│   ├── ResourceNotFoundException.java     # Custom exception
│   └── GlobalExceptionHandler.java       # Global exception handling
├── model/
│   ├── Student.java                       # Student entity
│   └── PerformanceRecord.java            # Performance entity
├── repository/
│   ├── StudentRepository.java             # Student data access
│   └── PerformanceRepository.java        # Performance data access
└── service/
    ├── StudentService.java                # Student service interface
    ├── PerformanceService.java           # Performance service interface
    └── impl/
        ├── StudentServiceImpl.java       # Student service implementation
        └── PerformanceServiceImpl.java   # Performance service implementation
```

## Development

### Build
```bash
.\mvnw.cmd clean compile
```

### Test
```bash
.\mvnw.cmd test
```

### Package
```bash
.\mvnw.cmd clean package
```

The packaged JAR will be available in the `target/` directory.