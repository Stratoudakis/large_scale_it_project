# Project: Large Scale IT Project

## Overview
This project is a large-scale Java-based application that appears to implement a sales and order management system. It includes modules for managing clients, orders, products, sales, and warehouse operations. The project is structured using the Maven build system and incorporates various best practices, such as modularized components and configuration management.

## Features
- **Client Management**: Includes functionality for managing client data.
- **Order Processing**: Handles the lifecycle of orders, including placement and tracking.
- **Product Management**: Allows for the management of product data.
- **Sales Integration**: Facilitates sales operations and reporting.
- **Warehouse Operations**: Manages inventory and shipping processes.
- **Security Configurations**: Implements security protocols to protect the system.

## Project Structure
```
large_scale_it_project
├── pom.xml                   # Maven build file
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── lsit
│   │   │   │   ├── Controllers  # Application controllers
│   │   │   │   ├── Models       # Data models
│   │   │   │   ├── Repositories # Data repositories
│   │   │   │   ├── Utils        # Utility classes
│   │   │   │   ├── Main.java    # Main application entry point
│   │   │   │   ├── Config.java # Security configuration
│   │   │   │   
│   │   ├── resources
│   │   │   └── application.yml # Application configuration
├── target                    # Compiled classes and build artifacts
└── .git                      # Git version control metadata
```

## Prerequisites
- **Java 11 or later**: Required for running the application.
- **Maven**: For building the project.
- **Git**: For version control.
- **Google Cloud Storage Bucket**: Used as the database for storing data.

## Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone <https://github.com/Stratoudakis/large_scale_it_project.git>
   ```

2. **Navigate to the Project Directory**:
   ```bash
   cd large_scale_it_project
   ```

3. **Build the Project**:
   ```bash
   mvn clean install
   ```

4. **Run the Application**:
   ```bash
   java -jar target/my-project-1.0-SNAPSHOT.jar
   ```

## Configuration
Update the `application.yml` file located in the `src/main/resources` directory with your environment-specific settings, such as database connection details and server configurations.

## Key Components
### Controllers
- **ClientController**: Handles client-related operations.
- **CreditController**: Manages credit-related operations.
- **HomeController**: Provides a home endpoint.
- **ProductController**: Handles product-related operations.
- **SalesController**: Manages sales processes.
- **WarehouseController**: Oversees warehouse operations.

### Models
- **Client**: Represents client data.
- **Order**: Represents order data.
- **Product**: Represents product data.

### Repositories
- **ClientRepository**: Interface for client data persistence.
- **OrderRepository**: Interface for order data persistence.
- **ProductRepository**: Interface for product data persistence.

### Utilities
- **AppConfig**: Application-level configurations.

### Security
The application includes a `Config` class to manage authentication and authorization settings.

### CI/CD Pipeline

This project uses Google Cloud Run as the CI/CD pipeline. The pipeline automates the build, test, and deployment processes, enabling seamless deployment of the application to the cloud environment. Integration with GCP ensures scalability and reliability for production deployments.


