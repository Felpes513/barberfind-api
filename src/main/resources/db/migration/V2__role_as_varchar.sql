-- V2__role_as_varchar.sql
-- Converte users.role de enum user_role para varchar(20)

ALTER TABLE "users"
    ALTER COLUMN role TYPE varchar(20)
        USING role::text;

-- mantém o default como antes (opcional, mas recomendado)
ALTER TABLE "users"
    ALTER COLUMN role SET DEFAULT 'CLIENT';

-- tenta remover o enum APENAS se ninguém mais estiver usando
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