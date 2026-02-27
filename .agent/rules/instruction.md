---
trigger: always_on
---

Antigravity Project Rules: Personal Finance Backend
1. Architectural Blueprint (Clean Architecture & DDD)
O agente deve seguir rigorosamente a separação de interesses em camadas:

Domain Layer: Contém as entidades de negócio (ex: Transaction.java), Domain Services e interfaces de Repositório. Proibido importar qualquer dependência do Spring ou JPA aqui.

Application Layer: Contém os casos de uso (UseCases), Portas de entrada/saída. Define o que o sistema faz.

Infrastructure Layer: Implementações técnicas. Inclui Repositórios JPA, configurações de banco de dados (MySQL), Beans do Spring e integrações externas.

Presentation/Web Layer: Controllers REST, mapeamento de rotas, tratamento global de exceções e DTOs (Records) de entrada e saída.

2. Implementation Standards
Money Handling: Use exclusivamente java.math.BigDecimal para qualquer valor monetário. Configure a escala para 2 casas decimais com RoundingMode.HALF_EVEN.

Entity Identity: Utilize java.util.UUID como chave primária (PK) para todas as tabelas e entidades.

Immutability: Prefira o uso de Java Records para DTOs e Value Objects. Campos em entidades de domínio devem ser privados, com acesso via métodos semânticos (evite setters genéricos).

Validation: Use jakarta.validation (Bean Validation) para validar DTOs na camada de entrada (Web).

3. Database & Persistence (MySQL + JPA)
Separation of Models: Diferencie a Entidade de Domínio da Entidade JPA. Use mapeadores (mappers) para converter entre elas na camada de Infrastructure.

Migrations: Toda alteração no banco de dados deve ser acompanhada de um script Flyway (src/main/resources/db/migration).

Naming Convention: Tabelas em snake_case e plural (ex: transactions), colunas em snake_case.

4. Testing Requirements (Mandatory)
Unit Tests: Todo Caso de Uso (UseCase) deve ter uma classe de teste JUnit 5 correspondente com Mockito.

Coverage Focus: O foco deve ser a lógica de negócio e as regras de validação.

Pattern: Siga o padrão Given-When-Then para a escrita dos métodos de teste.

5. Error Handling
Business Exceptions: Crie exceções específicas para o domínio (ex: InsufficientBalanceException, DuplicateEmailException). Nunca usar exceções genéricas.

Global Handler: Implemente um @RestControllerAdvice para traduzir exceções em respostas JSON amigáveis com os códigos HTTP corretos (400 para erros de negócio, 404 para recursos não encontrados).

6. Coding Conventions
Lombok First: Sempre utilizar annotations Lombok em vez de escrever código manualmente. Exemplos: @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor, @RequiredArgsConstructor. Nunca escrever getters, setters ou construtores manualmente.

Builder Pattern: Quando for necessário criar um objeto com mais de 3 parâmetros, sempre utilizar um Builder (@Builder) para melhorar a legibilidade.

DTOs na Web Layer: DTOs (Records) de entrada e saída devem ficar na camada Presentation/Web (web/dto/) para concentrar a entrada e saída em um só lugar.

Entity Naming: Não utilizar sufixos como "Jpa", "Entity" ou similares nos nomes das entidades de infraestrutura. Usar nomes neutros que não acoplem a uma tecnologia. Exemplo: UserEntity em vez de UserJpaEntity.

UseCase Agrupado: Não criar classes separadas para cada caso de uso. Agrupar por domínio em uma única classe. Exemplo: UserUseCase contendo create, getById, listAll, etc.

Exceptions Específicas: Sempre criar exceções específicas por regra de negócio violada. Exemplo: DuplicateEmailException em vez de BusinessException genérica.

Tipagem Explícita: Não utilizar var para declaração de variáveis quando existe uma classe/objeto/tipagem específica para o mesmo. Sempre declarar o tipo explicitamente. Exemplo: UserEntity user = ... em vez de var user = ...

DTOs Separados: Sempre separar classes de DTO em subpacotes request/ e response/ dentro de web/dto/. Exemplo: web/dto/request/CreateUserRequest.java e web/dto/response/UserResponse.java.

Null Checking: Nunca verificar valor null utilizando != null ou == null. Sempre utilizar java.util.Objects para validar nulidade com Objects.isNull() ou Objects.nonNull(). Exemplo: Objects.isNull(parent) em vez de parent == null.