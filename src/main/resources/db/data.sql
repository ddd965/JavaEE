-- 初始测试数据

INSERT INTO user (id, username, password, email, phone, create_time) VALUES
(1, 'admin', '0192023a7bbd73250516f069df18b500', 'admin@example.com', '13800000001', NOW()),
(2, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', 'zhangsan@example.com', '13800000002', NOW());

INSERT INTO user_profile (id, user_id, real_name, address, points) VALUES
(1, 1, '管理员', '北京市朝阳区', 1000),
(2, 2, '张三', '上海市浦东新区', 200);

INSERT INTO product (id, name, price, stock, category, status) VALUES
(1, 'iPhone 15', 5999.00, 100, '手机', 1),
(2, 'MacBook Pro', 12999.00, 50, '电脑', 1),
(3, 'AirPods Pro', 1899.00, 200, '配件', 1);

INSERT INTO cart (id, user_id, product_id, quantity, create_time, update_time) VALUES
(1, 2, 1, 1, NOW(), NOW()),
(2, 2, 3, 2, NOW(), NOW());

INSERT INTO `order` (id, order_no, user_id, total_price, status, create_time) VALUES
(1, 'ORD202405150001', 2, 7898.00, 1, NOW());

INSERT INTO order_item (id, order_id, product_id, quantity, subtotal) VALUES
(1, 1, 1, 1, 5999.00),
(2, 1, 3, 1, 1899.00);
