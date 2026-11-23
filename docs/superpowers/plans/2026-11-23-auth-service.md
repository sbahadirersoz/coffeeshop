# auth-service (Step 1) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** An admin creates staff users; staff log in and get an RS256 JWT that other services can verify through a JWKS endpoint.

**Architecture:** Hexagonal: `domain` (User aggregate, value objects, policies), `application` (use cases, ports), `infrastructure` (JPA adapter, BCrypt, JWT, web). Flyway owns the schema. Tests: plain JUnit for domain and use cases, Testcontainers for persistence and HTTP.

**Tech Stack:** Java 25, Spring Boot 4.1.0, Spring Security (oauth2 resource server + jose), Spring Data JPA, Flyway, PostgreSQL 17, Testcontainers, JUnit 5, Mockito, AssertJ.

**Spec:** `docs/superpowers/specs/2026-11-23-coffeeshop-design.md`

**Work split:** Claude writes the red tests (Steps 1-2 of each task). The owner writes the production code and runs `.http` checks. Commits have no AI attribution lines.

## Global Constraints
- Java 25. In a terminal set `JAVA_HOME=C:/Users/smhbh/.jdks/ms-25.0.4.1` first (system `java` is 24).
- Run tests from `coffeeshop-root`: `./mvnw -pl auth-service -am test`.
- Package root: `com.portfolioproject.authservice` with lowercase `domain`, `application`, `infrastructure`.
- Username: trimmed, lowercase, 6-12 characters. Password: 8-64 characters (BCrypt ignores bytes after 72).
- Token: RS256, issuer `coffeeshop-auth`, lifetime 900 seconds, claims `sub` (user id), `username`, `permissions` (list of permission strings). No refresh token.
- Database `coffeeshop_auth` (snake_case), `ddl-auto: validate`, schema only through Flyway.
- Errors leave the service as ProblemDetails: invalid input 400, bad credentials 401, missing permission 403, username taken 409.
- `common-shared` keeps only generic code: `Result`, `AuthErrors` (type only), `AggregateRoot`, `AggregateID`, `IDomainEvent`, `PrimitiveGuard`.

## Review Focus
1. Unknown username and wrong password give the same 401 body (no user enumeration). Test in Task 4.
2. `Waiter01` and `waiter01` are the same user: creating both gives 409, login with capitals works. Tests in Tasks 3 and 4.
3. A 65-character password is rejected before hashing. Test in Task 1.
4. A token signed by another key gets 401 on `/users`. Test in Task 5.
5. Restarting the service does not create a second admin or crash on the unique index. Test in Task 5.

---

### Task 0: Clean up and first commit (owner, IntelliJ refactor tools)

**Files:** `auth-service/src/main/java`, `common-shared`, `docker-compose.yaml`, `.env`, root `pom.xml`, `CoffeeShop-Project/Root/`.

- [ ] Delete the empty `CoffeeShop-Project/Root` folder.
- [ ] Move `.../authservice/com/portfolioproject/authservice/*` up to `.../authservice/` (Refactor > Move). Rename packages to lowercase: `Domain` -> `domain`, `AppLayer` -> `application`, `Infrastructure` -> `infrastructure`, subpackages likewise.
- [x] Rename `UserJpaAggregate` -> `UserAggregate` (done by the owner).
- [ ] Move `AppPermissions`, `Permission`, `PasswordConstraints`, `UsernameConstraints` and the auth constants of `AuthErrors` into `auth-service` (`domain.security`, `domain.errors.AuthErrors`). Fix the copy-paste: `*_UNDER_MIN_VALUE` must not say `OVER_MAX`. Keep `ErrorDetails(String desc, String code)` and `NONE` in common-shared.
- [ ] `PasswordConstraints.MaxPassLen = 64`.
- [ ] Delete `PasswordManager`, `PasswordManagerImpl`, `AuthenticationService`, `ValueObjectErrors`, `UserEmailAdressChangedEvent`; remove `email` from the command.
- [ ] `RegisterUserUseCase`: make it compile with `return Result.success();` (Task 3 replaces it).
- [ ] Root `pom.xml`: remove the trailing space in `${starter.version} `.
- [ ] Compose: database `coffeeshop_auth`, user and password from `.env`. Fix `.env`: no spaces around keys, no duplicate `AUTH_DB_PORT`.
- [ ] Verify: `./mvnw -q -DskipTests package` succeeds.
- [ ] Create the GitHub repo, commit `chore: restructure auth-service packages and shared module`, push.

### Task 1: User aggregate and value objects

