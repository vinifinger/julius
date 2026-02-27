# Julius — Documentação de Arquitetura e Padrões

## 1. Visão Geral

**Julius** é uma API REST de controle financeiro pessoal construída em **Java 21** + **Spring Boot 3.4.3** seguindo os princípios de **Clean Architecture** e **DDD** (Domain-Driven Design).

O sistema permite gerenciar usuários, contas, categorias, competências (períodos mensais), transações financeiras e um dashboard com projeções agregadas.

---

## 2. Arquitetura de Camadas

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

### Regras de Dependência

- **Domain** → não importa Spring, JPA, ou qualquer framework
- **Application** → depende de Domain + DTOs da Web (Records)
- **Infrastructure** → implementa interfaces do Domain, usa JPA/Spring
- **Web** → depende de Application (UseCases), define DTOs e Controllers

---

## 3. Estrutura de Pacotes

```
com.finance.app
├── JuliusApplication.java
├── application
│   └── usecase
│       ├── AccountUseCase.java
│       ├── CategoryUseCase.java
│       ├── CompetenceUseCase.java
│       ├── DashboardUseCase.java
│       ├── TransactionUseCase.java
│       └── UserUseCase.java
├── domain
│   ├── entity
│   │   ├── Account.java
│   │   ├── Category.java
│   │   ├── CategoryExpenseSummary.java    (Record de projeção)
│   │   ├── Competence.java
│   │   ├── CompetenceAmountSummary.java   (Record de projeção)
│   │   ├── Transaction.java
│   │   ├── TransactionStatus.java         (enum: PAID, PENDING)
│   │   ├── TransactionType.java           (enum: REVENUE, EXPENSE)
│   │   └── User.java
│   ├── exception
│   │   ├── AccountNotFoundException.java
│   │   ├── CategoryNotFoundException.java
│   │   ├── CompetenceNotFoundException.java
│   │   ├── DuplicateEmailException.java
│   │   ├── InvalidTransactionException.java
│   │   ├── TransactionNotFoundException.java
│   │   └── UserNotFoundException.java
│   ├── repository
│   │   ├── AccountRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── CompetenceRepository.java
│   │   ├── TransactionRepository.java
│   │   └── UserRepository.java
│   └── service
│       └── TransactionService.java
├── infrastructure
│   ├── config
│   │   ├── DomainServiceConfig.java
│   │   ├── JpaAuditingConfig.java
│   │   └── OpenApiConfig.java
│   └── persistence
│       ├── AccountRepositoryImpl.java
│       ├── CategoryRepositoryImpl.java
│       ├── CompetenceRepositoryImpl.java
│       ├── TransactionRepositoryImpl.java
│       ├── UserRepositoryImpl.java
│       ├── entity (JPA Entities)
│       ├── mapper (Domain ↔ JPA)
│       └── repository (Spring Data JPA Interfaces)
└── web
    ├── controller
    │   ├── AccountController.java
    │   ├── CategoryController.java
    │   ├── CompetenceController.java
    │   ├── DashboardController.java
    │   ├── TransactionController.java
    │   └── UserController.java
    ├── dto
    │   ├── request
    │   └── response
    └── exception
        └── GlobalExceptionHandler.java
```

---

## 4. Módulos de Domínio

### 4.1 User (Usuário)

Gerenciamento de usuários com validação de email único.

| Endpoint | Método | Descrição |
|---|---|---|
| `/api/v1/users` | `POST` | Cria usuário |
| `/api/v1/users/{id}` | `GET` | Busca por ID |
| `/api/v1/users` | `GET` | Lista todos |

### 4.2 Account (Conta)

Contas financeiras com saldo, moeda e vínculo ao usuário.

| Endpoint | Método | Header/Param | Descrição |
|---|---|---|---|
| `/api/v1/accounts` | `POST` | `X-User-Id` | Cria conta (default BRL) |
| `/api/v1/accounts` | `GET` | `X-User-Id` | Lista contas do usuário |
| `/api/v1/accounts/{id}/balance` | `GET` | `X-User-Id` | Saldo de uma conta |
| `/api/v1/accounts/total-balance` | `GET` | `X-User-Id` | Saldo total consolidado |

### 4.3 Category (Categoria)

Categorias de transações (ex: Alimentação, Transporte).

| Endpoint | Método | Header/Param | Descrição |
|---|---|---|---|
| `/api/v1/categories` | `POST` | `X-User-Id` | Cria categoria (com colorHex opcional) |
| `/api/v1/categories` | `GET` | `X-User-Id` | Lista categorias do usuário |

### 4.4 Competence (Competência)

Períodos financeiros mensais (Mês/Ano) para agrupamento de transações.

| Endpoint | Método | Header/Param | Descrição |
|---|---|---|---|
| `/api/v1/competences` | `POST` | `X-User-Id` | Cria competência (retorna existente se duplicado) |
| `/api/v1/competences` | `GET` | `X-User-Id` | Lista ordenada (ano/mês decrescente) |
| `/api/v1/competences/current` | `GET` | `X-User-Id` | Competência atual (auto-cria se não existir) |

### 4.5 Transaction (Transação)

Transações financeiras (REVENUE/EXPENSE) com controle de status (PAID/PENDING) e impacto automático no saldo.

