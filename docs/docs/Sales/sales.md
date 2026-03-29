# Vendas

Endpoints para gerenciamento de vendas de produtos de uma barbearia.

Autenticação é necessária para todas as operações. Ações de exclusão são restritas ao papel de **OWNER**.

---

## Listar Vendas da Barbearia

Retorna todas as vendas registradas em uma barbearia específica.

### Requisição

`GET /api/barbershops/{barbershopId}/sales`

#### Headers

```
Authorization: Bearer <token>
```

### Resposta

`200 OK`

```json
[
  {
    "id": "sale_abc123",
    "barbershopId": "barber_xyz",
    "barbershopName": "Barbearia do João",
    "barberId": "barber_001",
    "barberName": "Carlos Silva",
    "totalAmount": 75.00,
    "items": [
      {
        "id": "item_001",
        "productId": "prod_abc123",
        "productName": "Pomada Modeladora",
        "quantity": 2,
        "unitPrice": 35.00,
        "subtotal": 70.00
      },
      {
        "id": "item_002",
        "productId": "prod_def456",
        "productName": "Shampoo Anticaspa",
        "quantity": 1,
        "unitPrice": 5.00,
        "subtotal": 5.00
      }
    ],
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

## Buscar Venda por ID

Retorna os dados de uma venda específica de uma barbearia.

### Requisição

`GET /api/barbershops/{barbershopId}/sales/{saleId}`

#### Headers

```
Authorization: Bearer <token>
```

### Resposta

`200 OK`

```json
{
  "id": "sale_abc123",
  "barbershopId": "barber_xyz",
  "barbershopName": "Barbearia do João",
  "barberId": "barber_001",
  "barberName": "Carlos Silva",
  "totalAmount": 75.00,
  "items": [
    {
      "id": "item_001",
      "productId": "prod_abc123",
      "productName": "Pomada Modeladora",
      "quantity": 2,
      "unitPrice": 35.00,
      "subtotal": 70.00
    },
    {
      "id": "item_002",
      "productId": "prod_def456",
      "productName": "Shampoo Anticaspa",
      "quantity": 1,
      "unitPrice": 5.00,
      "subtotal": 5.00
    }
  ],
  "createdAt": "2024-01-15T10:30:00"
}
```

---

## Registrar Venda

Registra uma nova venda de produtos. Requer papel **OWNER** ou **BARBER**.

- **OWNER** pode informar o `barberId` no body para atribuir a venda a um barbeiro específico. Se não informar, a venda fica sem barbeiro vinculado.
- **BARBER** não precisa informar `barberId` — a venda é automaticamente atribuída ao seu próprio perfil.

### Requisição

`POST /api/barbershops/{barbershopId}/sales`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body

```json
{
  "barberId": "barber_001",
  "items": [
    {
      "productId": "prod_abc123",
      "quantity": 2
    },
    {
      "productId": "prod_def456",
      "quantity": 1
    }
  ]
}
```

### Resposta

`201 Created`

```json
{
  "id": "sale_abc123",
  "barbershopId": "barber_xyz",
  "barbershopName": "Barbearia do João",
  "barberId": "barber_001",
  "barberName": "Carlos Silva",
  "totalAmount": 75.00,
  "items": [
    {
      "id": "item_001",
      "productId": "prod_abc123",
      "productName": "Pomada Modeladora",
      "quantity": 2,
      "unitPrice": 35.00,
      "subtotal": 70.00
    },
    {
      "id": "item_002",
      "productId": "prod_def456",
      "productName": "Shampoo Anticaspa",
      "quantity": 1,
      "unitPrice": 5.00,
      "subtotal": 5.00
    }
  ],
  "createdAt": "2024-01-15T10:30:00"
}
```

---

## Remover Venda

Remove uma venda da barbearia e estorna automaticamente o estoque de todos os produtos envolvidos. Requer papel **OWNER**.

### Requisição

`DELETE /api/barbershops/{barbershopId}/sales/{saleId}`

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
| `items` | Obrigatório, deve conter ao menos 1 item |
| `items[].productId` | Obrigatório, produto deve pertencer à barbearia informada |
| `items[].quantity` | Obrigatório, deve ser maior ou igual a 1 |
| `barberId` | Opcional para OWNER; ignorado para BARBER (usa perfil próprio) |
| Estoque | A quantidade solicitada não pode exceder o estoque disponível do produto |
| Preço | O `unitPrice` de cada item é o snapshot do preço do produto no momento da venda |

---

## Erros

### Barbearia não encontrada

`404 Not Found`

```json
{
  "error": "barbershop_not_found"
}
```

### Venda não encontrada

`404 Not Found`

```json
{
  "error": "sale_not_found"
}
```

### Produto não encontrado

`404 Not Found`

```json
{
  "error": "product_not_found"
}
```

### Barbeiro não encontrado

`404 Not Found`

```json
{
  "error": "barber_not_found"
}
```

### Perfil de barbeiro não encontrado

`404 Not Found`

```json
{
  "error": "barber_profile_not_found"
}
```

### Estoque insuficiente

`409 Conflict`

```json
{
  "error": "insufficient_stock"
}
```

### Acesso negado

`403 Forbidden`

```json
{
  "error": "forbidden"
}
```
