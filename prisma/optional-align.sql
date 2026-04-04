-- Optional: if barbershops / favorites lack columns expected by Prisma schema (JPA drift vs Flyway V1).
-- Safe to run multiple times.

ALTER TABLE barbershops ADD COLUMN IF NOT EXISTS is_active boolean NOT NULL DEFAULT true;
ALTER TABLE barbershops ADD COLUMN IF NOT EXISTS is_headquarter boolean NOT NULL DEFAULT true;
ALTER TABLE barbershops ADD COLUMN IF NOT EXISTS parent_barbershop_id varchar(40);

ALTER TABLE favorites ADD COLUMN IF NOT EXISTS created_at timestamp;
