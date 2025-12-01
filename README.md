# 🛒 ReactivEcommerce – Reactive Ecommerce Microservices Platform

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring WebFlux](https://img.shields.io/badge/Spring-WebFlux-green.svg)](https://spring.io/reactive)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-R2DBC-blue.svg)](https://www.postgresql.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-Reactive-green.svg)](https://www.mongodb.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)](https://maven.apache.org/)
[![JUnit](https://img.shields.io/badge/JUnit-5-green.svg)](https://junit.org/junit5/)
[![Mockito](https://img.shields.io/badge/Mockito-Testing-yellow.svg)](https://site.mockito.org/)
[![Kafka](https://img.shields.io/badge/Apache-Kafka-black.svg)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)

## 📌 Overview

**ReactivEcommerce** is a modern, event-driven ecommerce platform built entirely on **reactive principles** using **Spring WebFlux**. It demonstrates production-ready patterns for building scalable microservices with non-blocking I/O, asynchronous messaging, and robust security.

### ✨ Key Highlights

- ⚡ **Fully reactive** end-to-end with Spring WebFlux and R2DBC
- 🧩 **Microservices architecture** with clear domain boundaries
- 🔐 **Multi-layer security**: JWT for users + API-Key for service-to-service
- 📨 **Event-driven** workflows using Apache Kafka
- 🐳 **Containerized** infrastructure with Docker Compose
- 🧪 **Test coverage** with JUnit 5, Mockito, and WebTestClient

## 🎯 Project Goals

This project aims to showcase:

1. **High-performance reactive systems** capable of handling thousands of concurrent requests
2. **Clean microservices architecture** with proper separation of concerns
3. **Production-grade security** patterns for public and internal APIs
4. **Event-driven design** for loose coupling and scalability
5. **Modern DevOps practices** with containerization and orchestration

## 🛠️ Tech Stack

### Core Technologies

| Technology | Purpose | Version |
|------------|---------|---------|
| ☕ **Java** | Primary language | 17 |
| ⚡ **Spring WebFlux** | Reactive web framework | 3.x |
| 🛢️ **PostgreSQL + R2DBC** | Relational data (reactive) | Latest |
| 🍃 **MongoDB Reactive** | Document storage | Latest |
| 🔐 **Spring Security** | Authentication & Authorization | 6.x |
| 📦 **Apache Kafka** | Event streaming | 3.6+ |
| 🐳 **Docker** | Containerization | Latest |
| 🛠️ **Maven** | Build tool | 3.9+ |

### Testing & Quality

- 🧪 **JUnit 5** - Unit testing framework
- 🎭 **Mockito** - Mocking framework
- 🌐 **WebTestClient** - Reactive integration tests
- ✅ **AssertJ** - Fluent assertions

## 🏗️ Architecture

### High-Level Design
```
┌─────────────┐
│   Clients   │ (Web, Mobile, APIs)
└──────┬──────┘
       │
       ├─────────────────────────────────────────┐
       │                                         │
┌──────▼──────┐  JWT Auth   ┌─────────────────┐ │
│  API Gateway│◄────────────►│  Auth Service   │ │
│     │                     └─────────────────┘ │
└──────┬──────┘                                  │
       │                                         │
       ├──────────┬──────────┬──────────────────┤
       │          │          │                  │
┌──────▼──────┐ ┌▼──────────▼┐ ┌───────────────▼┐
│   Product   │ │   Cart      │ │   User Mgmt    │
│   Catalog   │ │   Service   │ │   Service      │
└──────┬──────┘ └──────┬──────┘ └────────┬───────┘
       │               │                 │
       └───────────────┼─────────────────┘
                       │
                ┌──────▼──────┐
                │    Kafka    │ (Event Bus)
                └─────────────┘
```

### Communication Patterns

- 🌐 **REST APIs**: Synchronous communication between services and clients
- 📨 **Kafka Events**: Asynchronous workflows (login tracking, order processing)
- 🔒 **API-Key Headers**: Secure internal service-to-service calls

### Security Model
```
┌─────────────────────────────────────────────────────┐
│                  Security Layers                    │
├─────────────────────────────────────────────────────┤
│  👥 Public APIs     → JWT Bearer Token             │
│  🔒 Internal APIs   → X-Internal-API-Key Header    │
│  🛡️ Database Access → Encrypted connections        │
└─────────────────────────────────────────────────────┘
```

#### 🔑 External Security (JWT)

- **Purpose**: Authenticate end-users (customers, admins)
- **Validation**: Each microservice validates JWT issuer, audience, expiration, and subject (user UUID)
- **Token contains**: User UUID, roles, expiration timestamp
- **Example header**: `Authorization: Bearer eyJhbGciOiJIUzI1Ni...`

#### 🛰️ Internal Security (API-Key)

- **Purpose**: Secure service-to-service communication
- **Implementation**: Custom `WebFilter` validates `X-Internal-API-Key` header
- **Authority granted**: `INTERNAL_SERVICE` role
- **Protected routes**: `/api/<service>/internal/**`
- **Example header**: `X-Internal-API-Key: X7k9mP2vQ8wR4tY6uI1oP3aS5dF7gH9j`

## 🧩 Microservices

### 👤 User Management Service

**Responsibility**: Core user data and profile management

- **Database**: MongoDB (flexible user schema)
- **Key features**:
  - User CRUD operations
  - Account status management (`isActive`, `isLocked`)
  - `latestAccess` tracking via Kafka events
  - Cart/order relationship management
- **Public endpoints**: User registration, profile updates (JWT protected)
- **Internal endpoints**: `/internal/updateUserHasCart`, `/internal/getUserByUuid` (API-Key protected)

### 🔑 User Authentication Service

**Responsibility**: Login and JWT token generation

- **Database**: None (stateless, queries User Management)
- **Key features**:
  - Username/email + password validation
  - JWT token generation with custom claims
  - Publishes `UserLoginEvent` to Kafka
  - Bcrypt password hashing
- **Public endpoints**: `/auth/login`, `/auth/register`
- **Kafka events**: `user.login.events` topic

### 📦 Product Catalog Service

**Responsibility**: Product inventory and metadata

- **Database**: PostgreSQL with R2DBC (relational product data)
- **Key features**:
  - Product CRUD (name, description, price, stock)
  - Category management
  - Stock validation for orders
  - Reactive queries with R2DBC
- **Public endpoints**: `/products` (GET all, GET by ID - JWT protected)
- **Admin endpoints**: `/products` (POST, PUT, DELETE - Admin role required)

### 🧺 Shopping Cart Service

**Responsibility**: User cart and item management

- **Database**: MongoDB (nested cart items structure)
- **Key features**:
  - Add/remove/update cart items
  - Cart validation before checkout
  - Integration with Product Catalog (stock checks)
  - Links cart to user via User Management
- **Public endpoints**: `/carts` (JWT protected, user-scoped)
- **Internal communication**: Calls Product Catalog and User Management

### 📊 MyData Service

**Responsibility**: User-related transactional and aggregated data

- **Database**: MongoDB (user-centric documents)
- **Key features**:
  - Order history aggregation
  - User statistics (total spent, order count)
  - Wishlist management
  - Creates cart records linked to users
- **Public endpoints**: `/mydata/stats` (JWT protected)
- **Internal endpoints**: Calls User Management via API-Key

### 💳 Payment Service

**Responsibility**: Payment processing workflows

- **Database**: PostgreSQL (payment transactions)
- **Key features**:
  - Payment intent creation
  - Transaction status tracking
  - Future integrations: Stripe, PayPal
  - Idempotency for duplicate prevention
- **Public endpoints**: `/payments/process` (JWT protected)
- **Kafka events**: `payment.completed`, `payment.failed`

### 🧾 Order Service *(Planned)*

**Responsibility**: Order lifecycle orchestration

- **Database**: PostgreSQL (order records, line items)
- **Key features**:
  - Order creation from cart
  - Stock reservation (saga pattern)
  - Payment coordination
  - Order status management (PENDING, PAID, SHIPPED, DELIVERED)
- **Kafka events**: `order.created`, `order.paid`, `order.shipped`
- **Saga orchestration**: Handles distributed transactions across Product, Payment, and User services

### 🌐 API Gateway *(Planned)*

**Responsibility**: Single entry point for all clients

- **Technology**: Spring Cloud Gateway
- **Key features**:
  - Request routing to microservices
  - Rate limiting and throttling
  - Global CORS configuration
  - Centralized JWT validation
  - Request/response logging

### 🔍 Service Discovery *(Optional)*

**Responsibility**: Dynamic service registration and discovery

- **Technology**: Eureka or Consul
- **Key features**:
  - Automatic service registration
  - Client-side load balancing
  - Health checks and failover

## 📨 Event-Driven Workflows

### Kafka Topics

| Topic | Producer | Consumer | Purpose |
|-------|----------|----------|---------|
| `user.login.events` | Auth Service | User Management | Update `latestAccess` and `lastLoginIp` |
| `order.created` | Order Service | Payment, Inventory | Trigger payment and reserve stock |
| `payment.completed` | Payment Service | Order Service | Update order status to PAID |
| `order.shipped` | Order Service | User Management | Send notification to user |

### Example: Login Flow
```
1. User sends credentials → Auth Service
2. Auth validates password → Queries User Management
3. Auth generates JWT token
4. Auth publishes UserLoginEvent → Kafka
5. User Management consumes event → Updates latestAccess
6. Auth returns JWT to user
```

## 🐳 Infrastructure

### Docker Compose Services
```yaml
services:
  zookeeper:     # Kafka dependency
  kafka:         # Event streaming
  mongodb:       # User Management, Cart, MyData
  postgres:      # Product Catalog, Payment, Orders
  # Microservices containers (future)
```

### Running the Platform
```bash
# Start infrastructure
docker-compose up -d

# Verify services
docker ps

# View Kafka topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Start microservices (in separate terminals)
cd user-management-service && mvn spring-boot:run
cd user-auth-service && mvn spring-boot:run
cd product-catalog-service && mvn spring-boot:run
# ... etc
```

## 🚀 Getting Started

### Prerequisites

- ☕ **Java 17+** installed
- 🐳 **Docker** and **Docker Compose** installed
- 🛠️ **Maven 3.9+** installed
- 📝 **IDE** (IntelliJ IDEA recommended)

### Quick Start
```bash
# 1. Clone the repository
git clone https://github.com/yourusername/reactive-ecommerce.git
cd reactive-ecommerce

# 2. Start infrastructure
docker-compose up -d

# 3. Build all services
mvn clean install

# 4. Run User Management Service
cd user-management-service
mvn spring-boot:run

# 5. Run User Auth Service (new terminal)
cd user-auth-service
mvn spring-boot:run

# 6. Test login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run tests for specific service
cd user-management-service && mvn test

# Run integration tests only
mvn test -Dgroups=integration

# Generate coverage report
mvn jacoco:report
```

### Test Structure
```
src/test/java/
├── unit/              # Unit tests with Mockito
├── integration/       # Integration tests with TestContainers
└── e2e/              # End-to-end API tests
```
## 🔮 Roadmap

### Phase 1: Core Services ✅
- [x] User Management
- [x] User Authentication
- [x] Product Catalog
- [x] Shopping Cart
- [x] Kafka integration
- [x] JWT security
- [x] API-Key internal auth

### Phase 2: Order & Payment 🚧
- [ ] Order Service implementation
- [ ] Payment Service integration
- [ ] Saga pattern for distributed transactions
- [ ] Stock reservation logic

### Phase 3: Gateway & Discovery 📋
- [ ] API Gateway with Spring Cloud Gateway
- [ ] Service Discovery (Eureka)
- [ ] Circuit breaker pattern (Resilience4j)

### Phase 4: Advanced Features 📋
- [ ] Redis caching layer
- [ ] Elasticsearch for product search
- [ ] Admin dashboard
- [ ] Email notifications

### Phase 5: Production Ready 📋
- [ ] Kubernetes deployment manifests
- [ ] Helm charts
- [ ] CI/CD pipelines (GitHub Actions)
- [ ] Monitoring stack (Prometheus + Grafana)
- [ ] Performance testing (Gatling)

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. 🍴 Fork the repository
2. 🌿 Create a feature branch (`git checkout -b feature/amazing-feature`)
3. 💾 Commit your changes (`git commit -m 'Add amazing feature'`)
4. 📤 Push to the branch (`git push origin feature/amazing-feature`)
5. 🔀 Open a Pull Request

### Contribution Guidelines

- ✅ Write tests for new features
- 📝 Update documentation
- 🎨 Follow code style conventions
- 🔍 Ensure all tests pass before submitting PR

---

⭐ **Star this repository** if you find it helpful!

📧 **Questions?** Open an issue or reach out!
