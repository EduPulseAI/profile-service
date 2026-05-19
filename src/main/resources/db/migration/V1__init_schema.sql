-- Full baseline schema for profile-service.
-- Flyway is disabled in dev (ddl-auto=create-drop handles schema there).
-- This migration runs in docker/prod where ddl-auto=none.

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- -------------------------
-- Core profile table
-- -------------------------
CREATE TABLE IF NOT EXISTS user_profiles (
    id           BIGSERIAL    PRIMARY KEY,
    username     VARCHAR(255) NOT NULL UNIQUE,
    is_primary   BOOLEAN,
    -- Personal
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    title        VARCHAR(255),
    location     VARCHAR(255),
    email        VARCHAR(255),
    phone        VARCHAR(255),
    -- About
    summary      TEXT,
    -- Social links
    social_github    VARCHAR(255),
    social_linkedin  VARCHAR(255),
    social_discord   VARCHAR(255),
    social_twitter   VARCHAR(255),
    social_instagram VARCHAR(255)
);

-- -------------------------
-- Resumes
-- -------------------------
CREATE TABLE IF NOT EXISTS resumes (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id         BIGINT       REFERENCES user_profiles(id),
    original_file_name VARCHAR(255),
    raw_text           TEXT,
    CONSTRAINT uc_profile_original_file_name UNIQUE (profile_id, original_file_name)
);

-- -------------------------
-- Experience collection
-- -------------------------
CREATE TABLE IF NOT EXISTS profile_experiences (
    profile_id           BIGINT  NOT NULL REFERENCES user_profiles(id),
    experience_order     INTEGER NOT NULL,
    exp_title            VARCHAR(255),
    exp_company          VARCHAR(255),
    exp_period           VARCHAR(255),
    exp_location         VARCHAR(255),
    exp_description      VARCHAR(2000),
    exp_responsibilities JSONB,
    exp_technologies     JSONB,
    PRIMARY KEY (profile_id, experience_order)
);

-- -------------------------
-- Credentials: certifications
-- -------------------------
CREATE TABLE IF NOT EXISTS profile_certifications (
    profile_id  BIGINT REFERENCES user_profiles(id),
    cert_name   VARCHAR(255),
    cert_issuer VARCHAR(255),
    cert_date   VARCHAR(255),
    cert_logo   VARCHAR(255)
);

-- -------------------------
-- Credentials: education
-- -------------------------
CREATE TABLE IF NOT EXISTS profile_education (
    profile_id      BIGINT REFERENCES user_profiles(id),
    edu_degree      VARCHAR(255),
    edu_institution VARCHAR(255),
    edu_year        VARCHAR(255)
);

-- -------------------------
-- Technical skills collections
-- -------------------------
CREATE TABLE IF NOT EXISTS profile_languages (
    profile_id BIGINT REFERENCES user_profiles(id),
    skill      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS profile_backend_skills (
    profile_id BIGINT REFERENCES user_profiles(id),
    skill      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS profile_frontend_skills (
    profile_id BIGINT REFERENCES user_profiles(id),
    skill      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS profile_database_skills (
    profile_id BIGINT REFERENCES user_profiles(id),
    skill      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS profile_cloud_skills (
    profile_id BIGINT REFERENCES user_profiles(id),
    skill      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS profile_tools_skills (
    profile_id BIGINT REFERENCES user_profiles(id),
    skill      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS profile_methodologies_skills (
    profile_id BIGINT REFERENCES user_profiles(id),
    skill      VARCHAR(255)
);

-- -------------------------
-- Spoken languages (new)
-- -------------------------
CREATE TABLE IF NOT EXISTS profile_spoken_languages (
    profile_id           BIGINT NOT NULL REFERENCES user_profiles(id),
    language_order       INTEGER NOT NULL,
    language_name        VARCHAR(255),
    language_proficiency VARCHAR(255),
    language_level       INTEGER,
    language_flag        VARCHAR(255),
    PRIMARY KEY (profile_id, language_order)
);
