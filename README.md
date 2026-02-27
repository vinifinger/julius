# Julius — Personal Finance API 💰

API de controle financeiro pessoal construída com **Clean Architecture** e **DDD** (Domain-Driven Design).

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen?logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-migrations-red?logo=flyway)

---

## 📐 Arquitetura

```
┌─────────────────────────────────────────────────┐
│                  Web Layer                       │
│   Controllers · DTOs (request/response)          │
├─────────────────────────────────────────────────┤
│              Application Layer                   │
│             UseCases (@Transactional)             │
├─────────────────────────────────────────────────┤
│                Domain Layer                      │
│    Entities · Services · Repositories (ports)    │
│         (sem dependências do Spring)             │
├─────────────────────────────────────────────────┤
│             Infrastructure Layer                 │
│   JPA Entities · Mappers · Repository Impls      │
│          Configs · Flyway Migrations             │
└─────────────────────────────────────────────────┘
```

---

## ⚡ Pré-requisitos

| Ferramenta | Versão |
|---|---|
| Java (JDK) | 21+ |
| Docker | 20+ |
| Gradle | 9.x (wrapper incluso) |

---

## 🚀 Como Rodar

### 1. Clone o repositório

```bash
git clone https://github.com/vinifinger/julius.git
cd julius
```

### 2. Suba o banco de dados

```bash
docker compose up -d
```

Isso inicia um container MySQL 8.0 com:
- **Host:** `localhost:3306`
- **Database:** `julius`
- **Usuário:** `julius_user` / **Senha:** `julius_pass`

### 3. Execute a aplicação

```bash
./gradlew bootRun
```

O Flyway aplica automaticamente as migrations ao iniciar. A API estará disponível em `http://localhost:8080`.

### 4. Acesse o Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📡 Endpoints

### Usuários

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/users` | Cria um novo usuário |
| `GET` | `/api/v1/users/{id}` | Busca usuário por ID |
| `GET` | `/api/v1/users` | Lista todos os usuários |

### Transações

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/transactions` | Cria uma transação (atualiza saldo se PAID) |
| `GET` | `/api/v1/transactions/{id}` | Detalha uma transação |
| `GET` | `/api/v1/transactions?userId=` | Lista transações por usuário |
| `PATCH` | `/api/v1/transactions/{id}/status` | Altera status (PENDING ↔ PAID) |
| `DELETE` | `/api/v1/transactions/{id}` | Remove transação (estorna saldo se PAID) |

---

## 🧪 Exemplos de Uso (curl)

### Criar um usuário

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "password": "senha12345"
  }'
```

### Criar uma transação

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "<UUID_DA_CONTA>",
    "categoryId": "<UUID_DA_CATEGORIA>",
    "competenceId": "<UUID_DA_COMPETENCIA>",
    "userId": "<UUID_DO_USUARIO>",
    "description": "Supermercado",
    "amount": 150.50,
    "dateTime": "2026-02-27T10:30:00",
    "type": "EXPENSE",
    "status": "PAID"
  }'
```

### Alterar status de uma transação

```bash
curl -X PATCH http://localhost:8080/api/v1/transactions/<UUID>/status \
  -H "Content-Type: application/json" \
  -d '{ "status": "PAID" }'
```

---

## 🧪 Testes

```bash
# Rodar todos os testes unitários
./gradlew test
```

---

## 🛠️ Tecnologias

- **Java 21** + **Spring Boot 3.4.3**
- **MySQL 8.0** com **Flyway** para migrations
- **Lombok** para redução de boilerplate
- **SpringDoc OpenAPI** para documentação Swagger
- **JUnit 5** + **Mockito** para testes
- **Docker Compose** para infraestrutura local
