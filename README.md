# SAN3A Backend (Spring Boot + PostgreSQL)

Production-style starter for SAN3A graduation project backend.

## Tech Stack
- Java 21
- Spring Boot 3.4.4
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Bruno API (`/swagger-ui/index.html`)

## Project Structure
- `auth`: register/login + token issuance
- `request`: service request lifecycle
- `catalog`: services catalog endpoints
- `payment`: payment lifecycle (request 1:1 payment)
- `review`: rating & review lifecycle (request 1:1 review)
- `security`: JWT filter + security config
- `common`: response wrapper + global exception handling
- `domain`: entities and enums

## Run
1. Create database:
   - DB name: `san3a_db`
   - Or use Docker:
```bash
docker compose up -d
```
2. Set env vars (optional, defaults exist in `application.yml`):
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET`
3. Start app:
```bash
./mvnw spring-boot:run
```

Windows:
```powershell
.\mvnw.cmd spring-boot:run
```

## Default Admin
- Email: `admin@san3a.com`
- Password hash is seeded by Flyway migration in `V1__init_schema.sql`
- You can replace it with your own BCrypt hash.

## Main Endpoints

### Auth
- `POST /api/v1/auth/register/user`
- `POST /api/v1/auth/register/tasker`
- `POST /api/v1/auth/login`

### Services
- `GET /api/v1/services`
- `POST /api/v1/services` (ADMIN only)

### Requests
- `POST /api/v1/requests/user` (USER)
- `GET /api/v1/requests/user/me` (USER)
- `GET /api/v1/requests/tasker/me` (TASKER)
- `PATCH /api/v1/requests/tasker/{requestId}/accept` (TASKER)
- `PATCH /api/v1/requests/tasker/{requestId}/status` (TASKER)

### Payments
- `POST /api/v1/payments` (USER, own DONE request only)
- `GET /api/v1/payments/request/{requestId}` (USER owner / TASKER assignee / ADMIN)
- `GET /api/v1/payments/user/me` (USER)
- `GET /api/v1/payments/tasker/me` (TASKER)

### Reviews
- `POST /api/v1/reviews` (USER, own DONE request only)
- `GET /api/v1/reviews/request/{requestId}` (USER owner / TASKER assignee / ADMIN)
- `GET /api/v1/reviews/user/me` (USER)
- `GET /api/v1/reviews/tasker/me` (TASKER)
- `GET /api/v1/reviews/tasker/{taskerId}` (review summary + average)

## Request Status Flow
`PENDING -> ACCEPTED -> IN_PROGRESS -> DONE`

At each active stage, transition to `CANCELED` is allowed.

## Business Rules Added
- Payment can be created only once per request.
- Payment is allowed only when request status is `DONE`.
- Review can be created only once per request.
- Review is allowed only when request status is `DONE`.
- Tasker `rating_avg` is updated automatically after each review.
