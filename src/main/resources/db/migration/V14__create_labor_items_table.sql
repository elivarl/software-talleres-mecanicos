CREATE TABLE labor_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    work_order_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(19,2) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_labor_items_work_order FOREIGN KEY (work_order_id) REFERENCES work_orders (id)
);

CREATE INDEX idx_labor_items_work_order_id ON labor_items (work_order_id);
