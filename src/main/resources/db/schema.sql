CREATE TABLE IF NOT EXISTS coupons (
    id              UUID            PRIMARY KEY,
    code            VARCHAR(6)      NOT NULL UNIQUE,
    description     VARCHAR(255)    NOT NULL,
    discount_value  NUMERIC(10, 2)  NOT NULL CHECK (discount_value >= 0.5),
    expiration_date DATE            NOT NULL,
    published       BOOLEAN         NOT NULL DEFAULT FALSE,
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL
);
