--liquibase formatted sql

--changeset andreevcode:003-create-device_channel-simple-table
create table if not exists public.device_channel(
    id                      bigserial PRIMARY KEY,
    device_id               bigint not null REFERENCES public.device (id) ON DELETE CASCADE,
    channel_index           int not null,
    type                    text not null,
    created_at              timestamptz default now(),
    value                   real,
    value_updated_at        timestamptz
);

CREATE UNIQUE INDEX IF NOT EXISTS device_channel_uniq_idx ON public.device_channel(device_id, channel_index);
