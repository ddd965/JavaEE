CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    username VARCHAR(64),
    operation_type VARCHAR(32),
    content VARCHAR(512),
    result VARCHAR(64),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
