Task: Documentação Estratégica e Configuração do Swagger UI
Título: Setup de Documentação Técnica (README.md) e OpenApi/Swagger.
Prioridade: Média.
Contexto: Facilitar a execução local e a exploração visual dos endpoints da API.

1. Objetivo
Centralizar todas as informações necessárias para que qualquer pessoa (ou IA) consiga clonar, configurar e testar o projeto em menos de 5 minutos, além de fornecer uma interface interativa para chamadas de API.

2. Especificação da Documentação (README.md)
O arquivo README.md na raiz do projeto deve conter minuciosamente:

Propósito do Projeto: Descrição clara de que se trata de um sistema de controle financeiro pessoal baseado em Clean Architecture e DDD.

Pré-requisitos: Versão do Java (21), MySQL (8.0) e Maven.

Configuração Local:

Como configurar as variáveis de ambiente (DB_URL, DB_USER, DB_PASS).

Como rodar as migrations do Flyway.

Comando para executar a aplicação (./mvnw spring-boot:run).

Guia de Uso da API: Link para o Swagger local e exemplos básicos de chamadas.

3. Implementação do Swagger (SpringDoc OpenAPI)
Configurar o Swagger para mapear automaticamente os endpoints do Spring Boot.

Requisitos Técnicos:

Dependência: Adicionar springdoc-openapi-starter-webmvc-ui ao pom.xml.

Configuração:

Definir o caminho customizado (ex: /api-docs e /swagger-ui.html).

Configurar metadados: Título do Projeto, Versão (v1.0.0) e Descrição.

Segurança (Opcional por enquanto): Se houver JWT, configurar o SecurityScheme no OpenAPI para permitir o envio do Token "Bearer" pela interface do Swagger.

4. Instrução Detalhada para o Agente (Antigravity)
"Agente, execute a tarefa de documentação seguindo estes passos:

Swagger: Adicione a dependência do SpringDoc OpenAPI 3 no pom.xml. Crie uma classe de configuração em infrastructure.config para customizar o título da API como 'Personal Finance API'. Garanta que todos os Controllers em presentation.controller apareçam na interface.

README.md: Crie um arquivo README.md profissional. Utilize badges, seções claras de 'Como Rodar' e inclua um diagrama simples da arquitetura do projeto. Descreva que o projeto utiliza Clean Architecture para garantir o desacoplamento.

Exemplos: No README, inclua um exemplo de curl para criar um usuário e uma transação, facilitando o teste rápido via terminal."

5. Critérios de Aceite (DoD)
A aplicação sobe e o endpoint /swagger-ui/index.html carrega a interface gráfica com todos os métodos (GET, POST, etc) visíveis.

O arquivo README.md é legível, bem formatado em Markdown e contém os comandos de execução.

O Swagger permite realizar uma chamada de teste com sucesso para o endpoint de Health Check ou Transactions.