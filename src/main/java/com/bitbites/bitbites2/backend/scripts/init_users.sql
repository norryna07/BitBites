create type role_type as enum (
    'cooker',
    'admin',
    'writer'
    );

create table users (
    id SERIAL primary key,
    username varchar(50) unique not null,
    hashed_password varchar(255) not null,
    role role_type not null
);