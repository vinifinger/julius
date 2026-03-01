Task: Implementação de Segurança com JWT e Feature Toggle
Título: Setup de Autenticação Stateless (JWT) com Chave de Desativação em Runtime.
Prioridade: Alta.
Contexto: Proteger os recursos do sistema, permitindo o isolamento de dados por user_id, mas com suporte a bypass para testes locais.

1. Configuração do Feature Toggle (application.properties)
O agente deve considerar a seguinte propriedade:
app.security.enabled=true (Default: true).

2. Especificação Técnica da Abordagem
A. Camada de Infraestrutura (Security Config)
Conditional Security: A classe SecurityConfig deve utilizar a anotação @ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true).

Fallback Config: Se app.security.enabled=false, o sistema deve configurar um SecurityFilterChain que permite permitAll() em todas as rotas e injeta um "Mock User" no contexto para que os Use Cases não quebrem ao tentar ler o user_id.

B. Fluxo de Autenticação (JWT)
Endpoints: * POST /api/v1/auth/register: Cria o UserEntity com senha encriptada pelo BCrypt.

POST /api/v1/auth/login: Valida credenciais e retorna o Token JWT (expiração de 24h).

Provider: Utilizar a biblioteca java-jwt (Auth0) ou jjwt.

C. Camada de Domínio & Aplicação
Interface UserContext: Criar uma interface no domínio para recuperar o ID do usuário logado.

Implementação: A implementação na infraestrutura lerá do SecurityContextHolder. Se o toggle estiver desativado, retornará um UUID fixo de teste.

3. Instrução Detalhada para o Agente (Antigravity)
"Agente, implemente o módulo de segurança seguindo estas diretrizes:

Dependências: Adicione spring-boot-starter-security e uma biblioteca de JWT ao pom.xml.

Feature Toggle: Crie uma configuração que verifique a propriedade app.security.enabled.

Se true: Aplique filtros JWT, exija Header 'Authorization' e valide o segredo.

Se false: Desabilite o CSRF e permita todas as requisições (.anyRequest().permitAll()).

Password Storage: Nunca salve senhas em texto plano. Use BCryptPasswordEncoder.

JWT Payload: O token deve conter o sub (email) e uma Claim customizada userId (UUID).

Mock User: Se a segurança estiver desativada, garanta que o sistema ainda consiga simular um usuário logado para que as rotas de Transações (que dependem de um user_id) continuem funcionando nos testes.

Teste: Crie um teste unitário que valide se a geração do token contém as informações corretas e um teste de integração que tente acessar /api/v1/transactions sem token (deve retornar 401 se o toggle for true)."

4. Critérios de Aceite (DoD)
O login retorna um token válido que pode ser decodificado (ex: via jwt.io).

Ao mudar app.security.enabled para false e reiniciar, os endpoints respondem sem necessidade de Header de Autorização.

As senhas no banco de dados (MySQL) aparecem encriptadas (começando com $2a$).

O Swagger UI foi atualizado para incluir o botão "Authorize" (Security Definition) para testar os endpoints protegidos