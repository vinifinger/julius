Task: Implementação do Módulo de Competências Mensais
Título: Desenvolvimento da Lógica de Gestão de Competências (Agrupamento Mensal).
Prioridade: Alta.
Contexto: Garantir que todas as transações estejam vinculadas a um período (Mês/Ano) para viabilizar o fluxo de caixa mensal.

1. Objetivos da Feature
Permitir a criação de períodos financeiros (Ex: "Fevereiro/2026").

Garantir que não existam competências duplicadas para o mesmo período.

Prover uma rota para listar todas as competências ativas para preencher filtros no Front-end.

2. Especificação de Endpoints (REST)
Método	Endpoint	Funcionalidade
POST	/api/v1/competences	Cria uma nova competência (Ex: Mês 02, Ano 2026).
GET	/api/v1/competences	Lista todas as competências cadastradas (útil para seletores de data).
GET	/api/v1/competences/current	Retorna a competência baseada na data atual do sistema.

Export to Sheets

3. Requisitos de Implementação por Camada
A. Camada de Domínio (domain)
Entidade Competence: Deve validar se o mês está entre 1 e 12 e se o ano é válido (ex: não permitir anos no passado remoto).

Regra de Nome Automático: Se o usuário não enviar um nome, o sistema deve gerar automaticamente (ex: "02/2026").

B. Camada de Aplicação (application)
Use Case CreateCompetence: Antes de salvar, deve verificar no repositório se já existe uma competência para aquele Mês/Ano. Se sim, retornar um erro de "Conflito" ou retornar a competência existente.

Use Case GetSummaryByCompetence: (Opcional para esta task) Preparar a lógica para somar transações vinculadas a este ID de competência.

C. Camada de Infraestrutura (infrastructure)
Repositório JPA: Criar um método findByMonthAndYear(int month, int year).

Constraint de Banco: Relembrar o agente de IA que a tabela deve ter uma UNIQUE KEY composta por (month, year) para evitar duplicidade de dados.

4. Instrução Detalhada para o Agente (Antigravity)
"Agente, implemente o módulo de Competências seguindo estas regras de Clean Architecture:

Criação Inteligente: No Use Case de criação, se o usuário tentar criar uma competência que já existe (mesmo mês e ano), o sistema deve retornar a competência existente em vez de dar erro ou duplicar.

Validação: Garanta que o campo month seja validado com @Min(1) e @Max(12).

Vínculo com Transação: Certifique-se de que o modelo de Transaction (criado anteriormente) consiga receber o competenceId.

Data Automática: Implemente um método no Service que retorne a 'Competência Atual'. Se ela não existir no banco, o sistema deve criá-la automaticamente ao ser solicitada.

Teste Unitário: Valide se o sistema impede a criação de uma competência com mês 13."

5. Critérios de Aceite (DoD)
Endpoint POST /competences funcional e validando duplicidade.

Endpoint GET /competences retornando a lista ordenada por ano e mês (decrescente).

Swagger atualizado com os modelos de Competência.

Teste de integração validando que uma transação pode ser associada a uma competência recém-criada.