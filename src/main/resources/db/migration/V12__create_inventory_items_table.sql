CREATE TABLE inventory_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    sku VARCHAR(80) NOT NULL,
    description VARCHAR(255) NULL,
    current_stock DECIMAL(19,2) NOT NULL,
    min_stock DECIMAL(19,2) NOT NULL,
    unit_cost DECIMAL(19,2) NOT NULL,
    sale_price DECIMAL(19,2) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_inventory_items PRIMARY KEY (id),
    CONSTRAINT uk_inventory_items_sku UNIQUE (sku)
);

CREATE INDEX idx_inventory_items_name ON inventory_items (name);
CREATE INDEX idx_inventory_items_sku ON inventory_items (sku);
