# Credit Limit API

API RESTful para gerenciamento de limites de crédito de clientes, desenvolvida com Spring Boot 4 e SQL Server.

---

## Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Decisões Técnicas](#decisões-técnicas)
- [Regras de Negócio](#regras-de-negócio)
- [Endpoints](#endpoints)
- [Tratamento de Erros](#tratamento-de-erros)
- [Como Executar](#como-executar)

---

## Visão Geral

Esta API foi desenvolvida como parte de um teste técnico para a SuitPay. Ela gerencia limites de crédito de clientes com histórico completo de alterações, autenticação, autorização e aplicação de regras de negócio.

---

## Tecnologias

| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| Java | 21 | Linguagem de programação |
| Spring Boot | 4.0.6 | Framework da aplicação |
| Spring Security | 7.x | Autenticação e autorização |
| Spring Data JPA | 4.x | Camada de acesso a dados |
| Hibernate | 7.x | ORM |
| SQL Server | 2022 | Banco de dados |
| Docker | - | Container do banco de dados |
| Gradle | Wrapper | Gerenciador de dependências |
| Lombok | - | Redução de boilerplate |

---

## Arquitetura

O projeto segue o padrão de arquitetura em camadas organizado por responsabilidade:

```
src/main/java/com/api/creditlimit/
├── config/          # Spring Security e inicialização de dados
├── controller/      # Endpoints REST
├── domain/          # Entidades JPA
│   └── enums/       # Enumerações
├── dto/             # Records de Request e Response
├── exception/       # Exceptions customizadas e handler global
├── repository/      # Interfaces Spring Data JPA
└── service/         # Regras de negócio
```

---

## Decisões Técnicas

### Records como DTOs
Records do Java foram escolhidos para todos os DTOs por serem imutáveis por natureza, terem `equals`, `hashCode` e `toString` gerados automaticamente e serem ideais para objetos de transferência de dados sem comportamento.

### Exceptions customizadas por regra de negócio
Cada violação de regra de negócio tem sua própria classe de exception (`CustomerNotFoundException`, `NegativeCreditLimitException`, `VipMinimumLimitException`). Isso torna o tratamento de erros explícito, legível e fácil de estender.

### RFC 7807 — Problem Details
Todas as respostas de erro seguem o padrão RFC 7807 via `ProblemDetail` do Spring. Isso fornece um formato de erro consistente e legível por máquina em todos os endpoints.

### AppUser implementando UserDetails
A entidade `AppUser` implementa a interface `UserDetails` do Spring Security diretamente. Isso elimina a necessidade de conversão entre objetos de domínio e de segurança, permitindo que o `@AuthenticationPrincipal` injete a entidade diretamente nos controllers.

### BCrypt para hash de senhas
BCrypt foi escolhido para hash de senhas por ser um algoritmo unidirecional com salt embutido, resistente a ataques de rainbow table. Não existe descriptografia — a verificação é feita comparando hashes.

### Injeção via construtor
Todos os beans Spring utilizam injeção por construtor em vez de `@Autowired` no campo. Isso torna as dependências explícitas, permite instanciar a classe sem o Spring (facilita testes unitários) e garante que o objeto nunca esteja em estado inválido.

### BigDecimal para valores monetários
`double` e `float` usam representação binária de ponto flutuante, o que causa erros de arredondamento em cálculos financeiros. `BigDecimal` fornece precisão decimal exata e é o padrão da indústria para valores monetários.

### FetchType.LAZY nos relacionamentos
Todos os relacionamentos `@ManyToOne` utilizam `LAZY` para evitar queries desnecessárias. As entidades relacionadas só são carregadas quando explicitamente acessadas, melhorando a performance em alto volume de leitura.

### Limite mínimo VIP via application.properties
O limite mínimo de crédito para clientes VIP é configurado via `@Value("${credit.limit.vip.minimum}")` no `application.properties`. Isso evita números mágicos no código e permite que o valor seja alterado por ambiente sem recompilação.

### Seed via data.sql com MERGE
O seed dos dados iniciais é feito via `data.sql` em `src/main/resources`, executado automaticamente pelo Spring Boot na inicialização. A sintaxe `MERGE` do T-SQL foi escolhida por ser um statement único — compatível com o executor de scripts do Spring Boot — que insere os dados apenas quando o registro ainda não existe, evitando duplicações a cada reinicialização. As senhas dos usuários são armazenadas como hashes BCrypt pré-computados.

---

## Regras de Negócio

| Regra | Detalhe |
|-------|---------|
| Limite não negativo | Limites de crédito não podem ser negativos |
| Somente admin atualiza | Apenas usuários com role `CREDIT_LIMIT_ADMIN` podem atualizar limites |
| Limite mínimo VIP | Clientes VIP (`isVip = true`) devem manter um limite mínimo definido na configuração |
| Histórico completo | Toda alteração de limite é registrada com o usuário responsável e o timestamp |

---

## Endpoints

### URL Base
```
http://localhost:8080/api
```

### Autenticação
Todos os endpoints requerem Basic Auth.

| Usuário | Senha | Role |
|---------|-------|------|
| `admin` | `admin123` | `CREDIT_LIMIT_ADMIN` |
| `viewer` | `viewer123` | `CREDIT_LIMIT_VIEWER` |

---

### Consultar Limite de Crédito

```
GET /api/customers/{id}/credit-limit
Autenticacao: Qualquer usuario autenticado
```

Resposta 200:
```json
{
    "customerId": 1,
    "customerName": "John Doe",
    "isVip": false,
    "creditLimit": 5000.00
}
```

---

### Atualizar Limite de Crédito

```
PUT /api/customers/{id}/credit-limit
Autenticacao: Somente CREDIT_LIMIT_ADMIN
Content-Type: application/json
```

Requisição:
```json
{
    "newLimit": 8000.00
}
```

Resposta 200:
```json
{
    "customerId": 1,
    "customerName": "John Doe",
    "previousLimit": 5000.00,
    "newLimit": 8000.00
}
```

---

### Histórico de Alterações (Paginado)

```
GET /api/customers/{id}/credit-limit/history?page=0&size=10&sort=changedAt,desc
Autenticacao: Qualquer usuario autenticado
```

Resposta 200:
```json
{
    "content": [
        {
            "id": 1,
            "previousLimit": 5000.00,
            "newLimit": 8000.00,
            "changedBy": "admin",
            "changedAt": "2026-04-26T19:00:00"
        }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
}
```

---

## Tratamento de Erros

Todos os erros seguem o padrão RFC 7807 Problem Details:

| Status | Tipo | Quando ocorre |
|--------|------|---------------|
| `400` | `/errors/validation-error` | Campo obrigatório ausente ou formato inválido |
| `401` | - | Credenciais ausentes ou inválidas |
| `403` | `/errors/access-denied` | Permissão insuficiente |
| `404` | `/errors/customer-not-found` | Cliente não encontrado |
| `422` | `/errors/negative-credit-limit` | Limite de crédito negativo |
| `422` | `/errors/vip-minimum-limit` | Limite VIP abaixo do mínimo |
| `500` | `/errors/internal-server-error` | Erro inesperado |

Exemplo de resposta de erro:
```json
{
    "type": "/errors/vip-minimum-limit",
    "title": "VIP Minimum Limit Violation",
    "status": 422,
    "detail": "VIP customers must have a minimum credit limit of 1000.00",
    "instance": "/api/customers/2/credit-limit",
    "timestamp": "2026-04-26T19:00:00Z"
}
```

---

## Como Executar

### Pré-requisitos

- Java 21
- Docker Desktop
- Git

---

### Configuração do Ambiente

1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd credit-limit
```

2. Configure os arquivos de ambiente

```bash
cp .env.example .env
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

3. Preencha os valores nos arquivos `.env` e `application.properties`

---

### Executando a Aplicação

1. Suba o banco de dados

```bash
docker-compose up -d
```

2. Aguarde o SQL Server inicializar (aproximadamente 30 segundos) e crie o banco

```bash
docker exec -it credit-limit-db /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "sua_senha" \
  -Q "CREATE DATABASE creditlimitdb" -No
```

3. Execute a aplicação

```bash
# Linux / Mac
./gradlew bootRun

# Windows
.\gradlew.bat bootRun
```

4. A API estará disponível em

```
http://localhost:8080
```

---

### Comandos Gradle

| Comando | Descricao |
|---------|-----------|
| `./gradlew bootRun` | Inicia a aplicação |
| `./gradlew build` | Compila e empacota em `.jar` |
| `./gradlew test` | Executa os testes |
| `./gradlew clean` | Limpa o diretório de build |
| `./gradlew clean bootRun` | Limpa e inicia a aplicação |

---

### Encerrando a Aplicação

```bash
# Para o container do banco de dados
docker-compose down

# Para e remove os volumes (reseta todos os dados)
docker-compose down -v
```