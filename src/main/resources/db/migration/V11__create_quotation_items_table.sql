CREATE TABLE quotation_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quotation_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity DECIMAL(19,2) NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    total DECIMAL(19,2) NOT NULL,
    CONSTRAINT pk_quotation_items PRIMARY KEY (id),
    CONSTRAINT fk_quotation_items_quotation FOREIGN KEY (quotation_id) REFERENCES quotations (id)
);

CREATE INDEX idx_quotation_items_quotation_id ON quotation_items (quotation_id);
