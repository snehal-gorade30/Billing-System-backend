-- Create bills table
CREATE TABLE IF NOT EXISTS bills (
    id BIGSERIAL PRIMARY KEY,
    bill_number VARCHAR(100) NOT NULL UNIQUE,
    customer_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    address TEXT,
    subtotal NUMERIC(10, 2) NOT NULL,
    tax_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    grand_total NUMERIC(10, 2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    bill_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Create items table
CREATE TABLE IF NOT EXISTS items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    item_name VARCHAR(255) NOT NULL,
    purchase_price NUMERIC(10, 2) NOT NULL,
    mrp NUMERIC(10, 2) NOT NULL,
    sell_price NUMERIC(10, 2) NOT NULL,
    min_sell_price NUMERIC(10, 2) NOT NULL,
    current_stock INTEGER NOT NULL,
    min_stock_level INTEGER NOT NULL,
    unit VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Create bill_items table
CREATE TABLE IF NOT EXISTS bill_items (
    id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL REFERENCES bills(id) ON DELETE CASCADE,
    item_id BIGINT NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    mrp NUMERIC(10, 2) NOT NULL,
    sell_price NUMERIC(10, 2) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    total NUMERIC(10, 2) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    CONSTRAINT fk_bill FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_bills_bill_number ON bills(bill_number);
CREATE INDEX IF NOT EXISTS idx_bills_customer_name ON bills(customer_name);
CREATE INDEX IF NOT EXISTS idx_bills_bill_date ON bills(bill_date);
CREATE INDEX IF NOT EXISTS idx_bill_items_bill_id ON bill_items(bill_id);
CREATE INDEX IF NOT EXISTS idx_bill_items_item_id ON bill_items(item_id);
