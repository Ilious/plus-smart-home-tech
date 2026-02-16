CREATE TABLE IF NOT EXISTS shopping_carts (
    shopping_cart_id BIGINT GENERATED ALWAYS AS  IDENTITY PRIMARY KEY,
    shopping_cart_state VARCHAR,
    username VARCHAR
);

CREATE TABLE IF NOT EXISTS shopping_carts_products (
    shopping_cart_id uuid NOT NULL references shopping_carts(shopping_cart_id),
    product_id uuid NOT NULL REFERENCES products(product_id),
    quantity INTEGER NOT NULL,
    PRIMARY KEY (product_id, shopping_cart_id)
);