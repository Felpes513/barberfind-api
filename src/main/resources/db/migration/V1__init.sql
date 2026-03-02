-- ==========================================
-- V1__init.sql  (CUID as primary key)
-- ==========================================

-- Convenção: CUID (string) com 25 chars
-- Geração do ID: aplicação (Java), não o banco.

CREATE TABLE users (
                       id varchar(25) PRIMARY KEY,
                       name varchar,
                       email varchar UNIQUE,
                       password_hash varchar,
                       phone varchar,
                       birth_date date,
                       hair_type varchar,
                       hair_texture varchar,
                       has_beard boolean,
                       created_at timestamp NOT NULL DEFAULT now(),
                       updated_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE user_documents (
                                id varchar(25) PRIMARY KEY,
                                user_id varchar(25) NOT NULL,
                                document_type varchar,
                                document_number varchar,
                                created_at timestamp NOT NULL DEFAULT now(),
                                CONSTRAINT fk_user_documents_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_payment_methods (
                                      id varchar(25) PRIMARY KEY,
                                      user_id varchar(25) NOT NULL,
                                      provider varchar,
                                      provider_customer_id varchar,
                                      created_at timestamp NOT NULL DEFAULT now(),
                                      CONSTRAINT fk_user_payment_methods_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE barbershops (
                             id varchar(25) PRIMARY KEY,
                             name varchar,
                             cnpj varchar,
                             description text,
                             phone varchar,
                             email varchar,
                             address varchar,
                             neighborhood varchar,
                             city varchar,
                             state varchar,
                             opening_time time,
                             closing_time time,
                             created_at timestamp NOT NULL DEFAULT now(),
                             updated_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE barbershop_photos (
                                   id varchar(25) PRIMARY KEY,
                                   barbershop_id varchar(25) NOT NULL,
                                   image_url varchar,
                                   created_at timestamp NOT NULL DEFAULT now(),
                                   CONSTRAINT fk_barbershop_photos_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershops (id)
);

CREATE TABLE barbers (
                         id varchar(25) PRIMARY KEY,
                         name varchar,
                         bio text,
                         years_experience int,
                         rating decimal,
                         created_at timestamp NOT NULL DEFAULT now(),
                         updated_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE barbershop_barbers (
                                    id varchar(25) PRIMARY KEY,
                                    barbershop_id varchar(25) NOT NULL,
                                    barber_id varchar(25) NOT NULL,
                                    active boolean DEFAULT true,
                                    CONSTRAINT fk_barbershop_barbers_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershops (id),
                                    CONSTRAINT fk_barbershop_barbers_barber FOREIGN KEY (barber_id) REFERENCES barbers (id),
                                    CONSTRAINT uq_barbershop_barber UNIQUE (barbershop_id, barber_id)
);

CREATE TABLE services (
                          id varchar(25) PRIMARY KEY,
                          name varchar,
                          description text,
                          base_price decimal,
                          duration_minutes int
);

CREATE TABLE barbershop_services (
                                     id varchar(25) PRIMARY KEY,
                                     barbershop_id varchar(25) NOT NULL,
                                     service_id varchar(25) NOT NULL,
                                     custom_price decimal,
                                     CONSTRAINT fk_barbershop_services_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershops (id),
                                     CONSTRAINT fk_barbershop_services_service FOREIGN KEY (service_id) REFERENCES services (id),
                                     CONSTRAINT uq_barbershop_service UNIQUE (barbershop_id, service_id)
);

CREATE TABLE barber_services (
                                 id varchar(25) PRIMARY KEY,
                                 barber_id varchar(25) NOT NULL,
                                 service_id varchar(25) NOT NULL,
                                 CONSTRAINT fk_barber_services_barber FOREIGN KEY (barber_id) REFERENCES barbers (id),
                                 CONSTRAINT fk_barber_services_service FOREIGN KEY (service_id) REFERENCES services (id),
                                 CONSTRAINT uq_barber_service UNIQUE (barber_id, service_id)
);

CREATE TABLE appointments (
                              id varchar(25) PRIMARY KEY,
                              user_id varchar(25) NOT NULL,
                              barbershop_id varchar(25) NOT NULL,
                              barber_id varchar(25) NOT NULL,
                              service_id varchar(25) NOT NULL,
                              scheduled_at timestamp NOT NULL,
                              completed_at timestamp,
                              status varchar NOT NULL, -- scheduled, completed, canceled, no_show
                              cancellation_reason varchar,
                              final_price decimal,
                              payment_method varchar, -- pix, dinheiro, cartao
                              created_at timestamp NOT NULL DEFAULT now(),
                              CONSTRAINT fk_appointments_user FOREIGN KEY (user_id) REFERENCES users (id),
                              CONSTRAINT fk_appointments_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershops (id),
                              CONSTRAINT fk_appointments_barber FOREIGN KEY (barber_id) REFERENCES barbers (id),
                              CONSTRAINT fk_appointments_service FOREIGN KEY (service_id) REFERENCES services (id)
);

CREATE TABLE products (
                          id varchar(25) PRIMARY KEY,
                          barbershop_id varchar(25) NOT NULL,
                          name varchar,
                          price decimal,
                          stock_quantity int,
                          created_at timestamp NOT NULL DEFAULT now(),
                          CONSTRAINT fk_products_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershops (id)
);

CREATE TABLE sales (
                       id varchar(25) PRIMARY KEY,
                       barbershop_id varchar(25) NOT NULL,
                       barber_id varchar(25),
                       total_amount decimal,
                       created_at timestamp NOT NULL DEFAULT now(),
                       CONSTRAINT fk_sales_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershops (id),
                       CONSTRAINT fk_sales_barber FOREIGN KEY (barber_id) REFERENCES barbers (id)
);

CREATE TABLE sale_items (
                            id varchar(25) PRIMARY KEY,
                            sale_id varchar(25) NOT NULL,
                            product_id varchar(25) NOT NULL,
                            quantity int,
                            unit_price decimal,
                            CONSTRAINT fk_sale_items_sale FOREIGN KEY (sale_id) REFERENCES sales (id),
                            CONSTRAINT fk_sale_items_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE reviews (
                         id varchar(25) PRIMARY KEY,
                         user_id varchar(25) NOT NULL,
                         barbershop_id varchar(25),
                         barber_id varchar(25),
                         rating int,
                         comment text,
                         created_at timestamp NOT NULL DEFAULT now(),
                         CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id),
                         CONSTRAINT fk_reviews_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershops (id),
                         CONSTRAINT fk_reviews_barber FOREIGN KEY (barber_id) REFERENCES barbers (id)
);

CREATE TABLE barber_portfolio (
                                  id varchar(25) PRIMARY KEY,
                                  barber_id varchar(25) NOT NULL,
                                  image_url varchar,
                                  hair_type varchar,
                                  style_tag varchar,
                                  created_at timestamp NOT NULL DEFAULT now(),
                                  CONSTRAINT fk_barber_portfolio_barber FOREIGN KEY (barber_id) REFERENCES barbers (id)
);

CREATE TABLE favorites (
                           id varchar(25) PRIMARY KEY,
                           user_id varchar(25) NOT NULL,
                           barber_id varchar(25),
                           barbershop_id varchar(25),
                           CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users (id),
                           CONSTRAINT fk_favorites_barber FOREIGN KEY (barber_id) REFERENCES barbers (id),
                           CONSTRAINT fk_favorites_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershops (id)
);

CREATE TABLE barber_availability (
                                     id varchar(25) PRIMARY KEY,
                                     barber_id varchar(25) NOT NULL,
                                     weekday int NOT NULL, -- 0-6
                                     start_time time NOT NULL,
                                     end_time time NOT NULL,
                                     CONSTRAINT fk_barber_availability_barber FOREIGN KEY (barber_id) REFERENCES barbers (id)
);

CREATE TABLE subscriptions (
                               id varchar(25) PRIMARY KEY,
                               barbershop_id varchar(25) NOT NULL,
                               plan_type varchar,
                               status varchar,
                               expires_at timestamp,
                               CONSTRAINT fk_subscriptions_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershops (id)
);

CREATE TABLE activity_logs (
                               id varchar(25) PRIMARY KEY,
                               entity_type varchar,
                               entity_id varchar(25),
                               action varchar,
                               old_data json,
                               new_data json,
                               created_at timestamp NOT NULL DEFAULT now()
);

-- Indexes úteis (MVP)
CREATE INDEX idx_barbershops_city ON barbershops(city);
CREATE INDEX idx_barbershops_neighborhood ON barbershops(neighborhood);
CREATE INDEX idx_appointments_scheduled_at ON appointments(scheduled_at);
CREATE INDEX idx_reviews_barbershop_id ON reviews(barbershop_id);
CREATE INDEX idx_reviews_barber_id ON reviews(barber_id);
CREATE INDEX idx_favorites_user_id ON favorites(user_id);