---
sidebar_position: 1
title: Register Client
description: Endpoint responsável pelo cadastro de clientes.
---

# Register Client

Cria um novo **cliente** na plataforma BarberFind.

### Endpoint

```
POST /api/auth/register/client
```

---

# Request Body

```json
{
  "name": "Felipe Moreira",
  "email": "felipe@email.com",
  "password": "123456",
  "phone": "11999999999"
}
```

---

# Campos

| Campo | Tipo | Obrigatório | Descrição |
|------|------|------|------|
| name | String | Sim | Nome do usuário |
| email | String | Sim | Email do usuário |
| password | String | Sim | Senha da conta |
| phone | String | Não | Telefone |

---

# Response

```json
{
  "message": "registered",
  "data": {
    "id": "usr_xxx",
    "role": "CLIENT",
    "email": "felipe@email.com"
  }
}
```

---

# Status possíveis

| Código | Descrição |
|------|------|
| 201 | Cliente registrado |
| 400 | Dados inválidos |
| 409 | Email ou telefone já em uso |

---

# Observações

- O usuário criado recebe automaticamente a role **CLIENT**
- Email e telefone são normalizados antes do registro
- Senha é armazenada usando **PasswordEncoder**