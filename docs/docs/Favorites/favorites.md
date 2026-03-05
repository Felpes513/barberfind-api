# Favoritos

Endpoints para gerenciamento de favoritos do usuário.

Um usuário pode favoritar:

- um **barbeiro**
- uma **barbearia**

**Apenas um deles pode ser enviado por requisição**.

Todos os endpoints requerem autenticação.

---

## Listar Favoritos

Retorna todos os favoritos de um usuário.

### Requisição

`GET /api/users/{userId}/favorites`

#### Headers

```
Authorization: Bearer <token>
```

### Resposta

`200 OK`

```json
[
  {
    "id": "fav_123",
    "barberId": "barber_1",
    "barberName": "John",
    "barbershopId": null,
    "barbershopName": null,
    "createdAt": "2025-03-10T15:20:00"
  }
]
```

---

## Adicionar Favorito

Cria um favorito para o usuário.

### Requisição

`POST /api/users/{userId}/favorites`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body (Barbeiro)

```json
{
  "barberId": "barber_123"
}
```

#### Body (Barbearia)

```json
{
  "barbershopId": "shop_123"
}
```

### Resposta

`201 Created`

```json
{
  "id": "fav_123",
  "barberId": "barber_123",
  "barberName": "John",
  "barbershopId": null,
  "barbershopName": null,
  "createdAt": "2025-03-10T15:20:00"
}
```

---

## Remover Favorito

Remove um favorito do usuário.

### Requisição

`DELETE /api/users/{userId}/favorites/{favoriteId}`

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
| Apenas um alvo | Somente barberId OU barbershopId pode ser enviado |
| Propriedade | O usuário só pode gerenciar seus próprios favoritos |
| Prevenção de duplicatas | Não é possível favoritar o mesmo barbeiro/barbearia duas vezes |

---

## Erros

### Barbeiro já favoritado

`409 Conflict`

```json
{
  "error": "barber_already_favorited"
}
```

### Barbearia já favoritada

`409 Conflict`

```json
{
  "error": "barbershop_already_favorited"
}
```

### Alvo ausente

`422 Unprocessable Entity`

```json
{
  "error": "barber_id_or_barbershop_id_required"
}
```

### Ambos os alvos enviados

`422 Unprocessable Entity`

```json
{
  "error": "only_one_of_barber_id_or_barbershop_id_allowed"
}
```

### Acesso negado

`403 Forbidden`

```json
{
  "error": "forbidden"
}
```

### Favorito não encontrado

`404 Not Found`

```json
{
  "error": "favorite_not_found"
}
```