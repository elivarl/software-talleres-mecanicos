CREATE TABLE work_order_parts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    work_order_id BIGINT NOT NULL,
    inventory_item_id BIGINT NOT NULL,
    quantity DECIMAL(19,2) NOT NULL,
    unit_cost DECIMAL(19,2) NOT NULL,
    sale_price DECIMAL(19,2) NOT NULL,
    total DECIMAL(19,2) NOT NULL,
    margin DECIMAL(19,2) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_work_order_parts PRIMARY KEY (id),
    CONSTRAINT fk_work_order_parts_work_order FOREIGN KEY (work_order_id) REFERENCES work_orders (id),
    CONSTRAINT fk_work_order_parts_inventory_item FOREIGN KEY (inventory_item_id) REFERENCES inventory_items (id)
);

CREATE INDEX idx_work_order_parts_work_order_id ON work_order_parts (work_order_id);
CREATE INDEX idx_work_order_parts_inventory_item_id ON work_order_parts (inventory_item_id);
