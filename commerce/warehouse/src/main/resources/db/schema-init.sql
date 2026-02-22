CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY NOT NULL,
    fragile BOOLEAN NOT NULL,
    weight DOUBLE PRECISION NOT NULL CHECK (weight > 0),
    width DOUBLE PRECISION NOT NULL CHECK (width > 0),
    height DOUBLE PRECISION NOT NULL CHECK (height > 0),
    depth DOUBLE PRECISION NOT NULL CHECK (depth > 0),
    quantity BIGINT NOT NULL CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS order_bookings (
    booking_id UUID NOT NULL PRIMARY KEY,
    order_id UUID NOT NULL,
    delivery_id UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings_products (
    booking_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (booking_id, product_id),
    FOREIGN KEY (booking_id) REFERENCES order_bookings(booking_id) ON DELETE CASCADE
);