| Endpoint | Método | Header/Param | Descrição |
|---|---|---|---|
| `/api/v1/transactions` | `POST` | `X-User-Id` | Cria transação (atualiza saldo se PAID) |
| `/api/v1/transactions/{id}` | `GET` | — | Detalha transação |
| `/api/v1/transactions` | `GET` | `X-User-Id` | Lista transações do usuário |
| `/api/v1/transactions/{id}/status` | `PATCH` | — | Altera status (PENDING ↔ PAID) |
| `/api/v1/transactions/{id}` | `DELETE` | — | Remove (estorna saldo se PAID) |

**Regras de negócio:**
- Transação `PAID` → atualiza saldo da conta automaticamente
- `PENDING → PAID` → aplica valor no saldo
- `PAID → PENDING` → reverte valor do saldo
- `DELETE` de transação `PAID` → estorna saldo
- Apenas transações com status `PAID` afetam o saldo

### 4.6 Dashboard (Painel)

Projeções agregadas para visualização financeira.

| Endpoint | Método | Param | Descrição |
|---|---|---|---|
| `/api/v1/dashboard/summary` | `GET` | `?competenceId=` | Resumo: receita, despesa, saldo, status |
| `/api/v1/dashboard/expenses-by-category` | `GET` | `?competenceId=` | Despesas agrupadas por categoria (com %) |
| `/api/v1/dashboard/evolution` | `GET` | Header `X-User-Id` | Evolução dos últimos 6 meses |

---

## 5. Padrões e Convenções de Código

### 5.1 Gerais

| Regra | Descrição |
|---|---|
| **Lombok obrigatório** | `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`, etc. Nunca escrever getters/setters manualmente |
| **Builder** | Obrigatório para objetos com mais de 3 parâmetros |
| **Tipagem explícita** | Nunca usar `var`. Sempre declarar o tipo: `UserEntity user = ...` |
| **Null checking** | Nunca usar `!= null` ou `== null`. Sempre `Objects.isNull()` ou `Objects.nonNull()` |
| **BigDecimal** | Usar para todos os valores monetários. Escala 2, `RoundingMode.HALF_EVEN` |
| **UUID** | Chave primária de todas as entidades. Gerado pelo banco via `@GeneratedValue(strategy = UUID)` |

### 5.2 Camada de Domínio

- Entidades Lombok (`@Builder`, `@Getter`, `@Setter`)
- Sem imports do Spring ou JPA
- Exceções específicas por regra de negócio violada (nunca genéricas)
- Interfaces de repositório (Ports) definem contratos tipados — nunca `Object[]`
- Records de projeção para resultados de queries agregadas (`CategoryExpenseSummary`, `CompetenceAmountSummary`)

### 5.3 Camada de Aplicação

- UseCases agrupados por domínio em uma única classe (`TransactionUseCase` com create, getById, listByUser, etc.)
- `@Service` + `@RequiredArgsConstructor`
- `@Transactional` em métodos que alteram mais de uma entidade
- IDs gerados internamente (nunca recebidos do cliente na criação)

### 5.4 Camada de Infraestrutura

- Entidades JPA sem sufixo "Jpa" (ex: `AccountEntity`, não `AccountJpaEntity`)
- Mappers dedicados para converter Domain ↔ JPA Entity
- Conversão de `Object[]` (retorno do JPQL) → Records tipados no `RepositoryImpl`
- `@EntityListeners(AuditingEntityListener.class)` + `@CreatedDate` / `@LastModifiedDate`
- Flyway para migrations (`src/main/resources/db/migration`)
- Naming: tabelas `snake_case` plural (`transactions`), colunas `snake_case`

### 5.5 Camada Web

- DTOs são Java Records separados em `web/dto/request/` e `web/dto/response/`
- Validação via `jakarta.validation` (`@NotNull`, `@NotBlank`, `@Min`, `@Max`, `@DecimalMin`)
- `userId` recebido via `@RequestHeader("X-User-Id")` (simula contexto de autenticação)
- UUIDs para entidades existentes (`accountId`, `categoryId`) enviados no request body
- `GlobalExceptionHandler` (`@RestControllerAdvice`) traduz exceções → JSON com HTTP status correto

---

## 6. Autenticação (Simulada)

O sistema simula contexto de usuário autenticado via header HTTP:

```
X-User-Id: <UUID do usuário>
```

Todos os endpoints que operam em contexto de usuário recebem este header. Consultas de entidades (Account, Transaction) verificam ownership via `findByIdAndUserId`.

---

## 7. Testes

| Tipo | Padrão | Framework |
|---|---|---|
| Unitários | Given-When-Then | JUnit 5 + Mockito |
| Foco | Lógica de negócio nos UseCases | `@ExtendWith(MockitoExtension.class)` |
| Estrutura | Uma classe de teste por UseCase | `@Nested` por método |

```bash
# Rodar testes unitários
./gradlew test --tests "com.finance.app.application.usecase.*"

# Rodar todos os testes
./gradlew test
```

---

## 8. Stack Tecnológico

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 3.4.3 | Framework |
| MySQL | 8.0 | Banco de dados |
| Flyway | — | Migrations |
| Lombok | — | Redução de boilerplate |
| SpringDoc OpenAPI | — | Swagger UI |
| JUnit 5 + Mockito | — | Testes |
| Docker Compose | — | Infraestrutura local |
| Gradle | 9.x | Build tool |

---

## 9. Como Rodar

```bash
# 1. Subir banco de dados
docker compose up -d

# 2. Executar aplicação (Flyway aplica migrations automaticamente)
./gradlew bootRun

# 3. Acessar Swagger UI
# http://localhost:8080/swagger-ui/index.html
```

**Banco de dados:**
- Host: `localhost:3306`
- Database: `julius`
- Usuário: `julius_user` / Senha: `julius_pass`
