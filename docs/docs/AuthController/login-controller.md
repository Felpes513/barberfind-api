---
sidebar_position: 1
title: Authentication
description: Endpoints responsáveis pela autenticação de usuários.
---

# Authentication

Endpoints responsáveis pela **autenticação de usuários na plataforma BarberFind**.

O sistema utiliza **JWT (JSON Web Token)** para autenticação.

Após realizar login, o token deve ser enviado no header:

```
Authorization: Bearer {token}
```

---

# Login

Realiza a autenticação do usuário e retorna um **JWT Token**.

### Endpoint

```
POST /api/auth/login
```

### Request Body

```json
{
  "email": "felipe.moreira@uscsonline.com.br",
  "password": "123456"
}
```

### Validações

| Campo | Regra |
|------|------|
| email | obrigatório e formato válido |
| password | obrigatório |

---

### Response

```json
{
  "token": "JWT_TOKEN",
  "userId": "user_xxx",
  "role": "CLIENT",
  "name": "Felipe Moreira",
  "email": "felipe.moreira@uscsonline.com.br"
}
```

---

### Descrição dos campos

| Campo | Tipo | Descrição |
|------|------|------|
| token | String | Token JWT utilizado para autenticação |
| userId | String | ID do usuário |
| role | String | Papel do usuário no sistema |
| name | String | Nome do usuário |
| email | String | Email do usuário |

---

### Possíveis Roles

| Role | Descrição |
|------|------|
| CLIENT | Cliente da barbearia |
| BARBER | Barbeiro |
| OWNER | Dono da barbearia |

---

### Status possíveis

| Código | Descrição |
|------|------|
| 200 | Login realizado com sucesso |
| 401 | Credenciais inválidas |

---

# Logout

Revoga o token JWT do usuário.

Após revogado, o token **não poderá mais ser utilizado para acessar a API**.

### Endpoint

```
POST /api/auth/logout
```

### Headers

```
Authorization: Bearer {token}
```

### Exemplo

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Request Body

Não é necessário body.

```json
{}
```

### Funcionamento

O sistema:

1. Extrai o token do header
2. Adiciona o token na tabela de **tokens revogados**
3. Bloqueia o uso futuro do token

---

### Status possíveis

| Código | Descrição |
|------|------|
| 204 | Logout realizado com sucesso |

---

# Fluxo de Autenticação

Fluxo padrão de autenticação da API:

```
Login
  ↓
Recebe JWT
  ↓
Envia JWT no header Authorization
  ↓
Acessa endpoints protegidos
  ↓
Logout (token revogado)
```

---

# Exemplo de uso com Authorization

Após login:

```
Authorization: Bearer JWT_TOKEN
```

Exemplo de chamada:

```
GET /api/appointments/me
Authorization: Bearer JWT_TOKEN
```

---

# Observações

- O sistema utiliza **JWT Stateless Authentication**
- Tokens revogados são armazenados no banco
- Emails são normalizados antes da autenticação
- Senhas são verificadas utilizando **PasswordEncoder**