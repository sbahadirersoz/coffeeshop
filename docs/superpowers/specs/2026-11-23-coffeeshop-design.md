# Coffee Shop Microservices: Design

Date: 2026-11-23. Status: draft, waiting for approval.

## Why
Portfolio project for job applications. Microservices appear in many job ads, and the owner wants to learn them by building them, not only read about them. The project must be explainable in an interview: why each service exists, how services trust each other, what happens when a message is lost.

## What it is
A staff app for a coffee shop. Waiters take orders, the kitchen prepares them, an admin manages staff. Users are staff only (no customers). Roles and permissions already exist: `WAITER`, `KITCHEN_STAFF`, `ADMIN`.

Order life cycle: `CREATED -> IN_KITCHEN -> READY -> SERVED`, or `CANCELLED` before the kitchen starts.

## Goals
- Every step ends with a running system (`docker compose up`) and something to show.
- Each service owns its own database. No service reads another service's tables.
- Services trust each other through JWT, not through calls to auth on every request.
- One asynchronous flow (order -> kitchen -> order) over a message broker.
- Tests: domain unit tests and one Testcontainers test per service. CI on GitHub Actions.

## Non-goals (for now)
Eureka / service discovery (Compose DNS is enough), Config Server, Kubernetes, customer accounts, payments, a menu service, React frontend (comes after step 4).

## Anti-goals
- Weeks of infrastructure with no working order. If a step runs longer than a week, cut its scope.
- A shared module that knows the business. `common-shared` holds only generic code.

## Approaches considered
- **A. Build everything first** (all services, gateway, Eureka, Config Server, Kafka). Rejected: weeks before the first working order.
- **B. Grow one service at a time** (chosen). Each step adds one service and one new idea, and the system runs at the end of each step.
- **C. Modular monolith first, split later.** Rejected: the goal is to learn microservices, and C reaches them last.

## Steps

### 1. auth-service
- No public sign-up (owner's decision). The existing register use case moves behind an admin endpoint: `POST /api/v1/auth/users` (permission `admin:manage-users`) creates a staff user with a role.
- The first admin is created at start-up from environment variables, only if no admin exists yet.
- `POST /api/v1/auth/login` returns a JWT with user id, username and permissions.
- Tokens signed with RS256. auth-service publishes its public key at `/.well-known/jwks.json`.
- Flyway migrations, `ddl-auto: validate`.
- Done when: admin creates a waiter, the waiter logs in (`.http` checks), a waiter token gets 403 on `/users`, wrong password is 401, a Testcontainers test runs login against real Postgres.

### 2. order-service
- Its own database. Menu items are a simple table inside order-service.
- Validates JWTs itself with Spring's OAuth2 resource server and auth's JWKS URL.
- `POST /orders` (permission `order:create`), `POST /orders/{id}/cancel` (`order:cancel`), `GET /orders/{id}`.
- Done when: a waiter's token can create an order, a kitchen token gets 403, no token gets 401.

### 3. API gateway
- Spring Cloud Gateway, one entry point on port 8080, routes `/api/v1/auth/**` and `/api/v1/orders/**`.
- Risk: Spring Cloud must support Spring Boot 4.1. Check this first; if it does not, move all services to the newest Boot version that Spring Cloud supports.

### 4. kitchen-service + RabbitMQ
- order-service publishes `OrderCreated`. kitchen-service stores a kitchen ticket.
- Kitchen staff (`kitchen:update-status`) mark a ticket ready. kitchen-service publishes `OrderReady`. order-service moves the order to `READY`.
- First version publishes after commit. Known limit: if the service crashes between commit and publish, the message is lost. Step 5 fixes this with an outbox table.

### 5. Later
Outbox pattern, React + TypeScript frontend, deploy, OpenAPI docs per service.

## Changes to the current code
- Fix the doubled folder path (`authservice/com/portfolioproject/authservice`) and use lowercase packages (`domain`, `application`, `infrastructure`).
- Rename `UserAggregate` to `User`. `create` must call `addEvent`.
- One password port with `hash` and `matches`. Remove the overlap between `PasswordHasher`, `PasswordManager` and the encoder bean.
- Move `AppPermissions` and auth constraints out of `common-shared` (permissions live in auth; other services read permission strings from the token).
- Spring's `@Transactional`, not `jakarta.transaction.Transactional`.
- Compose reads `.env`; one Postgres container with one database per service, snake_case names.
- Delete the empty `Root/` folder. First commit and push to GitHub before step 1 code.

## How we work
I write red tests, the owner writes the code. `.http` checks are run by the owner. One review before each PR.

## Decided by Claude (owner can change)
RS256 + JWKS instead of a shared secret (closer to real systems, and Spring supports it out of the box); RabbitMQ instead of Kafka (simpler to run, enough for one flow); one Postgres container with separate databases (less memory, same ownership rule); menu inside order-service.

## Left to the builder
Exact table columns, error codes, DTO shapes, token lifetime (suggest 15 minutes, no refresh token in step 1).
