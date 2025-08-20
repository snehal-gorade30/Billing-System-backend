-- Add missing columns to bills table if they don't exist
ALTER TABLE bills ADD COLUMN IF NOT EXISTS tax_amount NUMERIC(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE bills ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(10, 2) NOT NULL DEFAULT 0;