# API - Autenticação

## POST `/api/auth/register/client`

Cria um usuário com role `CLIENT`.

- **Permissão:** pública
- **Body:** `name`, `email`, `password`, `phone`, campos opcionais de perfil
- **Response:** `201` com `message=registered` e dados básicos do usuário

## POST `/api/auth/register/barber`

Cria usuário `BARBER` e perfil profissional em `barbers`.

- **Permissão:** pública
- **Body:** `name`, `email`, `password`, `phone`, `bio`, `yearsExperience`
- **Response:** `201` com `userId`, `barberId`, `name`, `email`

## POST `/api/auth/register/owner`

Cria usuário com role `OWNER`.

- **Permissão:** pública
- **Body:** `name`, `email`, `password`, `phone`
- **Response:** `201` com dados do owner criado

## POST `/api/auth/login`

Autentica usuário e retorna JWT.

- **Permissão:** pública
- **Body:** `email`, `password`
- **Response:** `200` com `token`, `userId`, `role`, `name`, `email`

## POST `/api/auth/logout`

Revoga o token atual (blacklist em `revoked_tokens`).

- **Permissão:** autenticado
- **Header:** `Authorization: Bearer <token>`
- **Response:** `204 No Content`
