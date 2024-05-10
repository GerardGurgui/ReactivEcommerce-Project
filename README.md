# Ecommerce Application

## Description

This is an ecommerce application developed using Java, Maven, SQL, MongoDB, Spring WebFlux, and JavaScript. The application provides a platform for users to browse and purchase products, add them to their cart, and place orders.

## Objectives

The main objective of this project is to provide an efficient and user-friendly ecommerce platform. It allows users to browse through products, add them to their cart, and place orders with ease. Additionally, administrators can manage products and user orders.

## Technologies

- **Java**: The primary programming language used for backend development.
- **Maven**: A software project management and comprehension tool.
- **SQL**: A structured query language used for managing the database.
- **MongoDB**: A source-available cross-platform document-oriented database program.
- **Spring WebFlux**: A non-blocking web stack to handle concurrency with a small number of threads and scale with fewer hardware resources.
- **JavaScript**: A programming language used for frontend development.

## Architecture

The project follows a microservices architecture. Each microservice is responsible for a specific functionality and communicates with others through REST APIs.

## Microservices

- **User Management Microservice**: Manages user data and provides functionalities such as user registration, user authentication, and user profile management.
- **Product Catalog Microservice**: Manages the product catalog and provides functionalities such as adding new products, updating product details, and deleting products.
- **Order Management Microservice**: Previously known as MyData-service, manages user orders and provides functionalities such as creating new orders, updating order status, and deleting orders.
- **Shopping Cart Microservice**: Manages the shopping cart and provides functionalities such as adding products to the cart, updating the quantity of products in the cart, and deleting products from the cart.
- **API Gateway**: Acts as a single entry point for all client requests and routes requests to the appropriate microservice.
- **Discovery Service**: Keeps track of all services in the system and their instances.
- **Payment Service**: Handles payment processing for orders.

## Installation

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Run `mvn clean install` to build the project.
4. Run `java -jar target/<project-name>.jar` to start the application.

## Testing

To run the tests, use the `mvn test` command.

## Contributing

If you wish to contribute to the project, please fork the repository, make your changes, and submit a pull request.
