Loan Management Backend (Spring Boot)
A robust backend service for the Loan Management application, built with Spring Boot and Spring Data JPA. This project provides RESTful APIs to manage loan-related operations, such as creating, retrieving, updating, and deleting loan records, with data persistence handled via a relational database.

Features
Spring Boot: A powerful framework for building production-ready applications with minimal configuration.
Spring Data JPA: Simplifies database operations with JPA-based repositories for CRUD functionality.
RESTful APIs: Exposes endpoints for loan management, designed to integrate seamlessly with the frontend.
Maven: Dependency management and build tool for the project.

Prerequisites
Before you begin, ensure you have the following installed:

Java (JDK 17 or higher recommended)
Maven (v3.6 or higher)
Git (for cloning the repository)
A running instance of the frontend (optional, for full integration testing)

Getting Started

1. Clone the repository
   git clone https://github.com/your-username/LOAN-MANAGEMENT-IN-SPRINGBOOT.git
cd LOAN-MANAGEMENT-IN-SPRINGBOOT
2. Install dependencies
   mvn clean install

Running the application
mvn spring-boot:run

API Endpoints
The backend exposes the following RESTful endpoints (base URL: http://localhost:8080/api): (These are only a few examples)

GET /api/loans: Retrieve all loans.
GET /api/loans/{id}: Retrieve a loan by ID.
POST /api/loans: Create a new loan.
PUT /api/loans/{id}: Update an existing loan.
DELETE /api/loans/{id}: Delete a loan by ID.
You can test these endpoints using tools like Postman.

Example of JSON testint the POST request in POSTMAN
 {
        "amountRequested": 53892,
        "loanType" : {
            "id": 3
}

Project Structure 
LOAN-MANAGEMENT-IN-SPRINGBOOT/
├── mvnw/                # Maven wrapper
├── src/                 # Source code
│   ├── main/            # Main application code
│   │   ├── java/        # Java source files
│   │   │   └── com/     # Package structure
│   │   │       └── example/loanmanagement/
│   │   │           ├── controller/  # REST controllers
│   │   │           ├── model/       # JPA entities
│   │   │           ├── repository/  # Spring Data JPA repositories
│   │   │           ├── service/     # Business logic
│   │   │           └── LoanManagementApplication.java  # Main application class
│   │   └── resources/   # Configuration and static resources
│   │       ├── application.properties  # Spring Boot configuration
│   │       └── data.sql                # Optional: Initial data for H2 database
│   └── test/            # Test code
├── .gitattributes       # Git attributes
├── .gitignore           # Git ignore file
├── mvnw.cmd             # Maven wrapper for Windows
├── pom.xml              # Maven configuration
└── README.md            # This file

Maven Dependencies
Key dependencies in pom.xml include:

spring-boot-starter-web: For building RESTful APIs.
spring-boot-starter-data-jpa: For database operations with JPA.
h2: In-memory database for development.
spring-boot-starter-test: For testing.

CORS Configuration
To allow the frontend (e.g., React app running on http://localhost:5173) to access the backend, configure CORS in LoanManagementApplication.java:
        import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*");
            }
        };
    }
}

Testing 
Run unit test using maven
mvn test

Contributing
Fork the repository.
Create a feature branch (git checkout -b feature/your-feature).
Commit your changes (git commit -m "Add your feature").
Push to the branch (git push origin feature/your-feature).
Open a pull request.


