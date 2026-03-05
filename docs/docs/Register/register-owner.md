---
sidebar_position: 3
title: Register Owner
description: Endpoint responsável pelo cadastro de donos de barbearia.
---

# Register Owner

Cria um novo **usuário com role OWNER**.

Esse usuário poderá posteriormente **criar e gerenciar barbearias**.

---

# Endpoint

```
POST /api/auth/register/owner
```

---

# Request Body

```json
{
  "name": "Carlos Silva",
  "email": "carlos@barberfind.com",
  "password": "123456",
  "phone": "11988888888"
}
```

---

# Campos

| Campo | Tipo | Obrigatório | Descrição |
|------|------|------|------|
| name | String | Sim | Nome |
| email | String | Sim | Email |
| password | String | Sim | Senha |
| phone | String | Não | Telefone |

---

# Response

```json
{
  "message": "registered",
  "data": {
    "userId": "usr_xxx",
    "role": "OWNER",
    "email": "carlos@barberfind.com"
  }
}
```

---

# Status possíveis

| Código | Descrição |
|------|------|
| 201 | Owner registrado |
| 400 | Dados inválidos |
| 409 | Email ou telefone já em uso |

---

# Observações

- O usuário criado recebe a role **OWNER**
- Owners podem criar barbearias posteriormente
- Senha é armazenada usando **PasswordEncoder**