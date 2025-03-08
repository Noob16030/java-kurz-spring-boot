DROP TABLE IF EXISTS product;
CREATE TABLE product
(
    id            BIGINT AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    description   VARCHAR(1000),
    amount        INT NOT NULL,
    price         DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS order;
CREATE TABLE order
(
    id              BIGINT AUTO_INCREMENT,
    paid            BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS shoppingListItem;
CREATE TABLE shoppingListItem
(
    id BIGINT AUTO_INCREMENT,
    orderId BIGINT NOT NULL,
    productId BIGINT NOT NULL,
    amount BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT order_id_fk FOREIGN KEY (orderId) REFERENCES order (id),
    CONSTRAINT product_id_fk FOREIGN KEY (productId) REFERENCES product (id)
);