**Files:** Modify `domain/aggregates/UserAggregate.java`, `domain/valueobjects/user/credentials/Username.java`, `domain/service/RawPasswordPolicy.java`. Test: `src/test/java/com/portfolioproject/authservice/domain/UserAggregateTest.java`, `UsernameTest.java`, `RawPasswordPolicyTest.java`.

**Interfaces (produces):**
- `static UserAggregate create(Username username, Password password, RoleType role)`: status ACTIVE, roles = {role}, new id, records `UserCreatedEvent(UserID id, Username username)` (rename of `NewUserAggregateCreatedEvent`).
- `boolean hasPermission(Permission p)`, `Set<String> permissionNames()` (used by the token issuer).

- [ ] **Step 1: Red tests**
```java
// UserAggregateTest
create_isActive_hasOnlyGivenRole()          // status ACTIVE, roles == Set.of(WAITER), id != null
create_recordsUserCreatedEvent()             // one UserCreatedEvent with same id and username
waiter_canCreateOrder_cannotManageUsers()    // ORDER_CREATE true, ADMIN_MANAGE_USERS false
permissionNames_ofWaiter()                   // Set.of("order:create", "order:cancel", "kitchen:view")
// UsernameTest
create_trimsAndLowercases()                  // " Waiter01 " -> "waiter01"
create_5chars_fails(), create_13chars_fails(), create_null_fails()   // AuthErrors.USERNAME_* codes
// RawPasswordPolicyTest
validate(7 fails, 8 ok, 64 ok, 65 fails)     // parameterized
```
- [ ] **Step 2:** `./mvnw -pl auth-service -am test` -> red (missing names / failures).
- [ ] **Step 3 (owner):** implement the interfaces above; `create` calls `addEvent`.
- [ ] **Step 4:** green.
- [ ] **Step 5:** commit `feat(auth): user aggregate records creation event and exposes permissions`.

### Task 2: Flyway schema and JPA adapter against real Postgres

**Files:** `auth-service/pom.xml` (add `spring-boot-starter-flyway`, `flyway-database-postgresql`, `spring-boot-testcontainers`, Testcontainers PostgreSQL + JUnit Jupiter; check the exact Boot 4.1 artifact names on start.spring.io), `src/main/resources/db/migration/V1__create_auth_users.sql`, `application.yml` (`ddl-auto: validate`, db `coffeeshop_auth`). Test: `src/test/java/.../TestcontainersConfig.java` (`@ServiceConnection` `PostgreSQLContainer` with `postgres:17`), `infrastructure/UserRepositoryAdapterIT.java`; `AuthServiceApplicationTests` imports `TestcontainersConfig`.

**Interfaces (produces):** `SecurityUserRepository` gains `boolean existsByRole(RoleType role)`.

Schema: `auth_users(id uuid pk, username varchar(12) unique not null, password_hash varchar(100) not null, status varchar(20) not null, version bigint not null)`, `auth_user_roles(user_id uuid references auth_users on delete cascade, role_type varchar(30) not null, primary key (user_id, role_type))`.

- [ ] **Step 1: Red tests**
```java
save_thenFindByUsername_returnsSameRolesAndStatus()
existsByUsername_trueAfterSave_falseForOther()
existsByRole_falseOnEmptyDb_trueAfterAdminSaved()
```
- [ ] **Step 2:** red (Docker Desktop must be running). Hibernate `ddl-auto: update` already created the tables, so run `docker compose down -v` once before the first Flyway run.
- [ ] **Step 3 (owner):** migration, yml, `existsByRole`.
- [ ] **Step 4:** green, including `contextLoads` (proves `validate` matches the migration).
- [ ] **Step 5:** commit `feat(auth): flyway schema and repository tests on postgres`.

### Task 3: CreateStaffUser use case

**Files:** rename `RegisterUserUseCase` -> `application/usecases/CreateStaffUserUseCase.java`; command `CreateStaffUserCommand(String username, String rawPassword, RoleType role)`. Port `application/ports/PasswordHasher` gets `boolean matches(String raw, String hash)`; `PasswordHasherImpl` uses the `PasswordEncoder` bean. Test: `application/CreateStaffUserUseCaseTest.java` (Mockito repo, fake hasher returning `"hashed:" + raw`).

**Interfaces (produces):** `Result<UUID> execute(CreateStaffUserCommand cmd)`; error `AuthErrors.ROLE_IS_REQUIRED` (code `Auth.Role.Required`).

