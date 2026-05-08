Task: Implementação dos Endpoints do Dashboard Financeiro (Agregadores)
Título: Desenvolvimento da Camada de Projeção e Agregação de Dados para o Dashboard.
Prioridade: Alta.
Contexto: Criar endpoints otimizados que retornem resumos financeiros baseados numa competência específica ou período de tempo.

1. Especificação dos Endpoints (Projeções de Dados)
A. Sumário de Fluxo de Caixa (GET /api/v1/dashboard/summary)
Retorna os totais consolidados para uma competência.

Query Params: ?competenceId={uuid} (obrigatório).

Response:

JSON

{
  "totalRevenue": 5000.00,
  "totalExpenses": 3250.00,
  "monthlyBalance": 1750.00,
  "status": "POSITIVE"
}
B. Despesas por Categoria (GET /api/v1/dashboard/expenses-by-category)
Retorna o agrupamento de gastos para construção de gráficos (Pizza/Barras).

Query Params: ?competenceId={uuid}.

Response: Lista de objetos com categoryName, totalAmount e percentage.

C. Evolução de Saldo Últimos 6 Meses (GET /api/v1/dashboard/evolution)
Retorna o saldo final de cada uma das últimas 6 competências para gráfico de linhas.

2. Requisitos Técnicos e de Performance
Queries Agregadas (JPQL/SQL): Para evitar o problema de performance N+1, o desenvolvedor deve utilizar funções de agregação do SQL (SUM, GROUP BY) diretamente no JpaRepository.

Projeções (DTOs): Utilizar interfaces ou Records do Java como Projeções do Spring Data JPA para capturar apenas os valores necessários do banco, sem carregar entidades completas.

Cache (Sugestão): Como os dados de competências passadas raramente mudam, esta é uma camada ideal para futura implementação de cache.

3. Instrução Detalhada para o Agente (Antigravity)
"Agente, implemente os endpoints do Dashboard seguindo estas diretrizes de Engenharia Sênior:

Repository Eficiente: No TransactionRepository, crie queries usando @Query para calcular o somatório de amount filtrando por competenceId e type (Revenue/Expense).

DTO de Resumo: Crie um DashboardSummaryDTO (Java Record) que contenha os campos consolidados. O cálculo do monthlyBalance deve ser feito no Use Case ou no Domain Service, subtraindo as despesas das receitas.

Gráficos: Para o endpoint de categorias, o retorno deve ser ordenado do maior gasto para o menor.

Null Safety: Garanta que, se uma competência não tiver transações, o retorno seja 0.00 e não null.

Teste de Integração: Crie um teste que insira 3 transações de categorias diferentes numa competência e valide se o endpoint de 'Despesas por Categoria' retorna a soma correta agrupada."

4. Critérios de Aceite (DoD)
Os cálculos de totalRevenue e totalExpenses batem exatamente com a soma das transações individuais no banco.

O endpoint do dashboard responde em menos de 200ms (performance de leitura).

O Swagger documenta claramente os parâmetros de consulta (Query Params) necessários.

A lógica de negócio para determinar se o saldo é positivo ou negativo está testada unitariamente.