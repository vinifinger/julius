Task: Implementação de Endpoints Base (Accounts, Categories & Balance)
Título: Desenvolvimento de CRUDs Auxiliares e Consulta de Saldo Consolidado.
Prioridade: Alta (Bloqueia o fluxo completo de transações).
Contexto: Implementar as rotas que alimentam os campos de seleção (dropdowns) do sistema.

1. Escopo de Endpoints
A. Gerenciamento de Contas (Accounts)
GET /api/v1/accounts: Lista todas as contas (Ex: Carteira, Nubank, Banco Inter) com seus respectivos saldos.

POST /api/v1/accounts: Cria uma nova conta.

Payload: { "name": "Nome da Conta", "balance": 100.00, "currency": "BRL" }

B. Gerenciamento de Categorias (Categories)
GET /api/v1/categories: Retorna as categorias disponíveis.

POST /api/v1/categories: Cria uma categoria personalizada.

Payload: { "name": "Alimentação", "color_hex": "#FF5733" }

C. Verificação de Saldo (Balance)
GET /api/v1/accounts/{id}/balance: Retorna o saldo atualizado de uma conta específica.

GET /api/v1/accounts/total-balance: Endpoint de conveniência que soma o saldo de todas as contas do usuário para exibir no topo do dashboard.

2. Requisitos Técnicos
Soft Delete (Sugestão): Em vez de DELETE físico, considere implementar um campo active (boolean). Deletar uma conta com histórico de transações causaria erro de integridade referencial.

Cálculo em Tempo Real: O endpoint de total-balance deve realizar a soma via query agregada no banco de dados (SELECT SUM(balance) FROM accounts WHERE user_id = ...) para garantir performance.

Diferenciação de DTOs: Criar DTOs específicos de saída para não expor IDs de usuário ou metadados de auditoria desnecessários.

3. Instrução Detalhada para o Agente (Antigravity)
"Agente, desenvolva os CRUDs de Account e Category seguindo o padrão Clean Architecture:

Domain: Crie as interfaces de repositório e os modelos de domínio simples.

Infrastructure: Implemente as JPA Entities e os JpaRepositories correspondentes.

Application: Crie os Use Cases FindAllAccountsUseCase e CreateCategoryUseCase.

Web: Implemente os Controllers com os mapeamentos REST apropriados.

Regra de Negócio Especial: No cadastro de Conta, o saldo inicial deve ser tratado como uma transação de 'Saldo Inicial' ou apenas populado no campo balance da entidade Account (decida pela simplicidade no MVP).

Testes: Garanta que o teste de total-balance some corretamente valores de contas diferentes (ex: R$ 100,00 em uma e R$ 250,00 em outra, totalizando R$ 350,00)."

4. Critérios de Aceite (DoD)
As rotas de GET e POST para Contas e Categorias funcionam conforme o design.

O saldo total consolidado retorna o valor exato da soma das contas.

O Swagger reflete essas novas rotas com exemplos de JSON de entrada.