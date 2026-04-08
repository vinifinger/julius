# Julius — Personal Finance API 💰

API de controle financeiro pessoal construída com **Clean Architecture** e **DDD** (Domain-Driven Design).

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen?logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-migrations-red?logo=flyway)

> 📄 Para documentação detalhada de arquitetura, padrões e convenções, veja [ARCHITECTURE.md](ARCHITECTURE.md).

---

## 📐 Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                       Web Layer                             │
│   Controllers · DTOs (request/ · response/) · Exception     │
├─────────────────────────────────────────────────────────────┤
│                    Application Layer                        │
│              UseCases (@Service, @Transactional)            │
├─────────────────────────────────────────────────────────────┤
│                      Domain Layer                           │
│   Entities · Records · Services · Repository Interfaces     │
│             Exceptions (sem dependências Spring)            │
├─────────────────────────────────────────────────────────────┤
│                   Infrastructure Layer                      │
│  JPA Entities · Mappers · Repository Impls · JPA Repos      │
│            Configs · Flyway Migrations                      │
└─────────────────────────────────────────────────────────────┘
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
docker compose up --build -d
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

> Todos os endpoints que operam em contexto de usuário recebem o header `X-User-Id: <UUID>`.

### Usuários

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/users` | Cria um novo usuário |
| `GET` | `/api/v1/users/{id}` | Busca usuário por ID |
| `GET` | `/api/v1/users` | Lista todos os usuários |

### Contas

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/accounts` | Cria uma conta (Header: `X-User-Id`) |
| `GET` | `/api/v1/accounts` | Lista contas do usuário (Header: `X-User-Id`) |
| `GET` | `/api/v1/accounts/{id}/balance` | Saldo de uma conta (Header: `X-User-Id`) |
| `GET` | `/api/v1/accounts/total-balance` | Saldo total consolidado (Header: `X-User-Id`) |
| `DELETE` | `/api/v1/accounts/{id}` | Remove conta (protegida se houver parcelamentos pendentes) (Header: `X-User-Id`) |

### Categorias

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/categories` | Cria uma categoria (Header: `X-User-Id`) |
| `GET` | `/api/v1/categories` | Lista categorias do usuário (Header: `X-User-Id`) |

### Competências

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/competences` | Cria competência — retorna existente se duplicado (Header: `X-User-Id`) |
| `GET` | `/api/v1/competences` | Lista ordenada por ano/mês desc (Header: `X-User-Id`) |
| `GET` | `/api/v1/competences/current` | Competência atual — auto-cria se necessário (Header: `X-User-Id`) |

### Transações

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/transactions` | Cria transação — atualiza saldo se PAID (Header: `X-User-Id`) |
| `GET` | `/api/v1/transactions/{id}` | Detalha uma transação |
| `GET` | `/api/v1/transactions` | Lista transações do usuário (Header: `X-User-Id`) |
| `PATCH` | `/api/v1/transactions/{id}/status` | Altera status (PENDING ↔ PAID) |
| `DELETE` | `/api/v1/transactions/{id}` | Remove transação — estorna saldo se PAID |

### Parcelamentos (Installments)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/installments` | Cria série de transações parceladas (Header: `X-User-Id`) |
| `GET` | `/api/v1/installments/{parentId}` | Consulta o progresso da série e os valores pagos/pendentes |
| `PUT` | `/api/v1/installments/{parentId}` | Atualiza o valor total da série recalculando os pendentes |
| `PATCH` | `/api/v1/installments/{parentId}/type` | Inverte a natureza de toda a série (REVENUE ↔ EXPENSE) com reflexos no saldo |

### Dashboard

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/v1/dashboard/summary?competenceId=` | Resumo: receita, despesa, saldo, status (POSITIVE/NEGATIVE/NEUTRAL) |
| `GET` | `/api/v1/dashboard/expenses-by-category?competenceId=` | Despesas agrupadas por categoria com percentual |
| `GET` | `/api/v1/dashboard/evolution` | Evolução dos últimos 6 meses (Header: `X-User-Id`) |

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

### Criar uma conta

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -H "X-User-Id: <UUID_DO_USUARIO>" \
  -d '{
    "name": "Nubank",
    "currency": "BRL"
  }'
```

### Criar uma transação

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -H "X-User-Id: <UUID_DO_USUARIO>" \
  -d '{
    "accountId": "<UUID_DA_CONTA>",
    "categoryId": "<UUID_DA_CATEGORIA>",
    "competenceId": "<UUID_DA_COMPETENCIA>",
    "description": "Supermercado",
    "amount": 150.50,
    "dateTime": "2026-02-27T10:30:00",
    "type": "EXPENSE",
    "status": "PAID"
  }'
```

### Consultar resumo do dashboard

```bash
curl http://localhost:8080/api/v1/dashboard/summary?competenceId=<UUID_DA_COMPETENCIA>
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
# Rodar todos os testes
./gradlew test

# Rodar apenas testes unitários dos UseCases
./gradlew test --tests "com.finance.app.application.usecase.*"
```

---

## 🛠️ Tecnologias

- **Java 21** + **Spring Boot 3.4.3**
- **MySQL 8.0** com **Flyway** para migrations
- **Lombok** para redução de boilerplate
- **SpringDoc OpenAPI** para documentação Swagger
- **JUnit 5** + **Mockito** para testes
- **Docker Compose** para infraestrutura local
