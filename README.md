# Workout API

API Java Spring Boot para registrar e consultar atividades de treino.

A aplicacao usa MongoDB para salvar as atividades e JWT para identificar o employee autenticado. Nas rotas de usuario autenticado, o `employeeId` vem da claim `employeeId` dentro do token JWT.

## Requisitos

- Java 17
- Maven Wrapper, ja incluido no projeto
- MongoDB local ou MongoDB Atlas
- Um JWT valido contendo a claim `employeeId`

## Configuracao

O arquivo local `src/main/resources/application.properties` nao deve ser commitado.

Use `src/main/resources/application-example.properties` como referencia:

```properties
spring.application.name=workout-api
spring.data.mongodb.uri=${MONGO_URI}
mongodb.collections.activities=${MONGODB_ACTIVITY_COLLECTION}
mongodb.collections.employees=${MONGODB_EMPLOYEES_COLLECTION}
jwt.secret=${JWT_SECRET}
jwt.expiration-hours=2
```

Variaveis obrigatorias:

| Variavel | Descricao |
| --- | --- |
| `MONGO_URI` | URI de conexao do MongoDB |
| `MONGODB_ACTIVITY_COLLECTION` | Nome da collection de atividades |
| `MONGODB_EMPLOYEES_COLLECTION` | Nome da collection de employees |
| `JWT_SECRET` | Secret usado pelo servico de autenticacao |

Exemplo para executar localmente no PowerShell:

```powershell
$env:MONGO_URI="mongodb://localhost:27017/workoutApplication"
$env:MONGODB_ACTIVITY_COLLECTION="activityCollection"
$env:MONGODB_EMPLOYEES_COLLECTION="employessCollection"
$env:JWT_SECRET="local-dev-secret"
```

Exemplo para Linux/macOS:

```bash
export MONGO_URI="mongodb://localhost:27017/workoutApplication"
export MONGODB_ACTIVITY_COLLECTION="activityCollection"
export MONGODB_EMPLOYEES_COLLECTION="employessCollection"
export JWT_SECRET="local-dev-secret"
```

## Como Executar

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

A API ficara disponivel em:

```text
http://localhost:8080
```

Para verificar se a aplicacao subiu:

```http
GET http://localhost:8080/v1/activities/healthCheck
```

Resposta esperada:

```text
running
```

## Autenticacao

As rotas protegidas precisam receber o header:

```http
Authorization: Bearer <seu-token-jwt>
```

O token precisa conter a claim `employeeId`:

```json
{
  "employeeId": "6a0dc4717b55af25df34b38c"
}
```

## Exemplos de Uso

### Criar atividade

```http
POST http://localhost:8080/v1/activities
Authorization: Bearer <seu-token-jwt>
Content-Type: application/json
```

Body:

```json
{
  "activityCode": "RUN",
  "activityDescription": "Corrida de 5km"
}
```

Resposta esperada:

```json
{
  "id": "activity-id",
  "employeeId": "6a0dc4717b55af25df34b38c",
  "activityDateTime": "2026-05-20T21:40:00",
  "activityCode": "RUN",
  "activityDescription": "Corrida de 5km"
}
```

Exemplo com `curl`:

```bash
curl -X POST "http://localhost:8080/v1/activities" \
  -H "Authorization: Bearer <seu-token-jwt>" \
  -H "Content-Type: application/json" \
  -d '{"activityCode":"RUN","activityDescription":"Corrida de 5km"}'
```

### Listar atividades do employee autenticado

```http
GET http://localhost:8080/v1/activities/me
Authorization: Bearer <seu-token-jwt>
```

Resposta esperada:

```json
[
  {
    "id": "activity-id",
    "employeeId": "6a0dc4717b55af25df34b38c",
    "activityDateTime": "2026-05-20T21:40:00",
    "activityCode": "RUN",
    "activityDescription": "Corrida de 5km"
  }
]
```

Exemplo com `curl`:

```bash
curl "http://localhost:8080/v1/activities/me" \
  -H "Authorization: Bearer <seu-token-jwt>"
```

### Listar todas as atividades

```http
GET http://localhost:8080/v1/activities
Authorization: Bearer <seu-token-jwt>
```

Exemplo com `curl`:

```bash
curl "http://localhost:8080/v1/activities" \
  -H "Authorization: Bearer <seu-token-jwt>"
```

### Buscar atividade por id

```http
GET http://localhost:8080/v1/activities/{id}
Authorization: Bearer <seu-token-jwt>
```

Exemplo:

```bash
curl "http://localhost:8080/v1/activities/activity-id" \
  -H "Authorization: Bearer <seu-token-jwt>"
```

## Respostas de Erro

Os erros seguem o formato padronizado:

```json
{
  "timestamp": "2026-05-20T21:40:00",
  "status": 404,
  "error": "Not Found",
  "code": "EMPLOYEE_NOT_FOUND",
  "message": "Employee not found with id: 6a0dc4717b55af25df34b38c",
  "path": "/v1/activities/me",
  "fields": null
}
```

## Testes

No Windows:

```powershell
.\mvnw.cmd test
```

No Linux/macOS:

```bash
./mvnw test
```

## Seguranca

Nao commite `application.properties` com credenciais reais.

Use variaveis de ambiente localmente e secrets na CI/CD ou na plataforma de deploy.
