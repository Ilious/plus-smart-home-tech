CREATE TABLE IF NOT EXISTS products (
    productId UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    image_src VARCHAR,
    price DECIMAL CHECK (price > 0),
    quantity_state VARCHAR NOT NULL,
    product_state VARCHAR NOT NULL,
    product_category VARCHAR NOT NULL
)
