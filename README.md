# DebateHub

Full-stack demo for Internet Technologies exam.

- **Backend:** Spring Boot (JWT auth), PostgreSQL
- **Frontend:** Angular 16
- **Domain:** Debates with users; CRUD; pin/unpin; profile; account (change password, delete account)
- **Public repo required:** yes (see instructions below)
- **Database dump:** included under `db/debatehub.sql` (see export/restore commands)

---

## 1) Requirements ✳️ ↔️ Implementation

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

> These match `backend/src/
