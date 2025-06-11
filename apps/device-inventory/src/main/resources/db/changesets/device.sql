--liquibase formatted sql

--changeset andreevcode:002-create-device-simple-table
create table if not exists public.device(
    id          bigserial PRIMARY KEY,
    name        text             NOT NULL,
    type        text             NOT NULL,
    location    text             NOT NULL,
    ip          inet,
    created_at  timestamptz  DEFAULT NOW()
);
