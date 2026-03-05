# Arquitetura do Backend

## Stack

- Java 17
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- Flyway
- SpringDoc / Swagger

## Organização por camadas

```text
com.barberfind.api
├── config
├── controller
├── domain
├── dto
├── repository
├── security
├── service
└── shared/util
```

### Controller
Recebe requisições HTTP, valida DTOs e delega para services.

### Service
Contém regras de negócio, ownership e autorização contextual.

### Repository
Interfaces JPA para persistência de entidades.

### DTO
Contratos de request/response. Evitam expor entidades diretamente.

### Security
- `JwtAuthFilter`: lê token Bearer e injeta autenticação no contexto.
- `JwtProvider`: gera e valida tokens.
- `SecurityConfig`: define rotas públicas e autenticadas.

## Segurança e permissões

Há 3 níveis de proteção:

1. **Regras globais** no `SecurityFilterChain`.
2. **Autorização declarativa** com `@PreAuthorize`.
3. **Validação de ownership no service** (ex.: só usuário dono do recurso pode editar).

## Migrations

As migrations SQL ficam em `src/main/resources/db/migration` e são aplicadas no startup com Flyway.

Principais arquivos:

- `V1__init.sql`: schema inicial
- `V2__role_as_varchar.sql`: ajustes de role
- `V3__create_revoked_tokens.sql`: revogação de JWT
- `V4__user_preferences_payment_methods_terms.sql`: preferências, payment methods e aceite de termos
