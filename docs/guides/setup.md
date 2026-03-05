# Guia de Setup

## Pré-requisitos

- Java 17+
- Maven
- PostgreSQL

## Passos

1. Criar banco `barberdb`.
2. Copiar `.env.example` para `.env`.
3. Revisar credenciais e variáveis.
4. Rodar:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

## Swagger

- `/swagger-ui/index.html`
- `/swagger-ui.html`
