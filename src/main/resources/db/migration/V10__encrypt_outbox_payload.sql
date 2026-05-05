-- Migration: Encrypt sensitive data in outbox_events table
-- This migration adds encryption support for the payload column which may contain PII/financial data

-- Step 1: Add encrypted_payload column (will replace TEXT payload)
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS encrypted_payload BYTEA;

-- Step 2: Add encryption_algorithm column to track encryption method
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS encryption_algorithm VARCHAR(50) DEFAULT 'AES-256-GCM';

-- Step 3: Add encryption_key_version for key rotation support
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS encryption_key_version INT DEFAULT 1;

-- Step 4: Create index on status for efficient polling
CREATE INDEX IF NOT EXISTS idx_outbox_status ON outbox_events(status) WHERE status IN ('PENDING', 'FAILED');

-- Step 5: Create index on created_at for time-based cleanup
CREATE INDEX IF NOT EXISTS idx_outbox_created_at ON outbox_events(created_at DESC);

-- Step 6: Add comment explaining encryption
COMMENT ON COLUMN outbox_events.encrypted_payload IS 'Encrypted event payload using AES-256-GCM. Key managed by Google Cloud KMS or AWS KMS.';
COMMENT ON COLUMN outbox_events.encryption_algorithm IS 'Encryption algorithm used. Currently AES-256-GCM. Used for rotation support.';

-- ⚠️ IMPORTANT: Data Migration Required
-- This migration only adds the columns. The application must:
-- 1. Load existing plaintext payloads from 'payload' column
-- 2. Encrypt using KMS (Google Cloud KMS recommended)
-- 3. Store encrypted data in 'encrypted_payload' column
-- 4. Update application to write to 'encrypted_payload' only
-- 5. In a future migration, drop the 'payload' column after verifying all data is encrypted

-- For now, PAYLOAD COLUMN REMAINS NOT NULL and is the source of truth
-- Application must handle both encrypted_payload and payload columns during transition

