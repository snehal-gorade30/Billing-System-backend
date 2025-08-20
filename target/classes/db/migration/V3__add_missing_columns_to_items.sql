-- Add missing columns to items table if they don't exist
ALTER TABLE items ADD COLUMN IF NOT EXISTS name VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE items ADD COLUMN IF NOT EXISTS category VARCHAR(100);

-- Update the name column to be unique after adding it
-- First, update any empty names with a default value based on item_name
UPDATE items SET name = item_name WHERE name = '' OR name IS NULL;

-- Then add the unique constraint
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'items_name_key' 
        AND table_name = 'items'
    ) THEN
        ALTER TABLE items ADD CONSTRAINT items_name_key UNIQUE (name);
    END IF;
END $$;
