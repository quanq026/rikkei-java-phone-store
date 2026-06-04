DROP TABLE IF EXISTS customer, product CASCADE;

-- Table product
CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL
);

-- Table customer
CREATE TABLE IF NOT EXISTS customer (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    address VARCHAR(255)
);

-- Stored Procedure add_product
CREATE OR REPLACE PROCEDURE add_product(
    p_name VARCHAR(100),
    p_brand VARCHAR(50),
    p_price DOUBLE PRECISION,
    p_stock INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO product(name, brand, price, stock)
    VALUES(p_name, p_brand, p_price, p_stock);
END;
$$;

-- Stored Procedure add_customer
CREATE OR REPLACE PROCEDURE add_customer(
    p_name VARCHAR(100),
    p_phone VARCHAR(20),
    p_email VARCHAR(100),
    p_address VARCHAR(255)
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO customer(name, phone, email, address)
    VALUES(p_name, p_phone, p_email, p_address);
END;
$$;
