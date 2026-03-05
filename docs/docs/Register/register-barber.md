---
sidebar_position: 2
title: Register Barber
description: Endpoint responsável pelo registro de barbeiros na plataforma.
---

# Register Barber

Endpoint responsável pelo **cadastro de barbeiros na plataforma BarberFind**.

Este endpoint cria:

1️⃣ Um **User** com role `BARBER`  
2️⃣ Um **Barber** vinculado ao usuário criado

---

# Endpoint

```
POST /api/auth/register/barber
```

---

# Request Body

```json
{
  "name": "João Silva",
  "email": "joao.silva@barberfind.com",
  "password": "123456",
  "phone": "11999999999",
  "bio": "Especialista em cortes modernos e barba",
  "yearsExperience": 5
}
```

---

# Campos

| Campo | Tipo | Obrigatório | Descrição |
|------|------|------|------|
| name | String | Sim | Nome do barbeiro |
| email | String | Sim | Email do usuário |
| password | String | Sim | Senha da conta |
| phone | String | Não | Telefone do usuário |
| bio | String | Não | Descrição profissional do barbeiro |
| yearsExperience | Integer | Não | Anos de experiência |

---

# Validações

| Campo | Regra |
|------|------|
| name | obrigatório |
| email | obrigatório e formato válido |
| password | mínimo 6 caracteres |
| yearsExperience | mínimo 0 |

---

# Response

```json
{
  "message": "registered",
  "data": {
    "userId": "usr_abc123",
    "barberId": "barber_xyz456",
    "name": "João Silva",
    "email": "joao.silva@barberfind.com"
  }
}
```

---

# Campos do Response

| Campo | Tipo | Descrição |
|------|------|------|
| message | String | Mensagem de confirmação |
| userId | String | ID do usuário criado |
| barberId | String | ID do barbeiro criado |
| name | String | Nome do barbeiro |
| email | String | Email do usuário |

---

# Regras de Negócio

Durante o registro o sistema:

1️⃣ Normaliza o email  
2️⃣ Normaliza o telefone  
3️⃣ Verifica se **email já está em uso**  
4️⃣ Verifica se **telefone já está em uso**  
5️⃣ Cria o **User com role BARBER**  
6️⃣ Cria o **Barber vinculado ao usuário**

---

# Status possíveis

| Código | Descrição |
|------|------|
| 201 | Barbeiro registrado com sucesso |
| 400 | Dados inválidos |
| 409 | Email ou telefone já em uso |

---

# Estrutura interna criada

### User

```json
{
  "id": "usr_xxx",
  "role": "BARBER",
  "name": "João Silva",
  "email": "joao.silva@barberfind.com",
  "phone": "11999999999"
}
```

### Barber

```json
{
  "id": "barber_xxx",
  "userId": "usr_xxx",
  "name": "João Silva",
  "bio": "Especialista em cortes modernos",
  "yearsExperience": 5,
  "rating": 0
}
```

---

# Observações

- IDs são gerados utilizando **CUID**
- Senhas são armazenadas usando **PasswordEncoder**
- O usuário criado recebe automaticamente a role **BARBER**
- O rating inicial do barbeiro é **0**