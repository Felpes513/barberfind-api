-- ==========================================
-- V1__init.sql  (CUID as primary key)
-- ==========================================
-- Convenção: CUID (string) gerado na aplicação (Java), não no banco.

-- 1) ENUM de roles
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
CREATE TYPE user_role AS ENUM ('CLIENT', 'BARBER', 'OWNER');
END IF;
END$$;

-- 2) Tabelas (IDs em CUID)
CREATE TABLE "users" (
                         "id" varchar(40) PRIMARY KEY,
                         "role" user_role NOT NULL DEFAULT 'CLIENT',
                         "name" varchar,
                         "email" varchar UNIQUE,
                         "password_hash" varchar,
                         "phone" varchar,
                         "birth_date" date,
                         "hair_type" varchar,
                         "hair_texture" varchar,
                         "has_beard" boolean,
                         "created_at" timestamp,
                         "updated_at" timestamp
);

CREATE TABLE "user_documents" (
                                  "id" varchar(40) PRIMARY KEY,
                                  "user_id" varchar(40),
                                  "document_type" varchar,
                                  "document_number" varchar,
                                  "created_at" timestamp
);

CREATE TABLE "user_payment_methods" (
                                        "id" varchar(40) PRIMARY KEY,
                                        "user_id" varchar(40),
                                        "provider" varchar,
                                        "provider_customer_id" varchar,
                                        "created_at" timestamp
);

CREATE TABLE "barbershops" (
                               "id" varchar(40) PRIMARY KEY,
                               "owner_user_id" varchar(40),
                               "name" varchar,
                               "cnpj" varchar,
                               "description" text,
                               "phone" varchar,
                               "email" varchar,
                               "address" varchar,
                               "neighborhood" varchar,
                               "city" varchar,
                               "state" varchar,
                               "opening_time" time,
                               "closing_time" time,
                               "created_at" timestamp,
                               "updated_at" timestamp
);

CREATE TABLE "barbershop_photos" (
                                     "id" varchar(40) PRIMARY KEY,
                                     "barbershop_id" varchar(40),
                                     "image_url" varchar,
                                     "created_at" timestamp
);

CREATE TABLE "barbers" (
                           "id" varchar(40) PRIMARY KEY,
                           "user_id" varchar(40) UNIQUE,
                           "name" varchar,
                           "bio" text,
                           "years_experience" int,
                           "rating" decimal,
                           "created_at" timestamp,
                           "updated_at" timestamp
);

CREATE TABLE "barbershop_barbers" (
                                      "id" varchar(40) PRIMARY KEY,
                                      "barbershop_id" varchar(40),
                                      "barber_id" varchar(40),
                                      "active" boolean
);

CREATE TABLE "services" (
                            "id" varchar(40) PRIMARY KEY,
                            "name" varchar,
                            "description" text,
                            "base_price" decimal,
                            "duration_minutes" int
);

CREATE TABLE "barbershop_services" (
                                       "id" varchar(40) PRIMARY KEY,
                                       "barbershop_id" varchar(40),
                                       "service_id" varchar(40),
                                       "custom_price" decimal
);

CREATE TABLE "barber_services" (
                                   "id" varchar(40) PRIMARY KEY,
                                   "barber_id" varchar(40),
                                   "service_id" varchar(40)
);

CREATE TABLE "appointments" (
                                "id" varchar(40) PRIMARY KEY,
                                "user_id" varchar(40),
                                "barbershop_id" varchar(40),
                                "barber_id" varchar(40),
                                "service_id" varchar(40),
                                "scheduled_at" timestamp,
                                "completed_at" timestamp,
                                "status" varchar,
                                "cancellation_reason" varchar,
                                "final_price" decimal,
                                "payment_method" varchar,
                                "created_at" timestamp
);

CREATE TABLE "products" (
                            "id" varchar(40) PRIMARY KEY,
                            "barbershop_id" varchar(40),
                            "name" varchar,
                            "price" decimal,
                            "stock_quantity" int,
                            "created_at" timestamp
);

CREATE TABLE "sales" (
                         "id" varchar(40) PRIMARY KEY,
                         "barbershop_id" varchar(40),
                         "barber_id" varchar(40),
                         "total_amount" decimal,
                         "created_at" timestamp
);

