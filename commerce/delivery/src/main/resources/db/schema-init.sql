CREATE TABLE IF NOT EXISTS addresses (
                                         address_id UUID NOT NULL PRIMARY KEY,
                                         country VARCHAR(2) NOT NULL,
                                         city VARCHAR(100) NOT NULL,
                                         street VARCHAR(150) NOT NULL,
                                         house VARCHAR(15) NOT NULL,
                                         flat VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS deliveries (
                                          delivery_id UUID NOT NULL PRIMARY KEY,
                                          delivery_weight DOUBLE PRECISION NOT NULL,
                                          delivery_volume DOUBLE PRECISION NOT NULL,
                                          fragile BOOLEAN NOT NULL,
                                          from_address_id UUID NOT NULL,
                                          to_address_id UUID NOT NULL,
                                          delivery_state VARCHAR(20) NOT NULL,
                                          order_id UUID NOT NULL,
                                          FOREIGN KEY (from_address_id) REFERENCES addresses (address_id),
                                          FOREIGN KEY (to_address_id) REFERENCES addresses (address_id)
);

