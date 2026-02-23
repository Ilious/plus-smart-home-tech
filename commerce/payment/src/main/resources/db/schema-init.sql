CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID PRIMARY KEY NOT NULL,
    order_id UUID NOT NULL,
    delivery_total DECIMAL(10,2) NOT NULL,
    total_payment DECIMAL(12,2) NOT NULL,
    fee_total DECIMAL(10,2) NOT NULL,
    payment_state VARCHAR(20) NOT NULL
);