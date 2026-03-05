# Guia de Desenvolvimento

## Fluxo recomendado

1. Rodar migrations automaticamente via Flyway no startup.
2. Implementar domínio -> repository -> service -> controller.
3. Criar/ajustar DTOs para contratos de API.
4. Validar endpoints no Swagger e Postman.

## Convenções importantes

- IDs em CUID gerados pela aplicação.
- Regras de autorização devem combinar:
  - Spring Security (`@PreAuthorize` quando necessário)
  - validações de ownership dentro do service.
- Evitar expor entidades JPA diretamente nos controllers.

## Funcionalidades ainda em aberto

Tabelas existentes no schema sem rotas dedicadas na API atual:

- reviews
- favorites
- products
- sales
- subscriptions
- notifications

Essas capacidades podem ser adicionadas incrementamente mantendo o padrão arquitetural já existente.
