---
sidebar_position: 3
title: Barbershop Photos
description: Gerenciamento de fotos da barbearia.
---

# Barbershop Photos

Endpoints responsáveis pelo **gerenciamento de fotos de uma barbearia**.

Essas fotos são utilizadas para:

- mostrar o ambiente da barbearia
- exibir resultados de cortes
- melhorar a apresentação no aplicativo

---

# Base Endpoint

```
/api/barbershops/{barbershopId}/photos
```

---

# Tipos de acesso

| Tipo | Permissão |
|-----|-----|
| Listar fotos | Público |
| Adicionar foto | OWNER |
| Remover foto | OWNER |

---

# 1️⃣ Listar fotos da barbearia

Retorna todas as fotos vinculadas a uma barbearia.

### Endpoint

```
GET /api/barbershops/{barbershopId}/photos
```

### Exemplo

```
GET http://localhost:8080/api/barbershops/bs_123/photos
```

### Response

```json
[
  {
    "id": "photo_1",
    "dataUrl": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ...",
    "createdAt": "2026-03-05T10:30:00"
  },
  {
    "id": "photo_2",
    "dataUrl": "data:image/png;base64,iVBORw0KGgoAAAANSUhEU...",
    "createdAt": "2026-03-05T11:00:00"
  }
]
```

---

# 2️⃣ Adicionar foto

Permite que o **OWNER da barbearia** adicione uma nova foto.

### Permissão

```
OWNER
```

### Endpoint

```
POST /api/barbershops/{barbershopId}/photos
```

### Exemplo

```
POST http://localhost:8080/api/barbershops/bs_123/photos
```

### Request Body

```json
{
  "imageData": "/9j/4AAQSkZJRgABAQAAAQABAAD...",
  "mediaType": "image/jpeg"
}
```

### Campos

| Campo | Tipo | Descrição |
|-----|-----|-----|
| imageData | string | imagem em Base64 |
| mediaType | string | tipo da imagem |

### Media Types permitidos

```
image/jpeg
image/png
image/webp
image/gif
```

### Response

```json
{
  "id": "photo_123",
  "dataUrl": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ...",
  "createdAt": "2026-03-05T12:00:00"
}
```

---

# 3️⃣ Remover foto

Remove uma foto da barbearia.

### Permissão

```
OWNER
```

### Endpoint

```
DELETE /api/barbershops/{barbershopId}/photos/{photoId}
```

### Exemplo

```
DELETE http://localhost:8080/api/barbershops/bs_123/photos/photo_123
```

### Response

```json
{
  "message": "deleted"
}
```

---

# Limites e validações

| Regra | Descrição |
|-----|-----|
| Tipos permitidos | jpeg, png, webp, gif |
| Tamanho máximo | 5MB por imagem |
| Formato | Base64 válido |

---

# Formato armazenado

A imagem é armazenada como **Data URL**:

```
data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ...
```

Isso permite que o frontend renderize diretamente:

```html
<img src="data:image/jpeg;base64,/9j/4AAQ..." />
```

---

# Possíveis erros

| Código | Motivo |
|------|------|
| 400 | Base64 inválido |
| 400 | Tipo de imagem inválido |
| 400 | Imagem muito grande |
| 403 | Usuário não é dono da barbearia |
| 404 | Foto não encontrada |

---

# Fluxo típico

1️⃣ Owner cria barbearia  
2️⃣ Owner adiciona fotos  
3️⃣ Clientes visualizam as fotos no app

```
Owner → upload photos
Client → view barbershop gallery
```