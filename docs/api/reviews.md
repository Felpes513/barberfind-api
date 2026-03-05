# API - Avaliações (Reviews)

No estado atual do backend, **não há controller/rota HTTP exposta** para CRUD de avaliações,
embora exista a tabela `reviews` no schema.

## Implicação

- Não há endpoint para criar, listar, atualizar ou remover avaliações.
- A funcionalidade está modelada no banco, mas ainda não está publicada na camada REST.
