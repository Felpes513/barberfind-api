# API - Pagamentos

A API atual não executa cobrança direta (sem endpoint de cobrança, captura, estorno ou webhook),
mas possui suporte de base para informações de pagamento em dois pontos:

## 1) Métodos de pagamento do usuário

- `GET /api/users/{userId}/payment-methods`
- `POST /api/users/{userId}/payment-methods`
- `DELETE /api/users/{userId}/payment-methods/{methodId}`

Esses endpoints armazenam apenas dados não sensíveis e tokenizados (ex.: `providerCustomerId`, últimos 4 dígitos, bandeira).

## 2) Pagamento em agendamento

- `POST /api/appointments` recebe `paymentMethod`
- `PATCH /api/appointments/{id}/complete` permite ajustar `paymentMethod` e `finalPrice`

## Observações

- Não há integração server-side com gateway para processar transação nesta versão.
- Não há webhook de confirmação de pagamento implementado.
