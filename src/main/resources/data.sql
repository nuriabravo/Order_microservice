
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    cart_id BIGINT,
    country_id BIGINT,
    from_address VARCHAR(150),
    status ENUM('UNPAID','PAID','SENT','IN_DELIVERY','CANCELLED','DELIVERED','UNKNOWN', 'RETURNED'),
    date_ordered DATETIME,
    date_delivered DATETIME,
    total_price DOUBLE
);

INSERT INTO orders (cart_id, user_id, country_id, from_address, status, date_ordered, date_delivered, total_price)
VALUES (1001, 1, 1, '123 Main St', 'PAID', '2024-05-07 08:00:00', '2024-05-10 15:00:00', 18);

INSERT INTO orders (cart_id, user_id, country_id, from_address, status, date_ordered, date_delivered, total_price)
VALUES (1002, 2, 1, '456 Elm St', 'UNPAID', '2024-05-08 09:00:00', '2024-05-10 16:00:00', 18);

INSERT INTO orders (cart_id, user_id, country_id, from_address, status, date_ordered, date_delivered, total_price)
VALUES (1003, 2, 1, '789 Oak St', 'IN_DELIVERY', '2024-05-09 10:00:00', '2024-05-11 17:00:00', 17);

INSERT INTO orders (cart_id, user_id, country_id, from_address, status, date_ordered, date_delivered, total_price)
VALUES (1004, 1, 1, '101 Maple Ave', 'DELIVERED', '2024-05-10 11:00:00', '2024-05-12 18:00:00', 17);

INSERT INTO orders (cart_id, user_id, country_id, from_address, status, date_ordered, date_delivered, total_price)
VALUES (1005, 2, 1, '222 Pine St', 'UNKNOWN', '2024-05-11 12:00:00', '2024-05-10 14:00:00', 16);

INSERT INTO orders (cart_id, user_id, country_id, from_address, status, date_ordered, date_delivered, total_price)
VALUES (1006, 2, 1, '333 Cedar Rd', 'PAID', '2024-05-12 13:00:00', '2024-05-14 19:00:00', 18);



CREATE TABLE IF NOT EXISTS ordered_products (
    order_id LONG,
    product_id LONG,
    name VARCHAR(150),
    category VARCHAR(150),
    description VARCHAR(150),
    price LONG,
    quantity LONG,
    CONSTRAINT PK_orderedProducts PRIMARY KEY (order_id,product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS addresses (
    order_id LONG AUTO_INCREMENT PRIMARY KEY,
    street VARCHAR(255),
    number INT,
    door VARCHAR(255),
    city_name VARCHAR(255),
    zip_code VARCHAR(10),
    country_id LONG,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

INSERT INTO addresses (order_id, street, number, door, city_name, zip_code, country_id) VALUES
(1, 'Main Street', 123, 'A', 'Springfield', '12345', 1),
(2, 'Elm Street', 456, 'B', 'Shelbyville', '67890', 2),
(3, 'Oak Street', 789, 'C', 'Capital City', '10112', 3),
(4, 'Oak Street', 789, 'C', 'Capital City', '10112', 3),
(5, 'Oak Street', 789, 'C', 'Capital City', '10112', 3),
(6, 'Oak Street', 789, 'C', 'Capital City', '10112', 3);


INSERT INTO ordered_products (order_id, product_id, name, category, description, price, quantity)
VALUES (1, 1, 'Product1', 'Category1', 'Description1', 50, 2);

INSERT INTO ordered_products (order_id, product_id, name, category, description, price, quantity)
VALUES (1, 2, 'Product2', 'Category2', 'Description2', 30, 1);

INSERT INTO ordered_products (order_id, product_id, name, category, description, price, quantity)
VALUES (1, 3, 'Product3', 'Category3', 'Description3', 40, 3);

INSERT INTO ordered_products (order_id, product_id, name, category, description, price, quantity)
VALUES (2, 1004, 'Product4', 'Category4', 'Description4', 60, 2);

INSERT INTO ordered_products (order_id, product_id, name, category, description, price, quantity)
VALUES (3, 1005, 'Product5', 'Category5', 'Description5', 70, 1);

INSERT INTO ordered_products (order_id, product_id, name, category, description, price, quantity)
VALUES (4, 1006, 'Product6', 'Category6', 'Description6', 80, 4);

INSERT INTO ordered_products (order_id, product_id, name, category, description, price, quantity)
VALUES (4, 1007, 'Product7', 'Category7', 'Description7', 90, 2);

INSERT INTO ordered_products (order_id, product_id, name, category, description, price, quantity)
VALUES (5, 1008, 'Product8', 'Category8', 'Description8', 100, 3);

INSERT INTO ordered_products (order_id, product_id, name, category, description, price, quantity)
VALUES (5, 1009, 'Product9', 'Category9', 'Description9', 110, 1);

INSERT INTO ordered_products (order_id, product_id, name, category, description, price, quantity)
VALUES (6, 1010, 'Product10', 'Category10', 'Description10', 120, 5);
