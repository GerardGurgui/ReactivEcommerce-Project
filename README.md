## 🛒 ReactivEcommerce – Reactive Ecommerce Microservices Platform

ReactivEcommerce is a modern, event-driven, cloud-ready Ecommerce platform built with Java 17, Spring WebFlux, Reactive Programming, Docker, Kafka, and a microservices architecture.
The system is designed for high throughput, non-blocking IO, horizontal scalability, and clean domain-driven separation.


## ✨ Key Features

⚡ Reactive (Non-Blocking) Architecture using Spring WebFlux

🧩 Modular Microservices, each responsible for a core business domain

🔐 JWT Authentication & Authorization using Spring Security Resource Servers

🔐 Internal Service-to-Service Authentication (API-Key) for private internal communication

📦 Kafka Event Streaming for login events, future order events, and asynchronous workflows

🗄️ Reactive PostgreSQL (R2DBC) for product catalog data

📘 Reactive MongoDB for user data and document storage

🐳 Docker + Docker Compose for full environment orchestration

📡 REST APIs for internal and external clients

🏗️ Scalable architecture ready for production

🔍 Future integration of Eureka/Discovery & load-balanced communication


## 🏗️ Microservices Overview

The platform consists of the following microservices:


🔑 User Authentication Service

Handles login, JWT token generation, and account validation

Publishes User Login Events to Kafka

Communicates securely with UserManagement through internal API-Key

Reactive password validation with BCrypt

Fully stateless JWT-based authentication


👤 User Management Service

Manages users (registration, profile, flags, security state)

Stores user data in MongoDB

Provides Resource Server protection (JWT validation)

Consumes Kafka login events and updates latestAccess

Exposes internal endpoints protected with API-Key

Supports future role-based permissions


📦 Product Catalog Service

Manages products using PostgreSQL via Spring Data R2DBC

Reactive searches, pagination, dynamic filtering

Future integration with caching layer (Redis)


🧺 Shopping Cart Service

Manages user carts, line items, quantities

Reactive operations

Communicates with Product Catalog and User Management


📊 MyData Service

Stores user-related transactional data

Acts as a bridge between Orders and User services

Calls UserManagement using API-Key protected internal endpoints

Secured as JWT Resource Server


💳 Payment Service

Manages payment processing

Integrates with external payment gateways (Stripe, PayPal, TBD)

Works alongside the upcoming Order Service


🚪 API Gateway (Future Integration)

Will serve as the unified entrypoint for clients

Will support:

Routing

Rate limiting

Filters

JWT validation

Security policies


🌐 Discovery Service (Optional / Future)

Currently disabled (not necessary for local development)

Will allow load balancing and dynamic discovery using:

Eureka

Spring Cloud LoadBalancer


📝 Upcoming Microservice: Order Service

A dedicated Order Service will manage the full lifecycle of user orders:

Create orders based on cart and user data

Validate product stock

Integrate with Payment Service

Publish Order Created / Paid / Cancelled Kafka events

It will also expose internal secure endpoints for communication with PaymentService and MyData.

## Patterns used:

Reactive Microservices

Event-Driven Architecture (Kafka)

API Gateway Pattern (future)

Service-to-Service Authentication Pattern

JWT Resource Servers

CQRS-friendly data separation (MongoDB for users, PostgreSQL for catalog)

Domain-Driven service boundaries

## 🔐 Security Architecture

🔑 1. JWT-Based External Security (Public API)
🌟 Full JWT Protection for Clients

All public APIs (login, cart, products, etc.) are secured using JSON Web Tokens (JWT).
Each microservice acts as a Spring Security OAuth2 Resource Server, performing strict token validation.

JWT validation includes:

Issuer ✔

Audience ✔

Expiration ✔

Custom Claims Validators, including:

sub (must be a valid user UUID)

userUuid consistency check

## 🎯 Technologies Used

Backend & Reactive Stack

# Java 17

Spring WebFlux

Spring Security

Spring Boot 3

Spring Data MongoDB (Reactive)

Spring Data R2DBC (PostgreSQL)

Event System

Kafka

Zookeeper

KafkaTemplate / ReactiveKafkaConsumer

DevOps & Infrastructure

Docker

Docker Compose

Multi-container orchestration (Kafka, Mongo, PG, services)

Testing Stack

JUnit 5

Mockito

Integration testing (WebTestClient + Reactive repositories)

🐳 Docker Support

Docker Compose orchestrates:

Kafka + Zookeeper

PostgreSQL

MongoDB

Future: multiple microservices

Future: distributed environment with load balancing

Start the full environment:

docker-compose up -d


Stop everything:

docker-compose down

🚀 Running Locally
1. Clone repository
git clone <repo-url>

2. Start infrastructure
docker-compose up -d

3. Build the platform
mvn clean install

4. Run each microservice

From IntelliJ or via:

mvn spring-boot:run

🧪 Testing

Run full test suite:

mvn test


Tests include:

Unit tests (Mockito + JUnit 5)

Reactive integration tests

WebTestClient-based functional tests

Kafka integration tests (future)

🔮 Future Improvements

⚙️ Order Service: complete purchase flow

⚙️ Saga / Orchestration Pattern for Payments

🧱 API Gateway with rate limiting & routing

🧰 Redis cache layer

☁️ Full deployment in Kubernetes

📡 Observability (Prometheus + Grafana)

🛡️ Distributed tracing (Zipkin/Jaeger)

⭐ Full CI/CD pipeline

## ❤️ Contributions

Contributions, suggestions, and improvements are welcome.
This is a personal learning and professional-grade project — feedback is encouraged.
