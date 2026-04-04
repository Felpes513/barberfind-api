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

### Supabase no Render (erro Prisma `P1001`)

Se na **tua máquina** o Beekeeper liga mas no **Render** aparece *Can't reach database server*, o Postgres está a correr — o problema é **ligação a partir da cloud**:

1. **`DATABASE_URL` com SSL** — No Supabase e na maioria dos hosts remotos, usa `?sslmode=require` (ou o connection string completo copiado de **Project Settings → Database**). Evita `sslmode=disable` no Render.
2. **Password na URL** — Caracteres especiais na palavra-passe têm de estar [percent-encoded](https://www.prisma.io/docs/orm/reference/connection-url#special-characters).
3. **Restrições de IP no Supabase** — Em **Database → Network restrictions / Connection pooling**, confirma que ligações de fora estão permitidas (muitos projectos permitem qualquer IP; se estiveres a restringir, o Render precisa de saída permitida — os IPs do Render não são fixos, por isso costuma ser necessário permitir tráfego externo ou usar a opção indicada na documentação Supabase).
4. **Host e porta** — Direct connection: `db.<ref>.supabase.co:5432`. O pooler (6543) é outro URI; para um Web Service persistente no Render, o direct costuma ser o adequado se o dashboard assim o indicar.

## Docker

Imagem multi-stage (**Node 22 bookworm-slim**, Debian): `npm ci`, `prisma generate`, `nest build`, estágio final com `node_modules` já podado (`npm prune --omit=dev`), utilizador não-root e **8080** exposto. Usa-se Debian em vez de Alpine para o motor nativo do Prisma alinhar com **OpenSSL 3** (em Alpine o binário musl costumava falhar com `libssl.so.1.1` em falta).

**Pré-requisitos:** Docker; em runtime, as mesmas variáveis que em [`.env.example`](./.env.example) — no mínimo `DATABASE_URL` e `JWT_SECRET`. Não incluas `.env` na imagem; injeta segredos no orchestrator (Docker Compose, Render, etc.).

```bash
docker build -t barberfind-api .
docker run --rm -p 8080:8080 \
  -e DATABASE_URL="postgresql://USER:PASSWORD@HOST:5432/DATABASE?sslmode=require" \
  -e JWT_SECRET="your-32-char-secret-minimum" \
  barberfind-api
```

Ajusta o mapeamento de portas se usares outro `PORT` dentro do contentor (ex.: `-p 3000:3000 -e PORT=3000`).

**Render (Web Service):** a plataforma define `PORT` em runtime; a app lê `PORT` ou `SERVER_PORT` (ver [`src/main.ts`](./src/main.ts)), alinhado com o que o Render espera.

**Health check do serviço:** `GET /init` (ou `/initi`) ou `GET /api/ping` — vê a tabela em [Health checks](#health-checks).

**Migrações:** corre `npx prisma migrate deploy` (ou o teu job de CI/CD) **antes** de expor tráfego novo à base; o `CMD` da imagem não executa migrações automaticamente.

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
