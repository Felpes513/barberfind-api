# Usuários

Endpoints para gerenciamento de dados pessoais, senha, métodos de pagamento, preferências, termos de uso e documentos do usuário.

Todos os endpoints requerem autenticação e só podem ser acessados pelo próprio usuário.

---

## Atualizar Dados Pessoais

Atualiza parcialmente os dados do usuário. Apenas os campos enviados serão alterados. Campos de barbeiro (`bio`, `yearsExperience`) só são aplicados se o usuário tiver o papel **BARBER**.

### Requisição

`PATCH /api/users/{userId}`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body

```json
{
  "name": "João Silva",
  "phone": "11999999999",
  "birthDate": "1995-06-15",
  "hairType": "Cacheado",
  "hairTexture": "Grosso",
  "hasBeard": true,
  "bio": "Barbeiro especialista em degradê",
  "yearsExperience": 5
}
```

> Todos os campos são opcionais. Apenas os campos enviados serão atualizados.

### Resposta

`200 OK`

```json
{
  "message": "updated"
}
```

---

## Alterar Senha

Altera a senha do usuário. Requer a senha atual para confirmação.

### Requisição

`PUT /api/users/{userId}/password`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body

```json
{
  "currentPassword": "senhaAtual123",
  "newPassword": "novaSenha456"
}
```

### Resposta

`200 OK`

```json
{
  "message": "password_updated"
}
```

---

## Métodos de Pagamento

### Listar Métodos de Pagamento

Retorna todos os métodos de pagamento cadastrados pelo usuário.

#### Requisição

`GET /api/users/{userId}/payment-methods`

##### Headers

```
Authorization: Bearer <token>
```

#### Resposta

`200 OK`

```json
[
  {
    "id": "pay_123",
    "provider": "STRIPE",
    "cardType": "CREDIT",
    "lastFourDigits": "4242",
    "brand": "VISA",
    "createdAt": "2025-03-10T15:20:00"
  }
]
```

---

### Adicionar Método de Pagamento

Adiciona um novo método de pagamento. O token do cartão é gerado pelo gateway de pagamento no frontend — dados brutos do cartão nunca são armazenados.

#### Requisição

`POST /api/users/{userId}/payment-methods`

##### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

##### Body

```json
{
  "provider": "STRIPE",
  "providerCustomerId": "cus_abc123",
  "cardType": "CREDIT",
  "lastFourDigits": "4242",
  "brand": "VISA"
}
```

#### Resposta

`201 Created`

```json
{
  "id": "pay_123",
  "provider": "STRIPE",
  "cardType": "CREDIT",
  "lastFourDigits": "4242",
  "brand": "VISA",
  "createdAt": "2025-03-10T15:20:00"
}
```

---

### Remover Método de Pagamento

Remove um método de pagamento do usuário.

#### Requisição

`DELETE /api/users/{userId}/payment-methods/{methodId}`

##### Headers

```
Authorization: Bearer <token>
```

#### Resposta

`204 No Content`

---

## Preferências

### Obter Preferências

Retorna as preferências do usuário. Caso ainda não configuradas, retorna os valores padrão (`LIGHT` e `pt-BR`).

#### Requisição

`GET /api/users/{userId}/preferences`

##### Headers

```
Authorization: Bearer <token>
```

#### Resposta

`200 OK`

```json
{
  "id": "pref_123",
  "theme": "LIGHT",
  "language": "pt-BR",
  "updatedAt": "2025-03-10T15:20:00"
}
```

---

### Atualizar Preferências

Atualiza o tema e/ou idioma do usuário. Apenas os campos enviados serão alterados.

#### Requisição

`PUT /api/users/{userId}/preferences`

##### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

##### Body

```json
{
  "theme": "DARK",
  "language": "en-US"
}
```

#### Resposta

`200 OK`

```json
{
  "id": "pref_123",
  "theme": "DARK",
  "language": "en-US",
  "updatedAt": "2025-03-10T15:20:00"
}
```

