# Ecommerce Application

## Description

This is an Ecommerce application developed using Java with Spring Webflux, Maven, SQL and MongoDB. The application provides a platform for users to browse and purchase products, add them to their cart, and place orders.

## Objectives

The main objective of this project is to provide an efficient ecommerce platform. It allows users to browse through products, add them to their cart, and place orders with ease. Additionally, administrators can manage products and user orders.

## Technologies

- **Java**: The primary programming language used for backend development.
- **Spring WebFlux**: A non-blocking web stack to handle concurrency with a small number of threads and scale with fewer hardware resources.
- **Spring Data R2DBC**: A part of Spring Data project that makes it easy to implement RDBMS operations using the reactive paradigm.
- **Spring Security**: A powerful and highly customizable authentication and access-control framework to secure Spring-based applications.
- **JWT (JSON Web Token)**: A compact, URL-safe means of representing claims to be transferred between two parties. Used for secure transmission of information between parties as a JSON object.
- **Spring Cloud**: Provides tools for developers to quickly build some of the common patterns in distributed systems.
- **Eureka**: A REST based service that is primarily used in the AWS cloud for locating services for the purpose of load balancing and failover of middle-tier servers.
- **Maven**: A software project management and comprehension tool.
- **SQL**: A structured query language used for managing the database.
- **MongoDB**: A source-available cross-platform document-oriented database program.


## Testing

- **JUnit 5**: The next generation of JUnit, a simple framework to write repeatable tests in Java. It is used for unit testing of individual classes and methods.
- **Mockito**: A mocking framework for Java. It simplifies the development of tests by mocking external dependencies and injecting them into the class under test.
- **Integration Tests**: These tests check the interaction between different parts of the application, such as services, repositories, or even between different microservices.


## Architecture

The project follows a microservices architecture. Each microservice is responsible for a specific functionality and communicates with others through REST APIs.

## Microservices

- **User Management Microservice**: Manages user data and provides functionalities such as user registration, user authentication, and user profile management.
- **Product Catalog Microservice**: Manages the product catalog and provides functionalities such as adding new products, updating product details, and deleting products.
- **MyData Microservice**: Manages user orders and provides functionalities such as creating new orders, updating order status, deleting orders, etc.
- **Shopping Cart Microservice**: Manages the shopping cart and provides functionalities such as adding products to the cart, updating the quantity of products in the cart, and deleting products from the cart.
- **API Gateway Microservice**: Acts as a single entry point for all client requests and routes requests to the appropriate microservice.
- **Discovery Service**: Keeps track of all services in the system and their instances.
- **Payment Microservice**: Handles payment processing for orders.

## Installation

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Run `mvn clean install` to build the project.
4. Run `java -jar target/<ReactivEcommerce-Project>.jar` to start the application.

## Testing

To run the tests, use the `mvn test` command.

## Contributing

If you wish to contribute to the project, please fork the repository, make your changes, and submit a pull request.
