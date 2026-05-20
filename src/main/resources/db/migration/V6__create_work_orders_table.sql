CREATE TABLE work_orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL,
    customer_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    assigned_mechanic_id BIGINT NULL,
    status VARCHAR(30) NOT NULL,
    reception_date DATETIME(6) NOT NULL,
    estimated_delivery_date DATE NULL,
    current_mileage BIGINT NOT NULL,
    fuel_level VARCHAR(50) NULL,
    customer_complaint TEXT NOT NULL,
    initial_observations TEXT NULL,
    diagnosis TEXT NULL,
    internal_notes TEXT NULL,
    quality_control_completed BOOLEAN NOT NULL DEFAULT FALSE,
    quality_control_notes TEXT NULL,
    ready_at DATETIME(6) NULL,
    delivered_at DATETIME(6) NULL,
    delivered_to VARCHAR(150) NULL,
    final_mileage BIGINT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_work_orders PRIMARY KEY (id),
    CONSTRAINT uk_work_orders_code UNIQUE (code),
    CONSTRAINT fk_work_orders_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_work_orders_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id),
    CONSTRAINT fk_work_orders_assigned_mechanic FOREIGN KEY (assigned_mechanic_id) REFERENCES users (id)
);

CREATE INDEX idx_work_orders_status ON work_orders (status);
CREATE INDEX idx_work_orders_code ON work_orders (code);
CREATE INDEX idx_work_orders_vehicle_id ON work_orders (vehicle_id);
CREATE INDEX idx_work_orders_customer_id ON work_orders (customer_id);
CREATE INDEX idx_work_orders_reception_date ON work_orders (reception_date);