CREATE TABLE "sale_items" (
                              "id" varchar(40) PRIMARY KEY,
                              "sale_id" varchar(40),
                              "product_id" varchar(40),
                              "quantity" int,
                              "unit_price" decimal
);

CREATE TABLE "reviews" (
                           "id" varchar(40) PRIMARY KEY,
                           "user_id" varchar(40),
                           "barbershop_id" varchar(40),
                           "barber_id" varchar(40),
                           "rating" int,
                           "comment" text,
                           "created_at" timestamp
);

CREATE TABLE "barber_portfolio" (
                                    "id" varchar(40) PRIMARY KEY,
                                    "barber_id" varchar(40),
                                    "image_url" varchar,
                                    "hair_type" varchar,
                                    "style_tag" varchar,
                                    "created_at" timestamp
);

CREATE TABLE "favorites" (
                             "id" varchar(40) PRIMARY KEY,
                             "user_id" varchar(40),
                             "barber_id" varchar(40),
                             "barbershop_id" varchar(40)
);

CREATE TABLE "barber_availability" (
                                       "id" varchar(40) PRIMARY KEY,
                                       "barber_id" varchar(40),
                                       "weekday" int,
                                       "start_time" time,
                                       "end_time" time
);

CREATE TABLE "subscriptions" (
                                 "id" varchar(40) PRIMARY KEY,
                                 "barbershop_id" varchar(40),
                                 "plan_type" varchar,
                                 "status" varchar,
                                 "expires_at" timestamp
);

CREATE TABLE "activity_logs" (
                                 "id" varchar(40) PRIMARY KEY,
                                 "entity_type" varchar,
                                 "entity_id" varchar(40),
                                 "action" varchar,
                                 "old_data" json,
                                 "new_data" json,
                                 "created_at" timestamp
);

-- 3) Foreign Keys (mantendo as mesmas relações)
-- + adicionando os vínculos mínimos

ALTER TABLE "user_documents"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "user_payment_methods"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barbershops"
    ADD FOREIGN KEY ("owner_user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barbers"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barbershop_photos"
    ADD FOREIGN KEY ("barbershop_id") REFERENCES "barbershops" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barbershop_barbers"
    ADD FOREIGN KEY ("barbershop_id") REFERENCES "barbershops" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barbershop_barbers"
    ADD FOREIGN KEY ("barber_id") REFERENCES "barbers" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barbershop_services"
    ADD FOREIGN KEY ("barbershop_id") REFERENCES "barbershops" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barbershop_services"
    ADD FOREIGN KEY ("service_id") REFERENCES "services" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barber_services"
    ADD FOREIGN KEY ("barber_id") REFERENCES "barbers" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barber_services"
    ADD FOREIGN KEY ("service_id") REFERENCES "services" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "appointments"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "appointments"
    ADD FOREIGN KEY ("barbershop_id") REFERENCES "barbershops" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "appointments"
    ADD FOREIGN KEY ("barber_id") REFERENCES "barbers" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "appointments"
    ADD FOREIGN KEY ("service_id") REFERENCES "services" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "products"
    ADD FOREIGN KEY ("barbershop_id") REFERENCES "barbershops" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "sales"
    ADD FOREIGN KEY ("barbershop_id") REFERENCES "barbershops" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "sales"
    ADD FOREIGN KEY ("barber_id") REFERENCES "barbers" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "sale_items"
    ADD FOREIGN KEY ("sale_id") REFERENCES "sales" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "sale_items"
    ADD FOREIGN KEY ("product_id") REFERENCES "products" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "reviews"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "reviews"
    ADD FOREIGN KEY ("barbershop_id") REFERENCES "barbershops" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "reviews"
    ADD FOREIGN KEY ("barber_id") REFERENCES "barbers" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barber_portfolio"
    ADD FOREIGN KEY ("barber_id") REFERENCES "barbers" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "favorites"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "favorites"
    ADD FOREIGN KEY ("barber_id") REFERENCES "barbers" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "favorites"
    ADD FOREIGN KEY ("barbershop_id") REFERENCES "barbershops" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "barber_availability"
    ADD FOREIGN KEY ("barber_id") REFERENCES "barbers" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "subscriptions"
    ADD FOREIGN KEY ("barbershop_id") REFERENCES "barbershops" ("id") DEFERRABLE INITIALLY IMMEDIATE;