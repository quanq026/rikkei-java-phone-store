DROP TABLE IF EXISTS payment, booking, room, invoice_details, invoice, product, customer, admin CASCADE;

-- 1. Table admin
CREATE TABLE IF NOT EXISTS admin (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 2. Table product
CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL
);

-- 3. Table customer
CREATE TABLE IF NOT EXISTS customer (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    address VARCHAR(255)
);

-- 4. Table invoice
CREATE TABLE IF NOT EXISTS invoice (
    id SERIAL PRIMARY KEY,
    customer_id INT REFERENCES customer(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(12,2) NOT NULL
);

-- 5. Table invoice_details
CREATE TABLE IF NOT EXISTS invoice_details (
    id SERIAL PRIMARY KEY,
    invoice_id INT REFERENCES invoice(id) ON DELETE CASCADE,
    product_id INT REFERENCES product(id) ON DELETE CASCADE,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL
);

-- Seed initial admin: username='admin', password='admin123' (Caesar Cipher +5 encrypted -> firns678)
INSERT INTO admin (username, password)
VALUES ('admin', 'firns678');

-- Seed initial products
INSERT INTO product (name, brand, price, stock)
VALUES 
('iPhone 15 Pro Max', 'Apple', 1400.0, 10),
('Galaxy S24 Ultra', 'Samsung', 1200.0, 15),
('Xiaomi 14 Ultra', 'Xiaomi', 999.0, 8);

-- Seed initial customers
INSERT INTO customer (name, phone, email, address)
VALUES
('Nguyen Van A', '0912345678', 'a.nguyen@example.com', 'Hanoi, Vietnam'),
('Tran Thi B', '0987654321', 'b.tran@example.com', 'Ho Chi Minh, Vietnam');

-- STORED PROCEDURES
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

-- DATABASE TRIGGERS
CREATE OR REPLACE FUNCTION f_check_product_price()
RETURNS TRIGGER
AS $$
BEGIN
    IF NEW.price <= 0 THEN
        RAISE EXCEPTION 'Giá sản phẩm phải lớn hơn 0 !';
    END IF;
    IF NEW.stock < 0 THEN
        RAISE EXCEPTION 'Số lượng tồn kho không thể âm !';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER check_product_price
BEFORE INSERT OR UPDATE ON product
FOR EACH ROW
EXECUTE FUNCTION f_check_product_price();

CREATE OR REPLACE FUNCTION f_update_product_stock_on_invoice()
RETURNS TRIGGER
AS $$
DECLARE
    current_stock INT;
BEGIN
    SELECT stock INTO current_stock FROM product WHERE id = NEW.product_id;
    IF current_stock < NEW.quantity THEN
        RAISE EXCEPTION 'Số lượng tồn kho không đủ cho sản phẩm ID % !', NEW.product_id;
    END IF;

    UPDATE product
    SET stock = stock - NEW.quantity
    WHERE id = NEW.product_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER update_product_stock_on_invoice
AFTER INSERT ON invoice_details
FOR EACH ROW
EXECUTE FUNCTION f_update_product_stock_on_invoice();

-- DATABASE VIEWS
CREATE OR REPLACE VIEW v_invoice_details_summary AS
SELECT d.id AS detail_id, i.id AS invoice_id, c.name AS customer_name,
       p.name AS product_name, d.quantity, d.unit_price, (d.quantity * d.unit_price) AS subtotal,
       i.created_at
FROM invoice_details d
JOIN invoice i ON d.invoice_id = i.id
JOIN customer c ON i.customer_id = c.id
JOIN product p ON d.product_id = p.id;

CREATE OR REPLACE VIEW v_high_value_invoices AS
SELECT i.id AS invoice_id, c.name AS customer_name, i.total_amount, i.created_at
FROM invoice i
LEFT JOIN customer c ON i.customer_id = c.id
WHERE i.total_amount > 1000;
