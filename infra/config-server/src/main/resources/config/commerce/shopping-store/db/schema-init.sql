CREATE TABLE IF NOT EXISTS products (
    productId BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    image_src VARCHAR,
    price DECIMAL CHECK (price > 0),
    quantityState VARCHAR NOT NULL,
    productState VARCHAR NOT NULL,
    category VARCHAR NOT NULL
)
