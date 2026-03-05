# API - Barbearias

## GET `/api/barbershops`

Lista barbearias ativas (público).

### Filtros importantes

No estado atual do backend, a listagem pública **não possui query params de filtro** por região/nota/especialidade.
Esses filtros ainda dependem de evolução de endpoint/repository.

## GET `/api/barbershops/{id}`

Retorna detalhe público de barbearia ativa.

## POST `/api/barbershops`

Cria barbearia para o owner autenticado.

- **Permissão:** `OWNER`
- **Body:** dados cadastrais + flags de matriz/filial (`isHeadquarter`, `parentBarbershopId`)

## PUT `/api/barbershops/{id}`

Atualiza barbearia do owner.

- **Permissão:** `OWNER`

## PATCH `/api/barbershops/{id}/status`

Ativa/desativa barbearia.

- **Permissão:** `OWNER`
- **Body:** `isActive`

## GET `/api/owners/me/barbershops`

Lista barbearias do owner autenticado.

- **Permissão:** `OWNER`

## Fotos da barbearia

### GET `/api/barbershops/{barbershopId}/photos`
Público. Lista fotos.

### POST `/api/barbershops/{barbershopId}/photos`
Owner adiciona foto em Base64.

- **Body:** `imageData`, `mediaType`

### DELETE `/api/barbershops/{barbershopId}/photos/{photoId}`
Owner remove foto.
