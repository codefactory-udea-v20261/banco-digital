-- V11__migrate_plaintext_to_encrypted.sql
-- NOTE: This must be run after the application has been updated to write encrypted_payload.
-- Rows written after V10 will already have encrypted_payload populated.
-- This migration marks old plaintext-only rows for re-processing:
UPDATE outbox_events 
SET status = 'PENDING', retry_count = 0 
WHERE encrypted_payload IS NULL AND status = 'SENT';
