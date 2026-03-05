# API - Agendamentos

## POST `/api/appointments`

Cria agendamento.

- **Permissão:** `CLIENT`
- **Body:** `barbershopId`, `barberId`, `serviceId`, `scheduledAt` (futuro), `paymentMethod`
- **Response:** `201` com agendamento em status `PENDING`

## GET `/api/appointments/me`

Lista agendamentos do cliente autenticado.

- **Permissão:** `CLIENT`

## GET `/api/barbershops/{barbershopId}/appointments`

Lista agendamentos da barbearia.

- **Permissão:** `OWNER` ou `BARBER` com vínculo válido na barbearia

## PATCH `/api/appointments/{id}/confirm`

Confirma agendamento pendente.

- **Permissão:** `BARBER` ou `OWNER`

## PATCH `/api/appointments/{id}/complete`

Conclui agendamento confirmado.

- **Permissão:** `BARBER` ou `OWNER`
- **Body opcional:** `finalPrice`, `paymentMethod`

## PATCH `/api/appointments/{id}/no-show`

Marca no-show para agendamento confirmado.

- **Permissão:** `BARBER` ou `OWNER`

## PATCH `/api/appointments/{id}/cancel`

Cancela agendamento.

- **Permissão:** autenticado
  - `CLIENT`: apenas próprio agendamento
  - `BARBER`/`OWNER`: conforme validação de staff/ownership
- **Body opcional:** `cancellationReason`
