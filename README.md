# 🔐 SafeBox — Secure Secrets Management API

> A REST API for storing and managing digital secrets (passwords, API keys, access tokens) with strong, modern cryptography and a security-by-design mindset.

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![Security](https://img.shields.io/badge/Crypto-AES--256--GCM%20%7C%20BCrypt-C05B6B)
![CI](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF)

SafeBox is an end-of-year project (PFA) built to demonstrate a **DevSecOps** approach: security is designed in from the start, not bolted on at the end. It exposes a small, focused REST API where each user can store secrets that are **encrypted at rest** and **accessible only to their owner**.

---

## ✨ Features

- **User authentication** with stateless **JWT** (HMAC-SHA256, 24 h validity)
- **Password hashing** with **BCrypt** (irreversible, salted)
- **Secret encryption** with **AES-256-GCM** (authenticated encryption, random IV per operation)
- **Ownership-based access control** — a user can never read another user's secrets (`403 Forbidden`)
- **Defense in depth** — secret IDs are random **UUIDs** to prevent IDOR enumeration
- **Full CRUD** for secrets, with input validation and a global exception handler
- **Interactive API docs** via **Swagger / OpenAPI**
- **Containerized** with a multi-stage **Dockerfile** + **Docker Compose**
- **CI/CD pipeline** on **GitHub Actions** with **OWASP Dependency-Check**

---

## 🏗️ Architecture

A classic layered Spring Boot architecture with a transverse security layer:

```
Client (Postman / Swagger)
        │  HTTP (Bearer JWT)
        ▼
┌─────────────────────────────────────────────┐
│              Spring Boot (stateless)          │
│  Controller ──► Service ──► Repository        │
│                                               │
│  Security:  JWT Filter · BCrypt ·             │
│             AES-256-GCM · Ownership check     │
└─────────────────────────────────────────────┘
        │  JPA
        ▼
   PostgreSQL  (secrets stored encrypted)
```

**Data model**

| Entity | Key fields |
|--------|-----------|
| `User` | `id (Long)`, `email (unique)`, `username`, `password (BCrypt)`, `role` |
| `Secret` | `id (UUID)`, `label`, `description`, `value (AES-256-GCM)`, `owner`, `createdAt` |
| `Role` (enum) | `USER`, `ADMIN` |

A `User` owns 0..* `Secret`; each `Secret` belongs to exactly one `User`.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 25 (LTS) |
| Framework | Spring Boot 4.1 |
| Security | Spring Security 6 · jjwt 0.12 |
| Cryptography | BCrypt (passwords) · AES-256-GCM (secrets) |
| Persistence | Spring Data JPA · Hibernate |
| Database | PostgreSQL 17 |
| API docs | springdoc-openapi (Swagger UI) |
| Containers | Docker · Docker Compose |
| CI/CD | GitHub Actions · OWASP Dependency-Check |

---

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose

### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/safebox.git
cd safebox/demo
```

### 2. Configure environment variables
Copy the template and fill in your own values:
```bash
cp .env.example .env
```

Generate strong keys:
```bash
# JWT signing key (Base64)
openssl rand -base64 32
# AES-256 key (Base64, 32 bytes = 256 bits)
openssl rand -base64 32
```

Your `.env` should contain:
```env
DB_PASSWORD=your_db_password
JWT_SECRET=your_base64_jwt_key
AES_SECRET=your_base64_aes_key
```

> ⚠️ `.env` is git-ignored and must **never** be committed. Only `.env.example` is versioned.

### 3. Run
```bash
docker compose up -d --build
```

The API is now available at:
- **API base:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

---

## 📡 API Endpoints

### Authentication (public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Create a new account |
| `POST` | `/api/auth/login` | Authenticate and receive a JWT |

### Secrets (JWT required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/secrets` | Create a secret |
| `GET` | `/api/secrets` | List the current user's secrets |
| `GET` | `/api/secrets/{id}` | Get one secret by UUID |
| `PUT` | `/api/secrets/{id}` | Update a secret |
| `DELETE` | `/api/secrets/{id}` | Delete a secret |

**Authentication flow:** call `/api/auth/login`, copy the returned token, and send it on protected routes as:
```
Authorization: Bearer <token>
```

---

## 🔒 Security Highlights

- **Passwords** are hashed with BCrypt — never stored or logged in plaintext.
- **Secrets** are encrypted with AES-256-GCM before persistence; a fresh random IV is used for every encryption, and the 128-bit GCM tag guarantees integrity.
- **Login** returns the same generic error for a wrong password or an unknown email, preventing account enumeration.
- **Every** secret operation verifies ownership server-side.
- **Secret IDs** are UUIDs, making IDOR-style enumeration infeasible.
- **Configuration secrets** (DB password, JWT & AES keys) are injected via environment variables, never hardcoded.
- **Dependencies** are scanned on every push by OWASP Dependency-Check.

---

## 🧪 Testing

- A complete **Postman collection** (with environment variables and token/UUID automation) is provided under `demo/postman/`.
- The **CI pipeline** spins up an ephemeral PostgreSQL and runs the test suite on every push and pull request.
- Manual security scenarios validated: `401` (no token), `403` (cross-user access), `404` (unknown UUID), `409` (duplicate email), `400` (invalid body).

---

## 📁 Project Structure

```
demo/
├── src/main/java/com/safebox/demo/
│   ├── controller/     # REST endpoints (Auth, Secret)
│   ├── service/        # Business logic
│   ├── repository/     # Spring Data JPA
│   ├── entity/         # User, Secret, Role
│   ├── dto/            # Request/response objects
│   ├── security/       # JwtUtils, JwtAuthenticationFilter, EncryptionUtils
│   ├── config/         # SecurityConfig, OpenApiConfig, GlobalExceptionHandler
│   └── exception/      # Typed business exceptions
├── postman/            # Postman collection & environment
├── Dockerfile          # Multi-stage build
├── docker-compose.yml  # App + PostgreSQL
├── .env.example        # Configuration template
└── .github/workflows/  # CI pipeline
```

---

## 🗺️ Roadmap

- [ ] Two-factor authentication (TOTP / WebAuthn)
- [ ] Key rotation via a KMS or HashiCorp Vault (envelope encryption)
- [ ] Immutable audit trail (Hibernate Envers)
- [ ] Web frontend (React) + secret sharing between users (asymmetric crypto)
- [ ] Production deployment with HTTPS (Nginx reverse proxy + Let's Encrypt)
- [ ] Expanded JUnit 5 / Mockito unit-test coverage

---

## 👤 Author

**Aymane EL GLAOUI**

---

*SafeBox — the quiet architecture of trust.*
