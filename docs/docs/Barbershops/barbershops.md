---
sidebar_position: 2
title: Barbershops
description: Gerenciamento de barbearias no sistema.
---

# Barbershops

Endpoints responsáveis pelo **cadastro, gerenciamento e consulta de barbearias**.

Existem dois tipos de acesso:

- **Público** → listar e visualizar barbearias
- **OWNER** → criar, editar e gerenciar barbearias

---

# Base Endpoint

```
/api/barbershops
```

---

# 1️⃣ Listar barbearias públicas

Retorna todas as barbearias **ativas** disponíveis na plataforma.

### Endpoint

```
GET /api/barbershops
```

### Exemplo

```
GET http://localhost:8080/api/barbershops
```

### Response

```json
[
  {
    "id": "bs_123",
    "ownerUserId": "user_1",
    "name": "Barbearia Central",
    "cnpj": "12345678000199",
    "description": "Barbearia especializada em cortes modernos",
    "phone": "11999999999",
    "email": "contato@barbearia.com",
    "address": "Rua A, 100",
    "neighborhood": "Centro",
    "city": "São Paulo",
    "state": "SP",
    "openingTime": "09:00",
    "closingTime": "18:00",
    "active": true,
    "headquarter": true,
    "parentBarbershopId": null
  }
]
```

---

# 2️⃣ Buscar barbearia por ID

Retorna uma barbearia específica.

### Endpoint

```
GET /api/barbershops/{id}
```

### Exemplo

```
GET http://localhost:8080/api/barbershops/bs_123
```

### Response

```json
{
  "id": "bs_123",
  "ownerUserId": "user_1",
  "name": "Barbearia Central",
  "cnpj": "12345678000199",
  "description": "Barbearia especializada em cortes modernos",
  "phone": "11999999999",
  "email": "contato@barbearia.com",
  "address": "Rua A, 100",
  "neighborhood": "Centro",
  "city": "São Paulo",
  "state": "SP",
  "openingTime": "09:00",
  "closingTime": "18:00",
  "active": true,
  "headquarter": true,
  "parentBarbershopId": null
}
```

---

# 3️⃣ Criar barbearia

Cria uma nova barbearia vinculada ao **OWNER autenticado**.

### Permissão

```
OWNER
```

### Endpoint

```
POST /api/barbershops
```

### Exemplo

```
POST http://localhost:8080/api/barbershops
```

### Request Body

```json
{
  "name": "Barbearia Central",
  "cnpj": "12345678000199",
  "description": "Especializada em cortes modernos",
  "phone": "11999999999",
  "email": "contato@barbearia.com",
  "address": "Rua A, 100",
  "neighborhood": "Centro",
  "city": "São Paulo",
  "state": "SP",
  "openingTime": "09:00",
  "closingTime": "18:00",
  "isHeadquarter": true,
  "parentBarbershopId": null
}
```

### Response

```json
{
  "id": "bs_123",
  "ownerUserId": "user_1",
  "name": "Barbearia Central",
  "cnpj": "12345678000199",
  "description": "Especializada em cortes modernos",
  "phone": "11999999999",
  "email": "contato@barbearia.com",
  "address": "Rua A, 100",
  "neighborhood": "Centro",
  "city": "São Paulo",
  "state": "SP",
  "openingTime": "09:00",
  "closingTime": "18:00",
  "active": true,
  "headquarter": true,
  "parentBarbershopId": null
}
```

---

# 4️⃣ Atualizar barbearia

Atualiza informações de uma barbearia pertencente ao **OWNER**.

### Permissão

```
OWNER
```

### Endpoint

```
PUT /api/barbershops/{id}
```

### Exemplo

```
PUT http://localhost:8080/api/barbershops/bs_123
```

### Request Body

```json
{
  "name": "Barbearia Central Premium",
  "description": "Especializada em cortes premium",
  "phone": "11999999999",
  "email": "premium@barbearia.com",
  "address": "Rua A, 100",
  "neighborhood": "Centro",
  "city": "São Paulo",
  "state": "SP",
  "openingTime": "09:00",
  "closingTime": "20:00"
}
```

### Response

```json
{
  "id": "bs_123",
  "name": "Barbearia Central Premium"
}
```

---

# 5️⃣ Alterar status da barbearia

Permite **ativar ou desativar uma barbearia**.

### Permissão

```
OWNER
```

### Endpoint

```
PATCH /api/barbershops/{id}/status
```

### Exemplo

```
PATCH http://localhost:8080/api/barbershops/bs_123/status
```

### Request Body

```json
{
  "isActive": false
}
```

### Response

```json
{
  "id": "bs_123",
  "active": false
}
```

---

# 6️⃣ Listar barbearias do OWNER

Retorna todas as barbearias do **usuário autenticado**.

### Permissão

```
OWNER
```

### Endpoint

```
GET /api/owners/me/barbershops
```

### Exemplo

```
GET http://localhost:8080/api/owners/me/barbershops
```

### Response

```json
[
  {
    "id": "bs_123",
    "name": "Barbearia Central",
    "city": "São Paulo",
    "state": "SP",
    "active": true
  }
]
```

---

# Matriz e Filial

O sistema suporta **estrutura de franquia ou múltiplas unidades**.

| Campo | Descrição |
|------|------|
| `isHeadquarter` | Define se a barbearia é matriz |
| `parentBarbershopId` | ID da matriz quando for filial |

### Regras

- **Matriz**
    - `isHeadquarter = true`
    - `parentBarbershopId = null`

- **Filial**
    - `isHeadquarter = false`
    - `parentBarbershopId` deve conter o ID da matriz

---

# Status HTTP

| Código | Descrição |
|------|------|
| 200 | Sucesso |
| 201 | Criado |
| 400 | Dados inválidos |
| 401 | Não autenticado |
| 403 | Sem permissão |
| 404 | Barbearia não encontrada |
