# DebateHub Backend Setup

This README explains how to set up and run the DebateHub backend, including database preparation with PostgreSQL and restoring from the provided dump.

---

## 1. Prerequisites

- Java 17+
- Maven (or use `./mvnw` included)
- PostgreSQL 15+
- Git (for cloning project if needed)
- PowerShell or bash shell for commands

---

## 2. Database Setup

### Create Database & User

```sql
-- As postgres superuser
CREATE DATABASE debatehub;
CREATE USER debate_user WITH ENCRYPTED PASSWORD 'ChangeMe_Local_Only_123';
GRANT ALL PRIVILEGES ON DATABASE debatehub TO debate_user;
```

### Load Schema and Data

From the `DebateHub/db` folder of this project, run:

```bash
psql -U postgres -h localhost -d debatehub -f debatehub.dump
```

This will restore all tables, constraints, and seed data needed for testing.

---

## 3. Test Accounts

The database dump already includes some users you can use for login tests:

| Email              | Password     | Notes       |
|--------------------|-------------|-------------|
| demo@example.com   | (set via encoder) | Demo account |
| host@example.com   | (set via encoder) | Host test    |
| guest@example.com  | (set via encoder) | Guest test   |

⚠️ Passwords are stored hashed with BCrypt. To create new accounts, use the `/api/auth/register` endpoint.

---

## 4. Running the Backend

```bash
cd backend
./mvnw spring-boot:run
```

The backend will start at:

```
http://localhost:8080
```

---

## 5. Health Check

Verify DB connection:

```bash
curl http://localhost:8080/api/health/db
```

Response should be:

```json
{"ok": true}
```

---

## 6. API Quick Tests

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json"   -d '{"email":"test@example.com","displayName":"Tester","password":"Password123!"}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json"   -d '{"email":"test@example.com","password":"Password123!"}'
```

### Create Debate

```bash
curl -X POST http://localhost:8080/api/debates -H "Content-Type: application/json"   -H "Authorization: Bearer <token>"   -d '{"title":"My Debate","description":"Testing","inviteOnly":false}'
```

---

## 7. Restore on Another Machine

To replicate environment elsewhere:

1. Clone repository
2. Install prerequisites (Java, Maven, PostgreSQL)
3. Create DB + user (see step 2)
4. Run dump restore command
5. Start backend with `./mvnw spring-boot:run`
6. Use the API normally

---

## 8. Notes

- JWT secrets and other app configs are in `application.properties`.
- Database credentials can be overridden with env vars:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/debatehub
SPRING_DATASOURCE_USERNAME=debate_user
SPRING_DATASOURCE_PASSWORD=ChangeMe_Local_Only_123
```

