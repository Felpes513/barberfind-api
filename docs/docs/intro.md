---
sidebar_position: 1
---

# Introdução

Bem-vindo à documentação técnica da **BarberFind API** — a API REST do ecossistema BarberFind, construída com **Spring Boot** e **PostgreSQL**.

## O que é o BarberFind?

O BarberFind conecta clientes, barbeiros e donos de barbearia em uma plataforma única.

- **Clientes** podem se cadastrar, autenticar, buscar barbearias, agendar horários e gerenciar seus dados.
- **Barbeiros** podem se cadastrar, solicitar vínculo a barbearias e operar agenda de atendimentos.
- **Donos** podem cadastrar e gerenciar barbearias, serviços, fotos e equipe de barbeiros.

## Sobre esta documentação

Esta documentação descreve todos os endpoints disponíveis da API, organizados por domínio. Para cada rota você encontrará:

- Método e caminho HTTP
- Headers necessários (incluindo autenticação)
- Body da requisição com exemplos em JSON
- Resposta esperada com exemplos em JSON
- Regras de validação aplicadas
- Erros possíveis com seus códigos e mensagens

## Autenticação

A API utiliza **JWT** via header `Authorization: Bearer <token>`. O token é obtido no endpoint de login e deve ser enviado em todas as requisições protegidas.

As roles suportadas são `CLIENT`, `BARBER` e `OWNER` — cada endpoint indica qual nível de acesso é necessário.

## Endpoints disponíveis

| Seção | Descrição |
|-------|-----------|
| **Auth** | Login e logout |
| **Register** | Cadastro de clientes, barbeiros e donos |
| **Users** | Dados pessoais, senha, preferências, termos, documentos e métodos de pagamento |
| **Barbershops** | Listagem pública, detalhes, criação e gerenciamento (OWNER) |
| **Services** | Catálogo global de serviços e vínculos por barbearia |
| **Favorites** | Gerenciamento de barbeiros e barbearias favoritos |
| **Appointments** | Criação, listagens e transições de status de agendamentos |

## Tecnologias

- **Java 17** + **Spring Boot**
- **PostgreSQL** + **Flyway**
- **Spring Security** + **JWT**
- **Spring Data JPA**
- **Swagger** (SpringDoc OpenAPI)

## Rodando localmente

**Pré-requisitos:** Java 17+, Maven 3.9+, PostgreSQL 14+

```sql
CREATE DATABASE barberdb;
```

```bash
cp .env.example .env
./mvnw clean install
./mvnw spring-boot:run
```

API disponível em `http://localhost:8080`.

Também é possível explorar os endpoints interativamente pelo Swagger em `http://localhost:8080/swagger-ui/index.html`.