CREATE TABLE IF NOT EXISTS orders
(
    order_id         UUID         NOT NULL PRIMARY KEY,
    username         VARCHAR,
    shopping_cart_id UUID         NOT NULL,
    payment_id       UUID,
    delivery_id      UUID,
    state            VARCHAR(32) NOT NULL,
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN DEFAULT FALSE,
    total_price      DECIMAL(19, 2),
    product_price    DECIMAL(19, 2),
    delivery_price   DECIMAL(19, 2)
);

CREATE TABLE IF NOT EXISTS orders_products
(
    order_id    UUID NOT NULL,
    product_id UUID,
    quantity    INTEGER,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders (order_id)
);