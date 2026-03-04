-- =============================================================================
-- Migration: user preferences, terms acceptances e colunas de payment_methods
-- =============================================================================

-- 1) Novas colunas na tabela existente user_payment_methods
-- (dados de exibição do cartão — nunca dados sensíveis brutos)
ALTER TABLE "user_payment_methods"
    ADD COLUMN IF NOT EXISTS "card_type"        varchar(10), -- 'CREDIT' | 'DEBIT'
    ADD COLUMN IF NOT EXISTS "last_four_digits" varchar(4), -- ex: '4242'
    ADD COLUMN IF NOT EXISTS "brand"            varchar(30);
-- 'VISA', 'MASTERCARD', 'ELO'

-- 2) Tabela de preferências do usuário (1:1 com users)
CREATE TABLE IF NOT EXISTS "user_preferences"
(
    "id"         varchar(40) PRIMARY KEY,
    "user_id"    varchar(40) NOT NULL UNIQUE,
    "theme"      varchar(10) NOT NULL DEFAULT 'LIGHT', -- 'LIGHT' | 'DARK'
    "language"   varchar(10) NOT NULL DEFAULT 'pt-BR', -- 'pt-BR', 'en-US', 'es-ES' ...
    "updated_at" timestamp,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE
);

-- 3) Tabela de histórico de aceites de termos
CREATE TABLE IF NOT EXISTS "terms_acceptances"
(
    "id"            varchar(40) PRIMARY KEY,
    "user_id"       varchar(40) NOT NULL,
    "terms_version" varchar(20) NOT NULL,
    "accepted_at"   timestamp   NOT NULL,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE
);