- [ ] **Step 1: Red tests**
```java
valid_savesUserWithHashedPassword_returnsId()        // saved hash == "hashed:secret123", never the raw string
takenUsername_returnsUsernameExists_savesNothing()
takenUsername_differentCase_returnsUsernameExists()  // " WAITER01" vs stored "waiter01"
shortPassword_returnsPasswordTooShort_savesNothing()
nullRole_returnsRoleRequired_savesNothing()
```
- [ ] **Step 2:** red. **Step 3 (owner):** implement with Spring's `@Transactional`. **Step 4:** green.
- [ ] **Step 5:** commit `feat(auth): admin use case to create staff users`.

### Task 4: Login use case and JWT issuer

**Files:** `application/usecases/LoginUseCase.java`, `LoginCommand(String username, String rawPassword)`, `LoginResponse(String accessToken, long expiresInSeconds)`; port `application/ports/TokenIssuer { String issue(UserAggregate user); }`; `infrastructure/security/JwtTokenIssuer.java` (`NimbusJwtEncoder`), `infrastructure/security/RsaKeyConfig.java` (RSA 2048 key pair generated at start-up, `KeyPair` bean). Tests: `application/LoginUseCaseTest.java`, `infrastructure/JwtTokenIssuerTest.java`.

Shortcut comment for `RsaKeyConfig`: the key is regenerated on every start, so restarts log everyone out; load it from a secret before deploying.

**Interfaces (produces):** `Result<LoginResponse> execute(LoginCommand cmd)`; error `AuthErrors.BAD_CREDENTIALS` (code `Auth.Credentials.Bad`); `KeyPair` bean.

- [ ] **Step 1: Red tests**
```java
// LoginUseCaseTest
correctPassword_returnsToken_expires900()
wrongPassword_returnsBadCredentials()
unknownUser_returnsSameBadCredentials()     // error equals the wrong-password error
inactiveUser_returnsBadCredentials()
usernameWithCapitals_logsIn()
// JwtTokenIssuerTest (decode with NimbusJwtDecoder.withPublicKey)
token_hasSubUsernamePermissionsIssuer()
token_expiresAt_issuedAtPlus900()
```
- [ ] **Step 2:** red. **Step 3 (owner):** implement. **Step 4:** green.
- [ ] **Step 5:** commit `feat(auth): login issues RS256 JWT`.

### Task 5: HTTP layer, security, JWKS, admin seed

**Files:** `infrastructure/web/AuthController.java`, `infrastructure/web/JwksController.java`, `infrastructure/web/ErrorMapping.java` (error code -> ProblemDetails status), rewrite `infrastructure/config/SecurityConfig.java`, `infrastructure/AdminSeeder.java` (`ApplicationRunner`, reads `ADMIN_USERNAME`, `ADMIN_PASSWORD`). Delete `UserDetailsServiceImpl`, `securityUser`, `SecurityUserMapper`: login goes through `LoginUseCase`, so Spring's `UserDetailsService` is not used. Test: `infrastructure/web/AuthApiIT.java` (`@SpringBootTest` + MockMvc + `TestcontainersConfig`, admin credentials as test properties).

Routes: `POST /api/v1/auth/login` (public) -> 200 `LoginResponse`; `POST /api/v1/auth/users` (`hasAuthority("admin:manage-users")`) -> 201 `{ "id": ... }`; `GET /.well-known/jwks.json` (public). The resource server reads authorities from the `permissions` claim with no prefix.

- [ ] **Step 1: Red tests**
```java
seededAdmin_canLogin()
seeder_runTwice_keepsOneAdmin()
wrongPassword_401_problemDetails()
admin_createsWaiter_201_thenWaiterLogsIn_200()
waiterToken_onUsers_403()
noToken_onUsers_401()
tokenFromOtherKey_onUsers_401()
duplicateUsername_409()
invalidUsername_400_withCode()
jwks_returnsOneRsaKey()
```
- [ ] **Step 2:** red. **Step 3 (owner):** implement. **Step 4:** green.
- [ ] **Step 5:** commit `feat(auth): login, staff creation and JWKS endpoints`.

### Task 6: Run it for real, CI

**Files:** `auth-service/auth.http`, `.github/workflows/ci.yml`, short README (how to run, endpoints).

- [ ] Claude writes `auth.http` with asserts: admin login (store token), create waiter 201, duplicate 409, waiter login 200, waiter on `/users` 403, wrong password 401, jwks 200.
- [ ] Owner starts the service against `docker compose up -d` and runs `auth.http`; all pass.
- [ ] CI: JDK 25 (temurin), `./mvnw -B verify` on pull requests.
- [ ] One review, then PR `feat/auth-service` -> master. Spec Step 1 is done.
