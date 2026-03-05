---
sidebar_position: 1
title: Barbershop Barbers
description: Gerenciamento de barbeiros dentro de uma barbearia.
---

# Barbershop Barbers

Endpoints responsáveis por **gerenciar barbeiros vinculados a uma barbearia**.

Através dessas rotas é possível:

- listar barbeiros
- solicitar entrada em uma barbearia
- aprovar barbeiros
- rejeitar barbeiros
- remover barbeiros

---

# Base Endpoint

```
/api/barbershops/{barbershopId}/barbers
```

---

# 1️⃣ Listar barbeiros da barbearia

Retorna todos os barbeiros vinculados à barbearia (ativos ou pendentes).

### Endpoint

```
GET /api/barbershops/{barbershopId}/barbers
```

### Exemplo

```
GET http://localhost:8080/api/barbershops/bs_123/barbers
```

### Response

```json
[
  {
    "linkId": "link_xxx",
    "active": true,
    "barberId": "barber_xxx",
    "userId": "user_xxx",
    "name": "João Silva",
    "bio": "Especialista em fade",
    "yearsExperience": 5,
    "rating": 4.9,
    "services": [
      {
        "serviceId": "srv_1",
        "serviceName": "Corte Masculino",
        "durationMinutes": 30,
        "basePrice": 40
      }
    ],
    "availability": [
      {
        "weekday": 1,
        "startTime": "09:00",
        "endTime": "18:00"
      }
    ],
    "portfolio": [
      {
        "id": "photo_1",
        "imageUrl": "https://cdn.barberfind.com/1.jpg",
        "hairType": "curly",
        "styleTag": "fade",
        "createdAt": "2026-03-05T10:00:00"
      }
    ]
  }
]
```

---

# 2️⃣ Solicitar entrada na barbearia

Barbeiros podem solicitar entrada em uma barbearia.

### Permissão

```
BARBER
```

### Endpoint

```
POST /api/barbershops/{barbershopId}/barbers/request
```

### Exemplo

```
POST http://localhost:8080/api/barbershops/bs_123/barbers/request
```

### Request Body

Nenhum body é necessário.

### Response

```json
{
  "linkId": "link_xxx",
  "barbershopId": "bs_123",
  "barberId": "barber_xxx",
  "active": false,
  "message": "entry_requested_pending_approval"
}
```

---

# 3️⃣ Aprovar barbeiro

O **dono da barbearia (OWNER)** aprova a solicitação de um barbeiro.

### Permissão

```
OWNER
```

### Endpoint

```
PATCH /api/barbershops/{barbershopId}/barbers/{linkId}/approve
```

### Exemplo

```
PATCH http://localhost:8080/api/barbershops/bs_123/barbers/link_123/approve
```

### Response

```json
{
  "linkId": "link_123",
  "barbershopId": "bs_123",
  "barberId": "barber_1",
  "active": true,
  "message": "barber_approved"
}
```

---

# 4️⃣ Rejeitar barbeiro

O **OWNER** pode rejeitar ou desativar um barbeiro.

### Permissão

```
OWNER
```

### Endpoint

```
PATCH /api/barbershops/{barbershopId}/barbers/{linkId}/reject
```

### Exemplo

```
PATCH http://localhost:8080/api/barbershops/bs_123/barbers/link_123/reject
```

### Response

```json
{
  "linkId": "link_123",
  "barbershopId": "bs_123",
  "barberId": "barber_1",
  "active": false,
  "message": "barber_deactivated"
}
```

---

# 5️⃣ Remover barbeiro da barbearia

Remove o vínculo entre barbeiro e barbearia.

### Permissão

```
OWNER ou BARBER
```

- OWNER pode remover qualquer barbeiro
- BARBER pode sair da barbearia

### Endpoint

```
DELETE /api/barbershops/{barbershopId}/barbers/{linkId}
```

### Exemplo

```
DELETE http://localhost:8080/api/barbershops/bs_123/barbers/link_123
```

### Response

```json
{
  "message": "removed"
}
```

---

# Status possíveis

| Código | Descrição |
|------|------|
| 200 | Operação realizada |
| 201 | Solicitação criada |
| 403 | Sem permissão |
| 404 | Barbearia ou vínculo não encontrado |
| 409 | Barbeiro já vinculado |

---

# Observações

- Um barbeiro precisa **solicitar entrada** antes de ser aprovado.
- O vínculo é armazenado na tabela **barbershop_barbers**.
- `active = false` significa **pendente ou rejeitado**.
- `active = true` significa **barbeiro aprovado na barbearia**.