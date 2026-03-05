# Serviços

Endpoints para gerenciamento de serviços globais e vínculos de serviços específicos de barbearias.

Existem dois conceitos principais:

- **Serviço Global** – disponível para todas as barbearias.
- **Vínculo de Serviço da Barbearia** – uma barbearia específica oferecendo um serviço com precificação personalizada opcional.

Autenticação é necessária para ações restritas ao papel de **OWNER**.

---

## Criar Serviço Global

Cria um novo serviço global.

### Requisição

`POST /api/services`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body

```json
{
  "name": "Corte Degradê",
  "description": "Corte moderno com máquina e tesoura",
  "basePrice": 45.00,
  "durationMinutes": 30
}
```

### Resposta

`201 Created`

```json
{
  "id": "svc_123",
  "name": "Corte Degradê",
  "description": "Corte moderno com máquina e tesoura",
  "basePrice": 45.00,
  "durationMinutes": 30
}
```

---

## Listar Serviços Globais

Retorna todos os serviços globais.

### Requisição

`GET /api/services`

### Resposta

`200 OK`

```json
[
  {
    "id": "svc_123",
    "name": "Corte Degradê",
    "description": "Corte moderno com máquina e tesoura",
    "basePrice": 45.00,
    "durationMinutes": 30
  }
]
```

---

## Listar Serviços da Barbearia

Lista todos os serviços vinculados a uma barbearia específica.

### Requisição

`GET /api/barbershops/{barbershopId}/services`

### Resposta

`200 OK`

```json
[
  {
    "id": "link_123",
    "serviceId": "svc_123",
    "serviceName": "Corte Degradê",
    "serviceDescription": "Corte moderno com máquina e tesoura",
    "durationMinutes": 30,
    "basePrice": 45.00,
    "customPrice": 50.00,
    "effectivePrice": 50.00
  }
]
```

---

## Vincular Serviço à Barbearia

Cria um vínculo entre uma barbearia e um serviço global. Opcionalmente, um preço personalizado pode ser fornecido.

### Requisição

`POST /api/barbershops/{barbershopId}/services`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body

```json
{
  "serviceId": "svc_123",
  "customPrice": 50.00
}
```

### Resposta

`201 Created`

```json
{
  "id": "link_123",
  "serviceId": "svc_123",
  "serviceName": "Corte Degradê",
  "serviceDescription": "Corte moderno com máquina e tesoura",
  "durationMinutes": 30,
  "basePrice": 45.00,
  "customPrice": 50.00,
  "effectivePrice": 50.00
}
```

---

## Atualizar Vínculo de Serviço da Barbearia

Atualiza o preço personalizado de um serviço vinculado a uma barbearia.

### Requisição

`PUT /api/barbershops/{barbershopId}/services/{linkId}`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body

```json
{
  "customPrice": 55.00
}
```

### Resposta

`200 OK`

```json
{
  "id": "link_123",
  "serviceId": "svc_123",
  "serviceName": "Corte Degradê",
  "serviceDescription": "Corte moderno com máquina e tesoura",
  "durationMinutes": 30,
  "basePrice": 45.00,
  "customPrice": 55.00,
  "effectivePrice": 55.00
}
```

---

## Desvincular Serviço da Barbearia

Remove o vínculo de um serviço de uma barbearia.

### Requisição

`DELETE /api/barbershops/{barbershopId}/services/{linkId}`

#### Headers

```
Authorization: Bearer <token>
```

### Resposta

`200 OK`

```json
{
  "message": "unlinked"
}
```

---

## Regras de Validação

| Regra | Descrição |
|-------|-----------|
| Existência do serviço | Não é possível vincular um serviço que não existe |
| Propriedade | Apenas proprietários da barbearia podem gerenciar vínculos |
| Prevenção de duplicatas | Não é possível vincular o mesmo serviço duas vezes |
| Preço personalizado opcional | Se não fornecido, o basePrice será usado como effectivePrice |

---

## Erros

### Serviço não encontrado

`404 Not Found`

```json
{
  "error": "service_not_found"
}
```

### Serviço já vinculado

`409 Conflict`

```json
{
  "error": "service_already_linked"
}
```

### Vínculo não encontrado

`404 Not Found`

```json
{
  "error": "link_not_found"
}
```

### Acesso negado

`403 Forbidden`

```json
{
  "error": "not_owner_or_barbershop_not_found"
}
```