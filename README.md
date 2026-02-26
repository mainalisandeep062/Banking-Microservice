# Banking_Microservice

Overview
--------
Banking_Microservice is a small Java Spring Boot-based microservices demo implementing a simplified banking system. It is split into services with clear responsibilities and uses Eureka for discovery, RabbitMQ for async notifications, and MySQL for persistence.

Services
--------
- RegistryServer — Eureka service registry
- ApiGateway — Gateway + JWT guard and routing (function-based routing using Spring Cloud Gateway-style MVC handlers)
- UserService — User management, authentication, JWT issuance
- AccountService — Account creation and balance management
- TransactionService — Transaction orchestration (withdraw/deposit/transfer) and history
- NotificationService — Notification persistence and WebSocket/RabbitMQ handlers

High-level architecture
------------------------
- Clients call `ApiGateway` which validates JWT and routes to services via service discovery (Eureka).
- `TransactionService` orchestrates money-moving operations by calling `AccountService` via Feign clients and records transaction metadata. It publishes notification messages to RabbitMQ.
- `AccountService` maintains account balances, uses pessimistic locking for balance updates, and stores processed transaction IDs to ensure idempotency.
- `NotificationService` consumes RabbitMQ messages and persists notifications and can push them to WebSocket clients.

Prerequisites
-------------
- Java 17 installed
- Docker & Docker Compose (for full-stack run)
- Windows PowerShell (commands below use PowerShell syntax)
- Optional locally: MySQL and RabbitMQ if not using docker-compose

Quick start — build and run locally (per-service)
-------------------------------------------------
You can build and run services individually or use docker-compose to boot the entire system.

Build a single service (example: AccountService) using the included Maven wrapper:

```powershell
.\AccountService\mvnw.cmd -f .\AccountService\pom.xml clean package -DskipTests=true
java -jar .\AccountService\target\app.jar --spring.profiles.active=dev
```

Build all services (from repository root) — run the same command for each service or script it:

```powershell
# Example sequence (adjust for your machine):
.\RegistryServer\mvnw.cmd -f .\RegistryServer\pom.xml clean package -DskipTests=true;
.\UserService\mvnw.cmd -f .\UserService\pom.xml clean package -DskipTests=true;
.\AccountService\mvnw.cmd -f .\AccountService\pom.xml clean package -DskipTests=true;
.\TransactionService\mvnw.cmd -f .\TransactionService\pom.xml clean package -DskipTests=true;
.\NotificationService\mvnw.cmd -f .\NotificationService\pom.xml clean package -DskipTests=true;
.\ApiGateway\mvnw.cmd -f .\ApiGateway\pom.xml clean package -DskipTests=true
```

Run full stack with Docker Compose
---------------------------------
The repository provides a `docker-compose.yml` at the root. It will start Eureka, MySQL, RabbitMQ and each service (if images are configured in the compose file).

```powershell
# From repository root
docker-compose up --build
```

Important environment variables (dev)
------------------------------------
Each service has `application-dev.properties` that reference environment variables. For local development you can set these or rely on the non-production defaults — but be careful: some default values are insecure and meant only for local dev.

Common env vars used by services:
- `EUREKA_URL` — e.g., `http://localhost:8761/eureka/`
- `DB_URL` — JDBC URL for MySQL (overrides service-specific default)
- `DB_USERNAME` / `DB_PASSWORD` — database credentials
- `SECRET_STRING` — JWT secret used across services (must be at least 32 chars for HS256)
- `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_PORT`, `SPRING_RABBITMQ_USERNAME`, `SPRING_RABBITMQ_PASSWORD`

Security note: Do NOT use plaintext production secrets in properties. Use environment variables or a secret manager. The `application-dev.properties` files currently contain local defaults (e.g. `root`/`root`) for convenience but these must be overridden for any public or production deployment.

Health checks and actuators
---------------------------
- Many services expose Spring Boot Actuator endpoints. Check `/actuator/health` and `/actuator/info` on the service ports (see `application-dev.properties` for ports).
- Example: `http://localhost:8080/actuator/health` for the ApiGateway when running with `server.port=8080`.

JWT and authentication
----------------------
- The project uses JWT for auth. The gateway validates tokens before forwarding requests.
- Ensure the same JWT secret is used by the `UserService` (issuer) and all other services (consumers). The environment variable `SECRET_STRING` is referenced in some services; `UserService` uses `app.jwt.secret` in its properties — keep them in sync or update property keys consistently.

Database
--------
- Each service uses its own MySQL schema (configured in `application-dev.properties`). The default JDBC URLs include `createDatabaseIfNotExist=true` for convenience.
- For production use, remove `createDatabaseIfNotExist` and use controlled DB provisioning.

RabbitMQ
--------
- RabbitMQ is used for notifications and sync messages. Ensure RabbitMQ is available and credentials match the env vars.

Troubleshooting
---------------
- If a service fails to start, check logs for missing environment variables (DB or RabbitMQ connection failure) and ensure Eureka is reachable (for clients using discovery).
- If JWT validation fails at the gateway, verify `SECRET_STRING` matches the value used by `UserService` when issuing tokens.
- If inter-service Feign calls are receiving 401s, ensure the Authorization header is forwarded; the code includes a `FeignClientInterceptor` which copies the `Authorization` header from the incoming request to outgoing Feign calls.

Developer notes and next improvements
------------------------------------
- Account balance updates use pessimistic locking and idempotency checks; these are important for correctness when handling concurrent transactions.
- There is no saga/orchestrator for distributed transactions; if strong cross-service consistency is required, implement a saga pattern or compensation logic.
- Consider unifying JWT property names across services (`jwt.secret` vs `app.jwt.secret`) and pinning the JJWT dependency version consistently in all `pom.xml` files.
- Add CI pipeline to run `mvnw.cmd` for all modules and run tests and basic static analysis (spotbugs/checkstyle).

Contact
-------
Owner: Sandeep Mainali

Email: sawondeep4@gmail.com

This project is developed and maintained solely by the owner listed above. If you discover a bug or want to suggest an improvement, please open an issue on the repository. For time-sensitive or private matters, you may send an email to the address above.

Next steps (optional)
---------------------
To request expansions or other changes, please open an issue in the repository or send an email to the project owner (contact above). Common additions people request include:
- A `.env.example` listing recommended environment variables for each service
- A short `CONTRIBUTING.md` with contribution guidelines and how to open issues or submit pull requests
- A simple `start.ps1` script to build and run a minimal local smoke stack (Registry + ApiGateway + one service)

---
This README is a concise summary to help you build, run and understand the Banking_Microservice project locally.
