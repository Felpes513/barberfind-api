# Reviews

Endpoints para gerenciamento de avaliações de barbearias e barbeiros.

Uma review pode avaliar:

- uma **barbearia** (obrigatório)
- um **barbeiro** dentro da barbearia (opcional)

**Apenas clientes com agendamento concluído na barbearia podem criar uma review.**

---

## Criar Review

Cria uma avaliação para uma barbearia (e opcionalmente para um barbeiro).

### Requisição

`POST /api/reviews`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body

```json
{
  "barbershopId": "shop_123",
  "barberId": "barber_123",
  "rating": 5,
  "comment": "Ótimo atendimento!"
}
```

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `barbershopId` | string | Sim | ID da barbearia avaliada |
| `barberId` | string | Não | ID do barbeiro avaliado |
| `rating` | integer | Sim | Nota de 1 a 5 |
| `comment` | string | Não | Comentário da avaliação |

### Resposta

`201 Created`

```json
{
  "id": "rev_123",
  "userId": "user_123",
  "userName": "Felipe",
  "barbershopId": "shop_123",
  "barbershopName": "Barbearia do João",
  "barberId": "barber_123",
  "barberName": "Carlos",
  "rating": 5,
  "comment": "Ótimo atendimento!",
  "createdAt": "2025-03-10T15:20:00"
}
```

---

## Listar Reviews por Barbearia

Retorna todas as avaliações de uma barbearia.

### Requisição

`GET /api/barbershops/{barbershopId}/reviews`

### Resposta

`200 OK`

```json
[
  {
    "id": "rev_123",
    "userId": "user_123",
    "userName": "Felipe",
    "barbershopId": "shop_123",
    "barbershopName": "Barbearia do João",
    "barberId": "barber_123",
    "barberName": "Carlos",
    "rating": 5,
    "comment": "Ótimo atendimento!",
    "createdAt": "2025-03-10T15:20:00"
  }
]
```

---

## Listar Reviews por Barbeiro

Retorna todas as avaliações de um barbeiro.

### Requisição

`GET /api/barbers/{barberId}/reviews`

### Resposta

`200 OK`

```json
[
  {
    "id": "rev_123",
    "userId": "user_123",
    "userName": "Felipe",
    "barbershopId": "shop_123",
    "barbershopName": "Barbearia do João",
    "barberId": "barber_123",
    "barberName": "Carlos",
    "rating": 5,
    "comment": "Ótimo atendimento!",
    "createdAt": "2025-03-10T15:20:00"
  }
]
```

---

## Deletar Review

Remove uma avaliação de uma barbearia. Apenas o **OWNER** da barbearia pode realizar esta ação.

### Requisição

`DELETE /api/barbershops/{barbershopId}/reviews/{reviewId}`

#### Headers

```
Authorization: Bearer <token>
```

### Resposta

`204 No Content`

---

## Regras de Validação

A API aplica as seguintes regras:

| Regra | Descrição |
|-------|-----------|
| Agendamento concluído | O cliente precisa ter ao menos um agendamento com status `COMPLETED` na barbearia |
| Uma review por barbearia | Não é possível avaliar a mesma barbearia duas vezes |
| Uma review por barbeiro | Não é possível avaliar o mesmo barbeiro duas vezes |
| Nota entre 1 e 5 | O campo `rating` deve ter valor mínimo de 1 e máximo de 5 |
| Apenas OWNER pode deletar | Somente o dono da barbearia pode remover avaliações |

---

## Erros

### Agendamento concluído não encontrado

`403 Forbidden`

```json
{
  "error": "no_completed_appointment"
}
```

### Review já existe para a barbearia

`409 Conflict`

```json
{
  "error": "review_already_exists_for_barbershop"
}
```

### Review já existe para o barbeiro

`409 Conflict`

```json
{
  "error": "review_already_exists_for_barber"
}
```

### Barbearia não encontrada

`404 Not Found`

```json
{
  "error": "barbershop_not_found"
}
```

### Barbeiro não encontrado

`404 Not Found`

```json
{
  "error": "barber_not_found"
}
```

### Review não encontrada

`404 Not Found`

```json
{
  "error": "review_not_found"
}
```

### Acesso negado

`403 Forbidden`

```json
{
  "error": "forbidden"
}
```
