# API - Usuários

Base: `/api/users/{userId}`.

> Todas as rotas exigem autenticação e ownership (usuário só manipula os próprios dados).

## PATCH `/api/users/{userId}`

Atualiza dados pessoais (e campos de barbeiro quando role = BARBER).

- **Permissão:** usuário autenticado dono do `userId`
- **Body:** campos opcionais (`name`, `phone`, `birthDate`, `hairType`, `hairTexture`, `hasBeard`, `bio`, `yearsExperience`)
- **Response:** `200 {"message":"updated"}`

## PUT `/api/users/{userId}/password`

Troca senha.

- **Body:** `currentPassword`, `newPassword`
- **Response:** `200 {"message":"password_updated"}`

## Payment Methods

### GET `/api/users/{userId}/payment-methods`
Lista métodos de pagamento tokenizados do usuário.

### POST `/api/users/{userId}/payment-methods`
Adiciona método de pagamento.

- **Body:** `provider`, `providerCustomerId`, `cardType`, `lastFourDigits`, `brand`
- **Response:** `201` com método salvo

### DELETE `/api/users/{userId}/payment-methods/{methodId}`
Remove método de pagamento.

- **Response:** `204`

## Preferences

### GET `/api/users/{userId}/preferences`
Retorna preferências (theme/language).

### PUT `/api/users/{userId}/preferences`
Atualiza preferências.

- **Body:** `theme`, `language`

## POST `/api/users/{userId}/accept-terms`

Registra aceite de versão de termos.

- **Body:** `termsVersion`, `accepted=true`
- **Response:** `200` com `message=terms_accepted`

## Documents

### GET `/api/users/{userId}/documents`
Lista documentos do usuário.

### POST `/api/users/{userId}/documents`
Adiciona documento.

- **Body:** `documentType`, `documentNumber`

### PUT `/api/users/{userId}/documents/{documentId}`
Atualiza documento.
