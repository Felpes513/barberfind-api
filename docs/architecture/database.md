# Banco de Dados

## Visão geral

- Banco: PostgreSQL
- IDs: CUID (`varchar(40)`)
- Controle de versão: Flyway

## Entidades principais

- `users`: usuários do sistema (cliente, barbeiro, dono)
- `barbers`: perfil profissional do barbeiro
- `barbershops`: barbearias, matriz e filial
- `services`: serviços globais
- `barbershop_services`: serviços oferecidos por barbearia
- `barbershop_barbers`: vínculo barbeiro ↔ barbearia
- `appointments`: agendamentos
- `barbershop_photos`: fotos da barbearia
- `user_documents`: documentos do usuário
- `user_payment_methods`: tokens/métodos de pagamento do usuário
- `user_preferences`: preferências de tema/idioma
- `terms_acceptances`: histórico de aceite de termos

## Relacionamentos importantes

- `users` (OWNER) 1:N `barbershops`
- `users` (BARBER) 1:1 `barbers`
- `barbershops` N:N `barbers` via `barbershop_barbers`
- `barbershops` N:N `services` via `barbershop_services`
- `appointments` aponta para `users`, `barbershops`, `barbers` e `services`

## Tabelas previstas no domínio (sem rotas expostas nesta versão)

Embora existam no schema, ainda não há controllers dedicados para:

- `reviews`
- `favorites`
- `products`
- `sales` / `sale_items`
- `subscriptions`
- `activity_logs`

## Matriz e filial

`barbershops` suporta hierarquia:

- `is_headquarter`
- `parent_barbershop_id`

Regra de consistência via check constraint garante coerência entre matriz e filial.
