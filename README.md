# BarberFind API

API REST do ecossistema **BarberFind** (mobile + web administrativo), construída com Spring Boot e PostgreSQL para gestão de usuários, barbearias, barbeiros, serviços e agendamentos.

## Sobre o projeto

O BarberFind conecta clientes, barbeiros e donos de barbearia em uma plataforma única.

- Clientes podem se cadastrar, autenticar, buscar barbearias, agendar horários e gerenciar seus dados.
- Barbeiros podem se cadastrar, solicitar vínculo a barbearias e operar agenda de atendimentos.
- Donos podem cadastrar e gerenciar barbearias, serviços, fotos e equipe de barbeiros.

> **Status atual da API:** esta base já possui autenticação, gestão de usuários, barbearias, serviços, fotos, vínculo barbeiro-barbearia e fluxo principal de agendamentos. Algumas capacidades do domínio (reviews, favorites, vendas, notificações etc.) ainda não possuem controllers expostos.

## Tecnologias

- Spring Boot
- PostgreSQL
- Flyway
- Swagger (SpringDoc OpenAPI)
- JWT
- Spring Security
- Spring Data JPA
- Maven
- Java 17

## Arquitetura

Estrutura principal do backend:

```text
src/main/java/com/barberfind/api
├── config
├── controller
├── domain
├── dto
├── repository
├── security
├── service
└── shared/util
```

### Camadas

- **controller**: exposição das rotas HTTP.
- **service**: regras de negócio e autorização contextual (além da segurança declarativa).
- **repository**: persistência com Spring Data JPA.
- **domain**: entidades JPA e enums do domínio.
- **dto**: contratos de entrada e saída da API.
- **security/config**: autenticação JWT, filtro de segurança e regras de acesso.
- **db/migration**: versionamento do schema com Flyway.

## Banco de Dados

O banco é PostgreSQL com migrations Flyway.

- IDs em padrão CUID (`varchar(40)`) gerados na aplicação.
- Role de usuário com enum: `CLIENT`, `BARBER`, `OWNER`.
- Entidades centrais: `users`, `barbershops`, `barbers`, `services`, `appointments`, `user_payment_methods`, `user_documents`, `barbershop_barbers`, `barbershop_services`, entre outras.

### Relacionamentos relevantes

- `users (OWNER)` 1:N `barbershops`
- `users (BARBER)` 1:1 `barbers`
- `barbershops` N:N `barbers` via `barbershop_barbers`
- `barbershops` N:N `services` via `barbershop_services`
- `appointments` vincula `user`, `barbershop`, `barber`, `service`
- `user_documents` e `user_payment_methods` pertencem ao usuário

## Como rodar o projeto

### 1) Pré-requisitos

- Java 17+
- Maven 3.9+
- PostgreSQL 14+

### 2) Banco local

```sql
CREATE DATABASE barberdb;
```

### 3) Configurar ambiente

Copie o arquivo de exemplo:

```bash
cp .env.example .env
```

### 4) Build e execução

```bash
./mvnw clean install
./mvnw spring-boot:run
```

API disponível em:

- `http://localhost:8080`

## Variáveis de ambiente

Principais variáveis suportadas:

- `SERVER_PORT`
- `SPRING_PROFILES_ACTIVE`
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `JPA_DDL_AUTO`, `JPA_SHOW_SQL`
- `SWAGGER_ENABLED`
- `jwt.secret`
- `jwt.expiration-ms`
- `CORS_ALLOWED_ORIGINS`

> Observação: no `application.properties`, as chaves de JWT usadas pela aplicação são `jwt.secret` e `jwt.expiration-ms`.

## Documentação da API

### Swagger

Após subir a aplicação:

- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Docusaurus

Foi adicionada uma estrutura inicial em `docs/` com páginas técnicas e de endpoints.

## Rotas principais (resumo)

- **Auth**: login, logout e registro de client/barber/owner
- **Users**: atualização de perfil, senha, preferências, termos, documentos e métodos de pagamento
- **Barbershops**: listagem pública, detalhe público, criação/edição/status (OWNER)
- **Services**: catálogo global e vínculo de serviços por barbearia
- **Barbers**: vínculo barbeiro-barbearia (solicitar, aprovar, remover) + dados agregados (serviços, disponibilidade e portfólio)
- **Appointments**: criação, listagens e transições de status (confirmar, concluir, no-show, cancelar)

Documentação detalhada por endpoint está nos arquivos de `docs/api`.

## Autenticação e autorização

- JWT via header `Authorization: Bearer <token>`.
- Claims com `sub` (userId) e `role`.
- Roles suportadas: `CLIENT`, `BARBER`, `OWNER`.
- Segurança combina:
  - regras globais do `SecurityFilterChain`
  - `@PreAuthorize` em endpoints específicos
  - validações de ownership/papel dentro dos services.

## Migrations

As migrations ficam em:

```text
src/main/resources/db/migration
```

Para aplicar automaticamente no startup:

- `spring.flyway.enabled=true`

## Testes

- Testes manuais via **Swagger** e **Postman**.
- Testes automatizados estão em evolução conforme expansão das rotas.
- Execução dos testes atuais:

```bash
./mvnw test
```

---

Para documentação completa de arquitetura e endpoints, consulte a pasta `docs/`.
