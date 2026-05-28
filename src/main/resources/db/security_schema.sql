-- 安全模块表结构及用户表扩展（实验三）

ALTER TABLE user ADD COLUMN enabled TINYINT(1) NOT NULL DEFAULT 1;
ALTER TABLE user ADD COLUMN account_non_locked TINYINT(1) NOT NULL DEFAULT 1;
ALTER TABLE user ADD COLUMN fail_count INT NOT NULL DEFAULT 0;
ALTER TABLE user ADD COLUMN last_login_time DATETIME NULL;

CREATE TABLE IF NOT EXISTS role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS permission (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS login_log (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT,
    username   VARCHAR(50)  NOT NULL,
    login_time DATETIME     NOT NULL,
    ip         VARCHAR(64),
    status     TINYINT      NOT NULL COMMENT '1成功 0失败'
);

INSERT IGNORE INTO role (id, name, description) VALUES
(1, 'ROLE_ADMIN', '管理员'),
(2, 'ROLE_MANAGER', '经理'),
(3, 'ROLE_USER', '普通用户');

INSERT IGNORE INTO permission (id, name, description) VALUES
(1, 'product:delete', '删除商品'),
(2, 'product:update', '修改商品'),
(3, 'product:query', '查询商品'),
(4, 'order:manage', '订单管理'),
(5, 'order:query', '订单查询');

INSERT IGNORE INTO role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 2), (2, 3), (2, 4), (2, 5),
(3, 3), (3, 5);

INSERT IGNORE INTO user_role (user_id, role_id) VALUES
(1, 1),
(2, 3);
