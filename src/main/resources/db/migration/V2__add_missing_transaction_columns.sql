-- V2: Add missing columns to transactions table
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS description  VARCHAR(500);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS category     VARCHAR(100);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS merchant_name VARCHAR(255);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS is_recurring  BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS plaid_transaction_id VARCHAR(255);
