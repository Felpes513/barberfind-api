# API - Barbeiros (vínculo com barbearia)

Endpoints agrupados em `/api/barbershops/{barbershopId}/barbers`.

## GET `/api/barbershops/{barbershopId}/barbers`

Lista barbeiros vinculados à barbearia (ativos e pendentes), incluindo:

- dados do barbeiro
- serviços do barbeiro
- disponibilidade (`barber_availability`)
- portfólio (`barber_portfolio`)

- **Permissão:** autenticado

## POST `/api/barbershops/{barbershopId}/barbers/request`

Barbeiro solicita entrada na barbearia.

- **Permissão:** `BARBER`
- **Response:** `201` com status pendente

## PATCH `/api/barbershops/{barbershopId}/barbers/{linkId}/approve`

Owner aprova vínculo.

- **Permissão:** `OWNER`

## PATCH `/api/barbershops/{barbershopId}/barbers/{linkId}/reject`

Owner rejeita/desativa vínculo.

- **Permissão:** `OWNER`

## DELETE `/api/barbershops/{barbershopId}/barbers/{linkId}`

Remove vínculo.

- **Permissão:** `OWNER` (remove qualquer vínculo da barbearia) ou `BARBER` (apenas o próprio vínculo)
