# BarberFind API (NestJS + Prisma)

Substitui a API Spring Boot legada. Rotas sob prefixo global `/api`, exceto health checks na raiz.

## Health checks

| Método | Caminho | Descrição |
|--------|---------|-----------|
| GET | `/init` | Alias moderno — mesmo JSON que `/initi` |
| GET | `/initi` | Legado (Spring) — `{ ok, msg }` |
| GET | `/api/ping` | `{ status: 'ok' }` |

## Configuração

1. Copie [`.env.example`](./.env.example) para `.env` e preencha `DATABASE_URL` (PostgreSQL) e `JWT_SECRET` (32+ caracteres recomendado em produção).
2. Se o banco não tiver as colunas extras de `barbershops` / `favorites`, aplique o SQL em [`prisma/optional-align.sql`](./prisma/optional-align.sql) (ou equivalente no teu processo de migração).
3. Opcional: `npx prisma db pull` para alinhar `schema.prisma` ao banco real.
4. `npm install` e `npx prisma generate`
5. `npm run start:dev` — porta padrão **8080** (`PORT` ou `SERVER_PORT`).

### Keep-alive (Render e similares)

A cada **30 segundos** o processo regista um log `[keep-alive] BarberFind API tick ...`.

- No **Render** (plano gratuito), o serviço pode hibernar sem tráfego HTTP. Para o próprio processo fazer um pedido GET público a cada tick, define `ENABLE_KEEP_ALIVE_PING=true`. O URL usado é `KEEP_ALIVE_URL` se existir, senão `RENDER_EXTERNAL_URL` + `/api/ping` (a variável `RENDER_EXTERNAL_URL` é injetada pelo Render em Web Services).

### Documentação (Docusaurus)

Site estático separado em [`docs/`](./docs/): `cd docs && npm install && npm run start` (dev) ou `npm run build`.

## Scripts

- `npm run start:dev` — desenvolvimento
- `npm run build` / `npm run start:prod` — produção
- `npm run test:e2e` — smoke de `/init`, `/initi` e `/api/ping`
- `npm run prisma:pull` — introspect do banco
- `npm run prisma:generate` — gera o client
