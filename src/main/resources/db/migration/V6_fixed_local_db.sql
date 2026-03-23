-- =============================================================================
-- V6__correcao_estado_banco.sql
-- Correção de estado: aplica tudo que V2~V5 deveriam ter feito
-- mas não foi executado fisicamente no banco.
-- Todas as operações são idempotentes (IF NOT EXISTS / IF EXISTS / DO $$).
-- =============================================================================

-- ---------------------------------------------------------------------------
-- [V2] Converte users.role de enum user_role → varchar(20)
-- ---------------------------------------------------------------------------
DO $$
    BEGIN
        -- Só converte se a coluna ainda for do tipo user_role (enum)
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name  = 'users'
              AND column_name = 'role'
              AND udt_name    = 'user_role'
        ) THEN
            ALTER TABLE "users"
                ALTER COLUMN role TYPE varchar(20) USING role::text;

            ALTER TABLE "users"
                ALTER COLUMN role SET DEFAULT 'CLIENT';
        END IF;
    END$$;

-- Remove o enum apenas se ninguém mais o estiver usando
DO $$
    BEGIN
        IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role')
            AND NOT EXISTS (
                SELECT 1
                FROM information_schema.columns
                WHERE udt_name = 'user_role'
            )
        THEN
            DROP TYPE user_role;
        END IF;
    END$$;

-- ---------------------------------------------------------------------------
-- [V3] Tabela revoked_tokens
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS revoked_tokens (
                                              id         VARCHAR(36)  NOT NULL PRIMARY KEY,
                                              token      VARCHAR(512) NOT NULL UNIQUE,
                                              revoked_at TIMESTAMP    NOT NULL
);

-- ---------------------------------------------------------------------------
-- [V4] Colunas extras em user_payment_methods
-- ---------------------------------------------------------------------------
ALTER TABLE "user_payment_methods"
    ADD COLUMN IF NOT EXISTS "card_type"        varchar(10),  -- 'CREDIT' | 'DEBIT'
    ADD COLUMN IF NOT EXISTS "last_four_digits" varchar(4),   -- ex: '4242'
    ADD COLUMN IF NOT EXISTS "brand"            varchar(30);  -- 'VISA', 'MASTERCARD', 'ELO'

-- [V4] Tabela user_preferences (1:1 com users)
CREATE TABLE IF NOT EXISTS "user_preferences" (
                                                  "id"         varchar(40) PRIMARY KEY,
                                                  "user_id"    varchar(40) NOT NULL UNIQUE,
                                                  "theme"      varchar(10) NOT NULL DEFAULT 'LIGHT',
                                                  "language"   varchar(10) NOT NULL DEFAULT 'pt-BR',
                                                  "updated_at" timestamp,
                                                  FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE
);

-- [V4] Tabela terms_acceptances
CREATE TABLE IF NOT EXISTS "terms_acceptances" (
                                                   "id"            varchar(40) PRIMARY KEY,
                                                   "user_id"       varchar(40) NOT NULL,
                                                   "terms_version" varchar(20) NOT NULL,
                                                   "accepted_at"   timestamp   NOT NULL,
                                                   FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE
);

-- ---------------------------------------------------------------------------
-- [V5] Coluna created_at em favorites
-- ---------------------------------------------------------------------------
ALTER TABLE favorites
    ADD COLUMN IF NOT EXISTS created_at timestamp;

-- ---------------------------------------------------------------------------
-- [Manual] Colunas de matriz/filial em barbershops
-- (foram aplicadas manualmente fora do Flyway — registradas aqui)
-- ---------------------------------------------------------------------------
ALTER TABLE "barbershops"
    ADD COLUMN IF NOT EXISTS "is_active"            boolean     NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS "is_headquarter"        boolean     NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS "parent_barbershop_id"  varchar(40) DEFAULT NULL;

-- CHECK constraint de consistência matriz/filial
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE conname = 'chk_barbershop_branch'
        ) THEN
            ALTER TABLE "barbershops"
                ADD CONSTRAINT chk_barbershop_branch CHECK (
                    ("parent_barbershop_id" IS NULL     AND "is_headquarter" = true)
                        OR
                    ("parent_barbershop_id" IS NOT NULL AND "is_headquarter" = false)
                    );
        END IF;
    END$$;

-- FK de auto-referência para hierarquia matriz/filial
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE conname = 'fk_barbershops_parent'
        ) THEN
            ALTER TABLE "barbershops"
                ADD CONSTRAINT fk_barbershops_parent
                    FOREIGN KEY ("parent_barbershop_id") REFERENCES "barbershops" ("id")
                        DEFERRABLE INITIALLY IMMEDIATE;
        END IF;
    END$$;