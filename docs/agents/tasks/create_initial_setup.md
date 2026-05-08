Task: Implementação da Camada de Persistência e Migrations (Banco de Dados)
Título: Configuração do Schema MySQL via Flyway e Mapeamento de Entidades JPA.
Status: Backlog / A fazer.
Prioridade: Crítica (Base do Sistema).

1. Objetivo Objetivos
Implementar a estrutura de dados relacional no MySQL 8.0 utilizando Flyway para versionamento e JPA/Hibernate para o mapeamento objeto-relacional (ORM), respeitando a separação entre Entidades de Domínio e Entidades de Infraestrutura.

2. Detalhes Técnicos e Requisitos
A. Scripts de Migração (Flyway)
Crie o arquivo src/main/resources/db/migration/V1__initial_setup.sql. O script deve:

Utilizar UUID como Primary Key (PK) para todas as tabelas.

Utilizar o tipo DECIMAL(12,2) para todos os campos de valor financeiro (amount, balance).

Configurar as chaves estrangeiras (FK) com integridade referencial.

Tabelas Necessárias: users, accounts, competences, categories, types, authors, transactions.

Adicionar índices (INDEX) nos campos de busca frequente: user_id, date_time, e competence_id.

Tarefa: Implementação do Schema de Base de Dados e Camada de Entidades JPA
Objetivo: Criar o esquema físico no MySQL e o mapeamento relacional (ORM) garantindo integridade e performance.

1. Especificação da Migration (Flyway)
O desenvolvedor deve criar o ficheiro src/main/resources/db/migration/V1__create_initial_schema.sql com as seguintes definições:

Tabelas Obrigatórias: users, accounts, competences, categories, types, authors, transactions.

Regras de Tipagem:

Identificadores: Utilizar BINARY(16) ou VARCHAR(36) para armazenar UUIDs (PKs).

Valores Monetários: Utilizar obrigatoriamente DECIMAL(12, 2).

Datas: Utilizar DATETIME ou TIMESTAMP com Timezone para transações e DATE para competências.

Restrições e Índices:

NOT NULL em todos os campos obrigatórios.

UNIQUE no e-mail do utilizador e na combinação de mês/ano da competência por utilizador.

INDEX em transactions.date_time, transactions.user_id e transactions.account_id para otimizar dashboards.

2. Mapeamento de Entidades JPA (Infrastructure Layer)
As classes devem ser criadas no pacote infrastructure.persistence.entities.

Requisitos de Implementação:

Sufixo: Utilizar o sufixo Entity em todas as classes JPA (ex: TransactionEntity).

Relacionamentos:

Utilizar @ManyToOne(fetch = FetchType.LAZY) para evitar carregamento desnecessário de dados (problema N+1).

Configurar @JoinColumn explicitamente com o nome da coluna no banco de dados.

Auditoria:

Ativar @EnableJpaAuditing.

Incluir campos createdAt e updatedAt em todas as entidades usando @CreatedDate e @LastModifiedDate.

UUID: Configurar a geração automática no Java caso o banco de dados não a suporte nativamente:

Java

@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
3. Conteúdo do Script SQL (V1__create_initial_schema.sql)
SQL

CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE accounts (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL,
    balance DECIMAL(12, 2) DEFAULT 0.00,
    currency VARCHAR(10) DEFAULT 'BRL',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE competences (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    month TINYINT NOT NULL CHECK (month BETWEEN 1 AND 12),
    year SMALLINT NOT NULL,
    UNIQUE (user_id, month, year),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE categories (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL,
    color_hex VARCHAR(7),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE transactions (
    id BINARY(16) PRIMARY KEY,
    account_id BINARY(16) NOT NULL,
    category_id BINARY(16) NOT NULL,
    competence_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    parent_id BINARY(16),
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    date_time DATETIME NOT NULL,
    status ENUM('PENDING', 'PAID') DEFAULT 'PENDING',
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (competence_id) REFERENCES competences(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES transactions(id)
) ENGINE=InnoDB;

CREATE INDEX idx_trans_date ON transactions(date_time);
CREATE INDEX idx_trans_user ON transactions(user_id);
4. Definição de "Pronto" (Definition of Done)
A aplicação Spring Boot inicia sem erros de Hibernate.

O Flyway exibe o status SUCCESS para a migração V1.

Existem testes de repositório (@DataJpaTest) que validam a persistência de uma transação completa.

B. Mapeamento JPA (Infrastructure Layer)
As entidades na pasta infrastructure/persistence/entities/ devem seguir estas regras:

Naming: Use o sufixo Entity (ex: TransactionEntity.java) para diferenciar do modelo de domínio.

Mapeamento de UUID: Use @JdbcTypeCode(SqlTypes.CHAR) ou @Column(columnDefinition = "BINARY(16)") para otimizar o armazenamento de UUIDs no MySQL.

Auditoria: Implementar @CreatedDate e @LastModifiedDate utilizando o Spring Data JPA Auditing.

Fetch Type: Utilize FetchType.LAZY em todos os relacionamentos @ManyToOne e @OneToMany para evitar problemas de performance (N+1).

C. Isolamento de Domínio
As Entidades de Domínio (domain/model/) devem ser POJOs puros, sem anotações @Entity.

Deve ser criado um Mapeador (Mapper) para converter TransactionEntity (JPA) em Transaction (Domínio) e vice-versa.

3. Critérios de Aceite
Migração Bem-sucedida: Ao subir a aplicação Spring Boot, o Flyway deve executar o script e criar as tabelas no MySQL sem erros.

Validação de Tipos: O campo amount no banco deve ser rigorosamente decimal(12,2).

Integridade: Tentar deletar um usuário deve disparar o ON DELETE CASCADE ou impedir a exclusão se houver transações vinculadas (conforme regra definida).

Teste de Persistência: Deve haver um teste de integração (usando @DataJpaTest ou Testcontainers) que salve uma TransactionEntity e recupere com sucesso, validando os relacionamentos com Account e Category.