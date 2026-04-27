-- Zeno App - Initial Schema
-- V1: Create all core tables

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ── Users ─────────────────────────────────────────────────────────────────────
CREATE TABLE users (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email        VARCHAR(255) UNIQUE NOT NULL,
    name         VARCHAR(255)        NOT NULL,
    password     VARCHAR(255)        NOT NULL,
    plan         VARCHAR(20)         NOT NULL DEFAULT 'FREE',
    created_at   TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP           NOT NULL DEFAULT NOW()
);

-- ── Bank Accounts (linked via Plaid) ─────────────────────────────────────────
CREATE TABLE bank_accounts (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plaid_item_id       VARCHAR(255),
    plaid_access_token  VARCHAR(500),
    institution_name    VARCHAR(255),
    last_synced         TIMESTAMP,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Subscriptions ─────────────────────────────────────────────────────────────
CREATE TABLE subscriptions (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID           NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name             VARCHAR(255)   NOT NULL,
    amount           DECIMAL(10,2)  NOT NULL,
    currency         VARCHAR(3)     NOT NULL DEFAULT 'USD',
    billing_cycle    VARCHAR(20)    NOT NULL DEFAULT 'MONTHLY',
    next_charge_date DATE,
    category         VARCHAR(100),
    status           VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    waste_score      INT            NOT NULL DEFAULT 0,
    detected_via     VARCHAR(20)    NOT NULL DEFAULT 'MANUAL',
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- ── Transactions ──────────────────────────────────────────────────────────────
CREATE TABLE transactions (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID           NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    subscription_id       UUID           REFERENCES subscriptions(id) ON DELETE SET NULL,
    bank_account_id       UUID           REFERENCES bank_accounts(id) ON DELETE SET NULL,
    plaid_transaction_id  VARCHAR(255)   UNIQUE,
    amount                DECIMAL(10,2)  NOT NULL,
    merchant_name         VARCHAR(255),
    description           VARCHAR(500),
    category              VARCHAR(100),
    date                  DATE           NOT NULL,
    is_recurring          BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- ── Trial Watches ─────────────────────────────────────────────────────────────
CREATE TABLE trial_watches (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    subscription_id  UUID      NOT NULL REFERENCES subscriptions(id) ON DELETE CASCADE,
    trial_end_date   DATE      NOT NULL,
    alert_sent       BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ── Indexes ───────────────────────────────────────────────────────────────────
CREATE INDEX idx_subs_user_id    ON subscriptions(user_id);
CREATE INDEX idx_subs_status     ON subscriptions(status);
CREATE INDEX idx_txns_user_id    ON transactions(user_id);
CREATE INDEX idx_txns_date       ON transactions(date DESC);
CREATE INDEX idx_txns_recurring  ON transactions(is_recurring) WHERE is_recurring = TRUE;
CREATE INDEX idx_bank_user_id    ON bank_accounts(user_id);
CREATE INDEX idx_trial_user_id   ON trial_watches(user_id);