---

## Aceitar Termos de Uso

Registra o aceite de uma versão dos termos de uso pelo usuário.

### Requisição

`POST /api/users/{userId}/accept-terms`

#### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body

```json
{
  "termsVersion": "1.0",
  "accepted": true
}
```

### Resposta

`200 OK`

```json
{
  "message": "terms_accepted",
  "termsVersion": "1.0",
  "acceptedAt": "2025-03-10T15:20:00"
}
```

---

## Documentos

### Listar Documentos

Retorna todos os documentos cadastrados pelo usuário.

#### Requisição

`GET /api/users/{userId}/documents`

##### Headers

```
Authorization: Bearer <token>
```

#### Resposta

`200 OK`

```json
[
  {
    "id": "doc_123",
    "documentType": "CPF",
    "documentNumber": "123.456.789-00",
    "createdAt": "2025-03-10T15:20:00"
  }
]
```

---

### Adicionar Documento

Adiciona um novo documento ao usuário.

#### Requisição

`POST /api/users/{userId}/documents`

##### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

##### Body

```json
{
  "documentType": "CPF",
  "documentNumber": "123.456.789-00"
}
```

#### Resposta

`201 Created`

```json
{
  "id": "doc_123",
  "documentType": "CPF",
  "documentNumber": "123.456.789-00",
  "createdAt": "2025-03-10T15:20:00"
}
```

---

### Atualizar Documento

Atualiza um documento existente do usuário. Apenas os campos enviados serão alterados.

#### Requisição

`PUT /api/users/{userId}/documents/{documentId}`

##### Headers

```
Authorization: Bearer <token>
Content-Type: application/json
```

##### Body

```json
{
  "documentType": "RG",
  "documentNumber": "12.345.678-9"
}
```

#### Resposta

`200 OK`

```json
{
  "id": "doc_123",
  "documentType": "RG",
  "documentNumber": "12.345.678-9",
  "createdAt": "2025-03-10T15:20:00"
}
```

---

## Regras de Validação

| Campo | Regra |
|-------|-------|
| `phone` | Entre 10 e 20 caracteres, apenas números, `+`, `()`, `-` e espaços |
| `newPassword` | Entre 8 e 128 caracteres |
| `theme` | Apenas `LIGHT` ou `DARK` |
| `language` | Formato `xx` ou `xx-XX` (ex: `pt-BR`, `en-US`) |
| `cardType` | Apenas `CREDIT` ou `DEBIT` |
| `lastFourDigits` | Exatamente 4 dígitos numéricos |
| `accepted` | Deve ser `true` para aceitar os termos |
| `yearsExperience` | Valor mínimo de `0` |

---

## Erros

### Acesso negado

`403 Forbidden`

```json
{
  "error": "forbidden"
}
```

### Usuário não encontrado

`404 Not Found`

```json
{
  "error": "user_not_found"
}
```

### Telefone já em uso

`409 Conflict`

```json
{
  "error": "phone_already_in_use"
}
```

### Senha atual incorreta

`422 Unprocessable Entity`

```json
{
  "error": "current_password_incorrect"
}
```

### Nova senha igual à atual

`422 Unprocessable Entity`

```json
{
  "error": "new_password_same_as_current"
}
```

### Método de pagamento já cadastrado

`409 Conflict`

```json
{
  "error": "payment_method_already_exists"
}
```

### Método de pagamento não encontrado

`404 Not Found`

```json
{
  "error": "payment_method_not_found"
}
```

### Termos não aceitos

`422 Unprocessable Entity`

```json
{
  "error": "terms_must_be_accepted"
}
```

### Versão dos termos já aceita

`409 Conflict`

```json
{
  "error": "terms_version_already_accepted"
}
```

### Documento não encontrado

`404 Not Found`

```json
{
  "error": "document_not_found"
}
```

### Perfil de barbeiro não encontrado

`404 Not Found`

```json
{
  "error": "barber_profile_not_found"
}
```