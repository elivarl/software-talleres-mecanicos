CREATE TABLE vehicles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    plate VARCHAR(20) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year_value INT NULL,
    color VARCHAR(50) NULL,
    vin VARCHAR(100) NULL,
    mileage BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_vehicles PRIMARY KEY (id),
    CONSTRAINT uk_vehicles_plate UNIQUE (plate),
    CONSTRAINT fk_vehicles_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE INDEX idx_vehicles_plate ON vehicles (plate);
CREATE INDEX idx_vehicles_customer_id ON vehicles (customer_id);
