-- Add barcode column to items table
ALTER TABLE items ADD COLUMN barcode VARCHAR(100);

-- Create an index on the barcode column for faster lookups
CREATE UNIQUE INDEX idx_items_barcode ON items(barcode) WHERE barcode IS NOT NULL;
