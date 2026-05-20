CREATE TABLE inspection_photos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    inspection_id BIGINT NOT NULL,
    photo_url VARCHAR(500) NOT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_inspection_photos PRIMARY KEY (id),
    CONSTRAINT fk_inspection_photos_inspection FOREIGN KEY (inspection_id) REFERENCES reception_inspections (id)
);
