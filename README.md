# DebateHub

Full-stack demo for Internet Technologies exam.

- **Backend:** Spring Boot (JWT auth), PostgreSQL
- **Frontend:** Angular 16
- **Domain:** Debates with users; CRUD; pin/unpin; profile; account (change password, delete account)
- **Public repo required:** yes (see instructions below)
- **Database dump:** included under `db/debatehub.sql` (see export/restore commands)

---

## 1) Requirements - Implementation

- Relational DB with **at least 2 related tables** ➜ `users`, `debates` (+ participants relation)
- Backend framework ➜ **Spring Boot**
- CRUD operations ➜ **Debates** (create/read/update/delete), **Account** (change password, delete)
- Frontend ➜ **Angular 16**
- No external APIs required ➜ none used
- Public repository ➜ instructions below
- DB dump in repo ➜ `db/debatehub.sql`

---

## 2) Tech stack & ports

- Spring Boot on **`http://localhost:8080`**
- Angular on **`http://localhost:4200`**
- API base: **`/api`**

**PostgreSQL (local):**
- DB: `debatehub`
- User: `debate_user`
- Password env var: `DB_PASSWORD` (fallback default is `ChangeMe_Local_Only_123`)
- JDBC: `jdbc:postgresql://localhost:5432/debatehub`

> These match `backend/src/main/resources/application.yml`.

---

## 3) Local setup


### 3.1 Data (PostgreSQL)
Create DB & user (psql):

```sql
CREATE ROLE debate_user WITH LOGIN PASSWORD 'ChangeMe_Local_Only_123';
CREATE DATABASE debatehub OWNER debate_user;
GRANT ALL PRIVILEGES ON DATABASE debatehub TO debate_user;
```


### 3.2 Backend (Spring)
From terminal
```Backend

# from project root
cd backend

# run Spring Boot
# Windows:
.\mvnw.cmd spring-boot:run
# mac/linux:
./mvnw spring-boot:run
```

### 3.2 Frontend (Angular)
From terminal
```Frontend

# from project root
cd "frontend/debate-frontend"
npm install
ng serve
