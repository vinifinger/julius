---
description: Advanced Architecture and Design Patterns for Julius Project
trigger: always_on
---

# Julius Project - Advanced Architecture Guidelines
Este documento define as regras de arquitetura avançadas e padrões de software para o projeto Julius. Qualquer agente ou desenvolvedor atuando no projeto **deverá obrigatoriamente** seguir estas diretrizes para manter a consistência, estabilidade e performance.

## 1. Princípios Fundamentais (Clean Architecture & DDD)
- **Isolamento do Domínio:** A camada de `domain` é o núcleo do sistema. Ela **não pode ter nenhuma dependência externa** (sem Spring, sem JPA, sem anotações de infraestrutura).
- **Inversão de Dependência (Ports & Adapters):** Toda comunicação do Domínio com o mundo externo (Banco de dados, APIs de terceiros como Firebase, Contexto de Segurança) deve ser feita via Interfaces (Ports) localizadas no domínio. As implementações (Adapters) ficam na `infrastructure`.
- **Casos de Uso Coesos:** A camada de `application` orquestra a lógica chamando o domínio e a infraestrutura. Evite vazar lógica de negócios pura para os Services/UseCases; a lógica preferencialmente deve nascer dentro das Entidades ou Records de domínio.

## 2. Padrões de Design e Extensibilidade
- **Feature Toggles (Design Orientado a Interface):** Funcionalidades transversais (ex: Segurança, Autenticação) deverão utilizar Feature Toggles (via `application.yml` e `@ConditionalOnProperty`). Para manter o código limpo, utilize o padrão Strategy/Interface: 
  - Exemplo: `TokenVerifier` (Port) possui `FirebaseTokenVerifier` (ativo quando segurança habilitada) e `MockTokenVerifier` (ativo quando desabilitada).
  - Nunca encha o UseCase de "IFs" verificando se uma feature está ativa.
- **Abstração de Contexto:** Não acople Controllers diretamente a headers específicos (como `@RequestHeader` de autenticação) quando puder usar abstrações de Injeção de Dependência, como o `UserContext`.

## 3. Persistência, Performance e Projeções
- **Projeções Fortemente Tipadas:** **É terminantemente proibido o uso de mapas genéricos (`Map`) ou arrays de objetos (`Object[]`)** em respostas de repositórios (JPA/Query). Todas as projeções e consultas agregadas devem ser mapeadas diretamente para `Records` de domínio (ex: `CompetenceAmountSummary`).
- **Camada de Banco e Migrations:** Qualquer mudança estrutural no banco de dados deve ser feita exclusivamente via **Flyway** (`V...__name.sql`). O banco é a fonte de verdade dos UUIDs; o Id deve ser gerado pelo Hibernate/MySQL e não setado na mão pela aplicação antes de salvar.
- **Indexação:** Novas tabelas ou funcionalidades de busca frequente devem ser acompanhadas da criação de `INDEX` correspondentes no banco de dados, visando performance em escala.

## 4. Estabilidade e Tratamento de Erros
- **Domain Exceptions:** Toda regra de negócio violada precisa de uma exceção específica de domínio (ex: `InvalidFirebaseTokenException`, `DuplicateEmailException`). Nunca levante `RuntimeException` genérica.
- **English Messages:** Todas as mensagens de erro retornadas pelas exceções (que serão visualizadas nas APIs) devem estar em Inglês técnico, claras, amigáveis, e formatadas no `GlobalExceptionHandler`.
- **Validação Antecipada:** O Web layer é a primeira linha de defesa. Use Jakarta Validation (`@Valid`, `@NotBlank`) nos DTOs de Request para barrar dados impuros antes de atingir a Application layer.

## 5. Consistência e Código Limpo (Clean Code)
- **Lombok First e Redução de Boilerplate:** SEMPRE utilizar as anotações do Lombok (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, etc.) em vez de escrever o código manualmente. O código deve ser focado na lógica, não em métodos acessórios.
- **Padrão Builder:** Quando for necessário criar uma classe ou um objeto com **mais de 5 parâmetros**, a utilização do padrão `@Builder` do Lombok é **obrigatória** para melhorar a legibilidade e escalabilidade do código.
- **Imutabilidade Orientada:** Priorize `Records` nativos do Java 21 sobre Classes POJO para garantir Imutabilidade no mapeamento de DTOs e Projeções.
- **Evitar Null-Checks Inseguros:** Use `java.util.Optional` em retornos de busca no banco. Use `java.util.Objects.nonNull()` para checagem condicional (nunca `== null`).
- **Segregação de DTOs:** DTOs não devem ser reusados como "coringas". Há sempre um `Request` (entrada) e `Response` (saída) claros contidos nos subpacotes em `web/dto/`.
- **Ausência total de "var":** O tipo deve ser sempre claro e explícito.
- **Uso de YAML em perfis:** O projeto adota YAML (`application.yml`, `application-local.yml`, `application-test.yml`). Parametrização via variáveis de ambiente com defaults explícitos (ex: `${DB_URL:jdbc:...}`) deve prevalecer para garantir a portabilidade entre ambientes e esteiras CI/CD.

Ao implementar novas funcionalidades (como novos Endpoints/UseCases), sempre revise e revalide seu código contra estes 5 princípios de arquitetura antes de concluir a entrega.
