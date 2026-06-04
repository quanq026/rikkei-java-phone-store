DROP TABLE IF EXISTS product CASCADE;

-- Table product
CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL
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
