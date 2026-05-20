INSERT INTO users (full_name, email, password, role, active, created_at, updated_at)
VALUES (
    'Admin Taller360',
    'admin@taller360.com',
    '$2a$10$zJH7LWscrzWzAbNPpcxMnuwXpy3WPvHrRkpT0SCkCINU8aosHmqU6',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP(6),
    CURRENT_TIMESTAMP(6)
);
