-- ============================================================================
-- DebateHub - PostgreSQL schema (MVP)
-- Target DB: debatehub (owner: debate_user)
-- Safe to run on a fresh database. If re-running, drop/recreate the DB first.
-- ============================================================================

BEGIN;

-- UUID generation (PostgreSQL 10+): gen_random_uuid() from pgcrypto
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ----------------------------------------------------------------------------
-- Helpers: keep updated_at fresh
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION trg_set_updated_at()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at := NOW();
  RETURN NEW;
END $$;

-- ----------------------------------------------------------------------------
-- USERS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
  id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  email          text        NOT NULL,
  password_hash  text        NOT NULL,
  display_name   text        NOT NULL,
  avatar_url     text,
  is_active      boolean     NOT NULL DEFAULT true,
  created_at     timestamptz NOT NULL DEFAULT NOW(),
  updated_at     timestamptz NOT NULL DEFAULT NOW(),
  CONSTRAINT users_email_not_blank CHECK (length(trim(email)) > 0),
  CONSTRAINT users_pwd_not_blank   CHECK (length(password_hash) > 0)
);

-- Case-insensitive unique email (works with any collation)
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email_ci ON users (LOWER(email));

CREATE TRIGGER users_set_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();

-- ----------------------------------------------------------------------------
-- DEBATES
-- ----------------------------------------------------------------------------
-- Note: host_user_id is nullable so user deletion doesn’t block account removal.
-- If a host account is deleted, the debate remains and can be re-assigned.
CREATE TABLE IF NOT EXISTS debates (
  id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  title          text        NOT NULL,
  slug           text        NOT NULL,                      -- public URL segment (/d/:slug)
  host_user_id   uuid        NULL REFERENCES users(id) ON DELETE SET NULL,
  description    text,
  is_invite_only boolean     NOT NULL DEFAULT true,
  status         text        NOT NULL DEFAULT 'scheduled',
  created_at     timestamptz NOT NULL DEFAULT NOW(),
  updated_at     timestamptz NOT NULL DEFAULT NOW(),
  started_at     timestamptz,
  ended_at       timestamptz,
  CONSTRAINT debates_slug_not_blank CHECK (length(trim(slug)) > 0),
  CONSTRAINT debates_status_valid   CHECK (status IN ('scheduled','live','ended')),
  CONSTRAINT debates_time_order     CHECK (ended_at IS NULL OR started_at IS NULL OR ended_at >= started_at)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_debates_slug ON debates (slug);
CREATE INDEX IF NOT EXISTS ix_debates_host ON debates (host_user_id);
CREATE INDEX IF NOT EXISTS ix_debates_status ON debates (status);

CREATE TRIGGER debates_set_updated_at
BEFORE UPDATE ON debates
FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();

-- ----------------------------------------------------------------------------
-- PARTICIPANTS (who is part of a debate)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS debate_participants (
  debate_id  uuid        NOT NULL REFERENCES debates(id) ON DELETE CASCADE,
  user_id    uuid        NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
  role       text        NOT NULL,   -- 'host' | 'debater' | 'moderator'
  joined_at  timestamptz NOT NULL DEFAULT NOW(),
  PRIMARY KEY (debate_id, user_id),
  CONSTRAINT participants_role_valid CHECK (role IN ('host','debater','moderator'))
);

CREATE INDEX IF NOT EXISTS ix_participants_user ON debate_participants (user_id);
CREATE INDEX IF NOT EXISTS ix_participants_debate ON debate_participants (debate_id);

-- ----------------------------------------------------------------------------
-- INVITATIONS (codes / links to join debates)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS invitations (
  id                   uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
  debate_id            uuid        NOT NULL REFERENCES debates(id) ON DELETE CASCADE,
  role_granted         text        NOT NULL,   -- 'debater' | 'moderator'
  code                 text        NOT NULL,   -- human/shareable code or slug
  expires_at           timestamptz,
  max_uses             integer     NOT NULL DEFAULT 1,
  uses                 integer     NOT NULL DEFAULT 0,
  created_by           uuid        NOT NULL REFERENCES users(id) ON DELETE SET NULL,
  created_at           timestamptz NOT NULL DEFAULT NOW(),
  accepted_by_user_id  uuid        REFERENCES users(id) ON DELETE SET NULL,
  accepted_at          timestamptz,
  CONSTRAINT invitations_role_valid CHECK (role_granted IN ('debater','moderator')),
  CONSTRAINT invitations_uses_bounds CHECK (max_uses >= 1 AND uses >= 0 AND uses <= max_uses),
  CONSTRAINT invitations_accept_pair CHECK (
    (accepted_by_user_id IS NULL AND accepted_at IS NULL) OR
    (accepted_by_user_id IS NOT NULL AND accepted_at IS NOT NULL)
  )
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_invitations_code ON invitations (code);
CREATE INDEX IF NOT EXISTS ix_invitations_debate ON invitations (debate_id);

-- ----------------------------------------------------------------------------
-- USER PINS (profile page pinned debates)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_pins (
  user_id    uuid        NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
  debate_id  uuid        NOT NULL REFERENCES debates(id) ON DELETE CASCADE,
  pinned_at  timestamptz NOT NULL DEFAULT NOW(),
  PRIMARY KEY (user_id, debate_id)
);

CREATE INDEX IF NOT EXISTS ix_user_pins_user   ON user_pins (user_id);
CREATE INDEX IF NOT EXISTS ix_user_pins_debate ON user_pins (debate_id);

-- ----------------------------------------------------------------------------
-- PASSWORD RESET TOKENS (account recovery)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS auth_password_reset_tokens (
  id         uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    uuid        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token      text        NOT NULL,
  expires_at timestamptz NOT NULL,
  used_at    timestamptz,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  CONSTRAINT reset_token_future CHECK (expires_at > NOW())
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_reset_tokens_token ON auth_password_reset_tokens (token);
CREATE INDEX IF NOT EXISTS ix_reset_tokens_user ON auth_password_reset_tokens (user_id);

COMMIT;

-- ============================================================================
-- Verification snippets (run manually in psql if you want)
-- \dt
-- \d+ users
-- \d+ debates
-- ============================================================================
