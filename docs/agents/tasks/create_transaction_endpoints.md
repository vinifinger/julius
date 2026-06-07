Task: Implementação dos Endpoints de Transações (CRUD & Business Logic)
Título: Desenvolvimento da Camada de Aplicação e Controllers para Transações.
Prioridade: Alta.
Contexto: Utilizar a estrutura de banco de dados e entidades JPA criadas na task anterior.

1. Objetivo
Criar os endpoints necessários para gerenciar o fluxo financeiro, garantindo que toda transação paga (COMPLETED) atualize o saldo da conta correspondente de forma atômica.

2. Especificação dos Endpoints (REST)
Método	Endpoint	Funcionalidade
POST	/api/v1/transactions	Cria uma nova transação e atualiza o saldo da conta se estiver paga.
GET	/api/v1/transactions	Lista transações com filtros por competência, categoria ou período.
GET	/api/v1/transactions/{id}	Detalha uma transação específica.
PATCH	/api/v1/transactions/{id}/status	Altera o status (ex: de Pendente para Pago) e dispara atualização de saldo.
DELETE	/api/v1/transactions/{id}	Remove a transação e estorna o valor do saldo da conta (se paga).

Export to Sheets

3. Requisitos de Implementação por Camada
A. Camada de Domínio (domain)
Transaction Service: Criar um serviço de domínio para gerenciar a lógica de "fechamento" de transação.

Regra de Saldo: Se type == EXPENSE, o valor deve subtrair da conta. Se type == REVENUE, deve somar.

Validação: Impedir transações com valor zero ou datas em competências fechadas (opcional para MVP).

B. Camada de Aplicação (application)
Use Cases: Criar a classe TransactionUseCase que orquestra a busca das entidades (Account, Category, etc.), chama a lógica de domínio e persiste o resultado.

C. Camada de Apresentação (presentation)
DTOs (Records): Criar TransactionRequest e TransactionResponse.

Controller: Implementar o TransactionController.

Mapeamento: Utilizar mappers para converter o modelo de domínio em DTOs de resposta.

4. Instrução Detalhada para o Desenvolvedor/IA (Antigravity)
"Agente, implemente os endpoints de Transações seguindo estas diretrizes:

O POST deve receber um JSON contendo os UUIDs de account, category e competence.

Atomicidade: Use @Transactional no service para garantir que, se a atualização do saldo da conta falhar, a transação financeira não seja salva.

Cálculos: Utilize BigDecimal com setScale(2, RoundingMode.HALF_EVEN) para evitar erros de precisão.

Isolamento: Certifique-se de que o user_id da transação seja o do usuário autenticado (simule o contexto de segurança se o Auth ainda não estiver pronto).

Testes: Gere um teste de integração que valide se, ao inserir uma despesa de R$ 50,00, o saldo da conta vinculada diminui exatamente R$ 50,00."

5. Critérios de Aceite (DoD)
Endpoint POST retorna 201 Created e o objeto criado.

O saldo da tabela accounts reflete as transações com status COMPLETED.

Erros de "Conta não encontrada" ou "Categoria inexistente" retornam 400 Bad Request com mensagem clara.

Cobertura de testes unitários nos Use Cases acima de 80%.


Especificação Técnica: Lógica de Domínio (Balance Calculator)
1. O Modelo de Domínio (Account.java)
A entidade de domínio deve ser a responsável por alterar seu próprio estado. Isso evita o modelo anêmico.

Instrução para a IA:

"No pacote domain.model, implemente o método updateBalance na entidade Account. O método deve receber o valor da transação (BigDecimal) e o tipo (REVENUE ou EXPENSE). Use BigDecimal.add() e BigDecimal.subtract() para garantir a precisão decimal."

2. O Domain Service (TransactionService.java)
Como a criação de uma transação envolve múltiplas entidades (Transaction e Account), utilizamos um Service de Domínio para orquestrar essa interação.

Lógica a ser implementada:

Java

// Exemplo da lógica que o agente deve seguir
public void processTransaction(Transaction transaction, Account account) {
    if (transaction.isCompleted()) {
        if (transaction.getType().isExpense()) {
            account.subtract(transaction.getAmount());
        } else {
            account.add(transaction.getAmount());
        }
    }
}

📋 Task: Implementação da Lógica de Cálculo e Persistência Atômica
Título: Desenvolvimento do Core Business Logic para Saldo de Contas.
Status: Definição Técnica.

Detalhes Minuciosos para o Desenvolvedor:
Tratamento de Sinais:

O valor (amount) persistido no banco de dados deve ser sempre positivo.

A lógica de somar ou subtrair deve ser decidida pelo campo type_id (Entry/Expense) no momento do processamento.

Atomicidade com Spring @Transactional:

O Use Case CreateTransaction deve ser anotado com @Transactional.

Fluxo: 1. Salva a Transação -> 2. Atualiza o Saldo da Conta -> 3. Commita.

Se o passo 2 falhar, o passo 1 deve sofrer Rollback automático.

Precisão Decimal:

Configure o MathContext ou use .setScale(2, RoundingMode.HALF_EVEN) em todos os cálculos antes de persistir.

🧪 Instrução de Teste Unitário para o Agente (Antigravity)
Para garantir que a IA não cometa erros comuns de arredondamento ou lógica invertida, exija este teste:

"Agente, crie um teste unitário para o TransactionService que valide o seguinte cenário:

Dado uma conta com saldo inicial de 100.00.

Quando uma transação de despesa (EXPENSE) de 30.55 for processada.

Então o saldo final da conta deve ser exatamente 69.45.

Repita o teste para uma receita (REVENUE) de 50.00, onde o saldo deve subir para 150.00."