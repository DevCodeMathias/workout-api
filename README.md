# Workout API

API Java Spring Boot para registrar e consultar atividades de treino.

A aplicacao usa MongoDB para salvar atividades e Spring Security com JWT para autenticar employees. O `employeeId` usado nas rotas protegidas vem do token JWT validado, nao do body da requisicao.

## Requisitos

- Java 17
- Maven Wrapper incluido no projeto
- MongoDB local ou MongoDB Atlas
- JWT assinado com o mesmo secret configurado na API

## Configuracao

O arquivo local `src/main/resources/application.properties` nao deve ser commitado.

Use `src/main/resources/application-example.properties` como referencia:

```properties
spring.application.name=workout-api
spring.data.mongodb.uri=${MONGO_URI}
mongodb.collections.activities=${MONGODB_ACTIVITY_COLLECTION}
mongodb.collections.employees=${MONGODB_EMPLOYEES_COLLECTION}
security.jwt.secret=${JWT_SECRET}
security.jwt.expiration-hours=2
```

Variaveis obrigatorias:

| Variavel | Descricao |
| --- | --- |
| `MONGO_URI` | URI de conexao do MongoDB |
| `MONGODB_ACTIVITY_COLLECTION` | Nome da collection de atividades |
| `MONGODB_EMPLOYEES_COLLECTION` | Nome da collection de employees |
| `JWT_SECRET` | Secret usado para validar a assinatura do JWT |

Exemplo no PowerShell:

```powershell
$env:MONGO_URI="mongodb://localhost:27017/workoutApplication"
$env:MONGODB_ACTIVITY_COLLECTION="activityCollection"
$env:MONGODB_EMPLOYEES_COLLECTION="employessCollection"
$env:JWT_SECRET="local-dev-secret-with-at-least-32-chars"
```

## Como Executar

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

A API ficara disponivel em:

```text
http://localhost:8080
```

Health check publico:

```http
GET http://localhost:8080/api/v1/activities/healthCheck
```

Resposta:

```text
running
```

## Autenticacao

Todas as rotas, exceto o health check, exigem:

```http
Authorization: Bearer <jwt>
```

O token precisa:

- estar assinado com `security.jwt.secret`
- nao estar expirado
- conter `employeeId`
- conter `authorizationType` com valor `EMPLOYEE`

Payload esperado:

```json
{
  "employeeId": "6a0dc4717b55af25df34b38c",
  "authorizationType": "EMPLOYEE"
}
```

Depois que o filtro valida o token, o controller recebe o employee autenticado via `@AuthenticationPrincipal`.

## Rotas

### Criar atividade

```http
POST http://localhost:8080/api/v1/activities
Authorization: Bearer <jwt>
Content-Type: application/json
```

Body:

```json
{
  "activityCode": "RUN",
  "activityDescription": "Corrida de 5km"
}
```

Exemplo:

```bash
curl -X POST "http://localhost:8080/api/v1/activities" \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{"activityCode":"RUN","activityDescription":"Corrida de 5km"}'
```

### Listar minhas atividades

```http
GET http://localhost:8080/api/v1/activities/me
Authorization: Bearer <jwt>
```

### Listar atividades por employee

Compara o `employeeId` do path com o `employeeId` do token.

```http
GET http://localhost:8080/api/v1/activities/employees/{employeeId}
Authorization: Bearer <jwt>
```

Se forem diferentes, retorna `TOKEN_EMPLOYEE_MISMATCH`.

### Listar todas as atividades

```http
GET http://localhost:8080/api/v1/activities
Authorization: Bearer <jwt>
```

Mesmo nessa rota, o service verifica se o employee do token existe no banco.

### Buscar atividade por id

```http
GET http://localhost:8080/api/v1/activities/{id}
Authorization: Bearer <jwt>
```

## Erros

Formato padronizado:

```json
{
  "timestamp": "2026-05-21T19:00:00",
  "status": 401,
  "error": "Unauthorized",
  "code": "INVALID_TOKEN",
  "message": "JWT is invalid",
  "path": "/api/v1/activities",
  "fields": null
}
```

## Testes

Windows:

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

## Seguranca

Nao commite `application.properties` com credenciais reais.

Use variaveis de ambiente localmente e secrets na CI/CD ou na plataforma de deploy.
