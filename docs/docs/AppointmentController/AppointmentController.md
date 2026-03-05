``---
sidebar_position: 1
title: Appointment Controller
description: Endpoints responsáveis pelo gerenciamento de agendamentos.
---

# Appointment Controller

Endpoints responsáveis pelo **gerenciamento de agendamentos** entre clientes e barbeiros.

---

# Status de Agendamento

Os agendamentos possuem os seguintes status:

| Status | Descrição |
|------|------|
| PENDING | Agendamento criado aguardando confirmação |
| CONFIRMED | Agendamento confirmado pelo barbeiro |
| COMPLETED | Atendimento finalizado |
| CANCELLED | Agendamento cancelado |
| NO_SHOW | Cliente não compareceu |

---

# Criar Agendamento

Cria um novo agendamento.

### Permissão

CLIENT

### Endpoint

```
POST /api/appointments
```

### Request Body

```json
{
  "barbershopId": "ID_DA_BARBEARIA",
  "barberId": "ID_DO_BARBEIRO",
  "serviceId": "ID_DO_SERVICO",
  "scheduledAt": "2026-04-10T10:00:00",
  "paymentMethod": "PIX"
}
```

### Regras

- O horário precisa ser no futuro
- O barbeiro precisa pertencer à barbearia
- O barbeiro precisa estar ativo na barbearia
- O horário precisa estar disponível

### Status possíveis

| Código | Descrição |
|------|------|
| 201 | Agendamento criado |
| 404 | Barbearia, barbeiro ou serviço não encontrado |
| 409 | Horário já ocupado |
| 400 | Dados inválidos |

---

# Listar Meus Agendamentos

Lista os agendamentos do cliente autenticado.

### Permissão

CLIENT

### Endpoint

```
GET /api/appointments/me
```

### Response

```json
[
  {
    "id": "appt_xxx",
    "userId": "user_xxx",
    "barbershopId": "shop_xxx",
    "barberId": "barber_xxx",
    "serviceId": "service_xxx",
    "serviceName": "Corte Degradê",
    "durationMinutes": 30,
    "finalPrice": 40.00,
    "paymentMethod": "PIX",
    "scheduledAt": "2026-04-10T10:00:00",
    "completedAt": null,
    "status": "PENDING",
    "cancellationReason": null,
    "createdAt": "2026-03-05T18:00:00"
  }
]
```

---

# Listar Agendamentos da Barbearia

Lista todos os agendamentos de uma barbearia.

### Permissão

OWNER ou BARBER

### Endpoint

```
GET /api/barbershops/{barbershopId}/appointments
```

### Parâmetros

| Campo | Tipo | Descrição |
|------|------|------|
| barbershopId | String | ID da barbearia |

---

# Confirmar Agendamento

Confirma um agendamento.

### Permissão

BARBER ou OWNER

### Endpoint

```
PATCH /api/appointments/{id}/confirm
```

### Parâmetros

| Campo | Tipo | Descrição |
|------|------|------|
| id | String | ID do agendamento |

---

# Concluir Agendamento

Finaliza um atendimento.

### Permissão

BARBER ou OWNER

### Endpoint

```
PATCH /api/appointments/{id}/complete
```

### Request Body

```json
{
  "finalPrice": 45.00,
  "paymentMethod": "PIX"
}
```

### Regras

- Apenas agendamentos **CONFIRMED** podem ser concluídos.

---

# Marcar No Show

Marca quando o cliente não comparece.

### Permissão

BARBER ou OWNER

### Endpoint

```
PATCH /api/appointments/{id}/no-show
```

### Parâmetros

| Campo | Tipo | Descrição |
|------|------|------|
| id | String | ID do agendamento |

---

# Cancelar Agendamento

Cancela um agendamento.

### Permissão

CLIENT, BARBER ou OWNER

### Endpoint

```
PATCH /api/appointments/{id}/cancel
```

### Request Body

```json
{
  "cancellationReason": "Cliente não poderá comparecer"
}
```

### Regras

Não é possível cancelar agendamentos:

- COMPLETED
- CANCELLED

---

# Fluxo de Status

Fluxo típico de um agendamento:

```
PENDING
   ↓
CONFIRMED
   ↓
COMPLETED
```

Fluxos alternativos:

```
PENDING → CANCELLED
CONFIRMED → CANCELLED
CONFIRMED → NO_SHOW
```

---

# Estrutura do Response

```json
{
  "id": "string",
  "userId": "string",
  "barbershopId": "string",
  "barberId": "string",
  "serviceId": "string",
  "serviceName": "string",
  "durationMinutes": 30,
  "finalPrice": 40.00,
  "paymentMethod": "PIX",
  "scheduledAt": "2026-04-10T10:00:00",
  "completedAt": "2026-04-10T10:40:00",
  "status": "COMPLETED",
  "cancellationReason": null,
  "createdAt": "2026-03-05T18:00:00"
}
```

---

# Observações

- IDs são gerados usando **CUID**
- Horários utilizam **LocalDateTime**
- Os endpoints exigem autenticação