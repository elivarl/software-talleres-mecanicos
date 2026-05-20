CREATE TABLE customers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(150) NOT NULL,
    identification VARCHAR(50) NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(150) NULL,
    address VARCHAR(255) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_customers PRIMARY KEY (id),
    CONSTRAINT uk_customers_identification UNIQUE (identification)
);

CREATE INDEX idx_customers_full_name ON customers (full_name);
CREATE INDEX idx_customers_identification ON customers (identification);
CREATE INDEX idx_customers_phone ON customers (phone);
