CREATE TABLE quotations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL,
    work_order_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    subtotal DECIMAL(19,2) NOT NULL,
    tax DECIMAL(19,2) NOT NULL,
    total DECIMAL(19,2) NOT NULL,
    public_token VARCHAR(100) NULL,
    sent_at DATETIME(6) NULL,
    customer_decision_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_quotations PRIMARY KEY (id),
    CONSTRAINT uk_quotations_code UNIQUE (code),
    CONSTRAINT uk_quotations_public_token UNIQUE (public_token),
    CONSTRAINT fk_quotations_work_order FOREIGN KEY (work_order_id) REFERENCES work_orders (id)
);

CREATE INDEX idx_quotations_public_token ON quotations (public_token);
CREATE INDEX idx_quotations_work_order_id ON quotations (work_order_id);
CREATE INDEX idx_quotations_status ON quotations (status);
