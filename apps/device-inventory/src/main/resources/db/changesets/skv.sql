--liquibase formatted sql

--changeset andreevcode:005-create-skv-table
create table if not exists public.skv(
    storage_key   text,
    storage_value text
);
