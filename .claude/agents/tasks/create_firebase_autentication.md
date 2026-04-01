Task: Implementação de Autenticação Social via Firebase Admin SDK
Título: Integração de Identity Provider (Firebase) com Sincronização de Usuários (Shadow User).
Prioridade: Alta.
Contexto: Substituir ou complementar a autenticação local por login via Google, mantendo a integridade do user_id (UUID) no MySQL para todas as transações.

1. Objetivo Técnico
Validar o ID Token do Firebase enviado pelo Frontend, extrair as informações do usuário e garantir que ele possua um registro correspondente na nossa base de dados local (users), estabelecendo o contexto de segurança para as demais chamadas da API.

2. Fluxo de Execução (Step-by-Step)
A. Camada de Infraestrutura (External Integration)
Dependência: Adicionar com.google.firebase:firebase-admin:9.2.0 ao pom.xml.

Configuração: Criar um FirebaseConfig que inicialize o FirebaseApp usando as credenciais do Service Account (carregadas via variável de ambiente ou arquivo JSON).

Provider: Implementar a interface de domínio TokenVerifier utilizando FirebaseAuth.getInstance().verifyIdToken(token).

B. Camada de Aplicação (Use Case)
Use Case AuthenticateGoogleUser:

Recebe o idToken do Controller.

Chama o TokenVerifier para validar a assinatura e expiração.

Busca o usuário no UserRepository pelo e-mail retornado pelo Firebase.

Lógica Shadow User: Se o usuário não existir, cria um novo User (UUID, Name, Email) no MySQL.

Gera um JWT interno da aplicação (opcional) ou define o SecurityContext com o UUID local.

C. Camada de Apresentação (Web/API)
Endpoint: POST /api/v1/auth/google.

Payload: { "idToken": "string_longa_do_firebase" }.

3. Instrução Minuciosa para o Agente (Antigravity)
"Agente, implemente a integração com Firebase seguindo estes padrões de Engenharia Sênior:

Desacoplamento: A classe FirebaseToken do SDK não deve sair da camada de Infraestrutura. Converta-a para um Record de domínio SocialProfile.

Tratamento de Erros: Se o token for inválido ou expirado, retorne 401 Unauthorized. Capture FirebaseAuthException e trate-a no Global Exception Handler.

Atomicidade: A criação do novo usuário no MySQL deve ser transacional.

Segurança e Contexto: Após validar o token do Firebase, você deve garantir que o SecurityContextHolder do Spring contenha o nosso user_id (UUID) interno, e não o UID do Firebase, para manter a compatibilidade com as tabelas de Transactions e Accounts.

Feature Toggle: Mantenha suporte ao app.security.enabled. Se for false, ignore a validação do Firebase e use o usuário de teste.

Teste Unitário: Mocke o FirebaseAuth para testar o Use Case sem precisar de uma conexão real com o Google."

4. Critérios de Aceite (DoD)
Primeiro Acesso: Ao enviar um token de um e-mail novo, um registro é criado na tabela users do MySQL.

Acessos Seguintes: O sistema reconhece o e-mail e apenas retorna os dados do usuário existente.

Proteção de Rotas: Tentar acessar /api/v1/dashboard/summary com um token inválido do Firebase deve resultar em 403 Forbidden ou 401 Unauthorized.

Swagger: O endpoint de autenticação Google está visível e documentado.