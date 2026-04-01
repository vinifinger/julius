# Task: Expand Unit Test Coverage

## Objetivo
Analisar todas as funcionalidades desenvolvidas até o momento no projeto Julius e criar testes unitários abrangentes, cobrindo todos os cenários possíveis (caminho feliz, regras de negócio e fluxos de erro) para garantir a estabilidade e a corretude do sistema segundo as *Architecture Guidelines*.

## Foco de Testes (UseCases e Domain Services)
A lógica principal reside nas camadas de Aplicação (`UseCases`) e Domínio (`Services` e `Entities`). O foco primário desta task é levar a cobertura destas classes para perto de 100%.

Os seguintes componentes devem ser analisados e testados exaustivamente:

### 1. `AuthUseCase`
- [ ] Login com sucesso.
- [ ] Login com e-mail inexistente.
- [ ] Login com senha incorreta.
- [ ] Registro com sucesso.
- [ ] Registro com e-mail duplicado.
- [ ] Autenticação Firebase com sucesso (usuário novo vs existente).
- [ ] Autenticação Firebase com token inválido.

### 2. `TransactionUseCase` & `TransactionService`
- [ ] Criação de transação `PAID` (deve impactar o saldo da conta).
- [ ] Criação de transação `PENDING` (não deve impactar o saldo).
- [ ] Criação de transação com ID de categoria/conta/competência inexistente.
- [ ] Transição de `PENDING` para `PAID` (deve somar/subtrair do saldo).
- [ ] Transição de `PAID` para `PENDING` (deve estornar o saldo).
- [ ] Deleção de transação `PAID` (deve estornar o saldo).
- [ ] Deleção de transação `PENDING` (não deve alterar o saldo).
- [ ] Transações do tipo `EXPENSE` (despesa) vs `REVENUE` (receita) e seus impactos matemáticos corretos.

### 3. `AccountUseCase`
- [ ] Criação de conta com sucesso (saldo inicial zero, moeda BRL default).
- [ ] Busca do saldo total consolidado de todas as contas do usuário.
- [ ] Tentativa de acesso a uma conta pertence a outro usuário (segurança/ownership).

### 4. `CategoryUseCase`
- [ ] Criação de categoria com sucesso.
- [ ] Listagem de categorias filtrada corretamente por usuário.

### 5. `CompetenceUseCase`
- [ ] Criação de nova competência.
- [ ] Retorno de competência já existente (evitar duplicatas no banco para o mesmo usuário/mês/ano).
- [ ] Geração dinâmica da "competência atual" baseada no clock do sistema.

### 6. `DashboardUseCase`
- [ ] Geração precisa do sumário financeiro (`DashboardSummary`) agrupando receitas, despesas e calculando o saldo no período.
- [ ] Agrupamento correto de despesas por categoria (`ExpenseByCategorySummary`) incluindo cálculo percentual.
- [ ] Identificação correta dos dados vazios quando não há transações no período.

## Regras de Implementação para os Testes

1. **Frameworks:** Utilizar **JUnit 5** e **Mockito** (via `@ExtendWith(MockitoExtension.class)`).
2. **Padrão BDD:** Estruturar todos os métodos de teste utilizando a nomenclatura e separação clara de `// Given`, `// When`, `// Then`.
3. **Nomenclatura (Display names):** Utilizar anotações `@DisplayName` explicativas em inglês para descrever o comportamento esperado (Ex: *"Should throw InvalidTransactionException when trying to delete a locked transaction"*).
4. **Isolamento:** NUNCA carregar o contexto inteiro do Spring `@SpringBootTest` para testes unitários. Usar apenas mocks (`@Mock`, `@InjectMocks`).
5. **Agrupamento:** Utilizar `@Nested` para agrupar testes pertencentes ao mesmo método sob teste. Exemplo: um bloco `@Nested class CreateTransaction { ... }` dentro de `TransactionUseCaseTest`.
6. **Assertivas Limpas:** Evitar lógicas complexas dentro das assertivas. Utilizar os métodos do `org.junit.jupiter.api.Assertions.*` de forma direta.

## Passos para Execução (Agent)

1. Faça um scan na pasta `src/main/java/com/finance/app/application/usecase` e anote todos os métodos públicos.
2. Identifique quais testes já existem em `src/test/java/com/finance/app/application/usecase`.
3. Para cada método mapeado, escreva todos os cenários felizes e não-felizes (validações, NotFound, Business Rules).
4. Rode a suite de testes continuamente: `./gradlew test` para garantir que o que foi escrito compila e passa.
5. Se o projeto não utilizar um plugin de Coverage (JaCoCo), implemente os testes com base visual na lógica do UseCase.
