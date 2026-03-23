# Produtos

Endpoints para gerenciamento de produtos de uma barbearia.

Autenticação é necessária para ações restritas ao papel de **OWNER**.

---

## Listar Produtos da Barbearia

Retorna todos os produtos cadastrados em uma barbearia específica.

### Requisição

`GET /api/barbershops/{barbershopId}/products`

### Resposta

`200 OK`

```json
[
  {
    "id": "prod_abc123",
    "barbershopId": "barber_xyz",
    "barbershopName": "Barbearia do João",
    "name": "Pomada Modeladora",
    "price": 35.00,
    "stockQuantity": 50,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

## Buscar Produto por ID

Retorna os dados de um produto específico de uma barbearia.

### Requisição

`GET /api/barbershops/{barbershopId}/products/{productId}`

### Resposta

`200 OK`

```json
{
  "id": "prod_abc123",
  "barbershopId": "barber_xyz",
  "barbershopName": "Barbearia do João",
  "name": "Pomada Modeladora",
  "price": 35.00,
  "stockQuantity": 50,
  "createdAt": "2024-01-15T10:30:00"
}
```

---

## Criar Produto

Cria um novo produto para uma barbearia. Requer papel **OWNER**.

### Requisição

`POST /api/barbershops/{barbershopId}/products`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body

```json
{
  "name": "Pomada Modeladora",
  "price": 35.00,
  "stockQuantity": 50
}
```

### Resposta

`201 Created`

```json
{
  "id": "prod_abc123",
  "barbershopId": "barber_xyz",
  "barbershopName": "Barbearia do João",
  "name": "Pomada Modeladora",
  "price": 35.00,
  "stockQuantity": 50,
  "createdAt": "2024-01-15T10:30:00"
}
```

---

## Atualizar Produto

Atualiza os dados de um produto existente. Requer papel **OWNER**.

### Requisição

`PUT /api/barbershops/{barbershopId}/products/{productId}`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body

```json
{
  "name": "Pomada Modeladora Extra Forte",
  "price": 40.00,
  "stockQuantity": 30
}
```

### Resposta

`200 OK`

```json
{
  "id": "prod_abc123",
  "barbershopId": "barber_xyz",
  "barbershopName": "Barbearia do João",
  "name": "Pomada Modeladora Extra Forte",
  "price": 40.00,
  "stockQuantity": 30,
  "createdAt": "2024-01-15T10:30:00"
}
```

---

## Remover Produto

Remove um produto da barbearia. Requer papel **OWNER**.

### Requisição

`DELETE /api/barbershops/{barbershopId}/products/{productId}`

#### Headers

```
Authorization: Bearer <token>
```

### Resposta

`204 No Content`

---

## Regras de Validação

| Campo | Regra |
|-------|-------|
| `name` | Obrigatório, não pode ser vazio |
| `price` | Obrigatório, deve ser maior que 0 |
| `stockQuantity` | Obrigatório, deve ser maior ou igual a 0 |
| Nome do produto | Não pode haver dois produtos com o mesmo nome na mesma barbearia |

---

## Erros

### Barbearia não encontrada

`404 Not Found`

```json
{
  "error": "barbershop_not_found"
}
```

### Produto não encontrado

`404 Not Found`

```json
{
  "error": "product_not_found"
}
```

### Nome de produto já existe

`409 Conflict`

```json
{
  "error": "product_name_already_exists"
}
```

### Acesso negado

`403 Forbidden`

```json
{
  "error": "access_denied"
}
```