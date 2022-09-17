create database if not exists detection2;
use detection2;
create table if not exists bridge_tb
(
    id          bigint auto_increment
        primary key,
    name        varchar(64)   not null,
    span        varchar(64)   not null,
    structure   varchar(64)   not null,
    user_id     bigint        not null,
    gmt_create  timestamp     not null,
    report_path mediumtext    null,
    process     varchar(16)   not null,
    exp         int default 0 null
);

create table if not exists damage_tb
(
    id        bigint auto_increment
        primary key,
    type      varchar(16)      not null,
    image_id  bigint           not null,
    width     double default 0 not null,
    length    double default 0 not null,
    area      double default 0 not null,
    bridge_id bigint           null,
    struct_id bigint           null
);

create table if not exists image_tb
(
    id            bigint auto_increment
        primary key,
    name          varchar(64)      not null,
    src_path      mediumtext       not null,
    crack_json    text             null,
    area_json     text             null,
    status        int              not null,
    struct_id     bigint default 0 null,
    bridge_id     bigint default 0 null,
    user_id       bigint           not null,
    process       varchar(16)      null,
    shot_distance int              not null,
    focal_length  int              not null,
    gmt_create    timestamp        not null,
    res_path      mediumtext       null,
    exception     varchar(64)      null
);

create table if not exists struct_tb
(
    id            bigint auto_increment
        primary key,
    serial_number varchar(64) not null,
    bridge_id     bigint      not null,
    part          varchar(8)  not null,
    shot_distance int         not null,
    focal_length  int         not null
);

create table if not exists user_tb
(
    id       bigint auto_increment
        primary key,
    nick     varchar(64)                   not null,
    email    varchar(64)                   not null,
    salt     varchar(8)                    not null,
    password varchar(32)                   not null,
    header   varchar(32) default 'default' null,
    constraint user_tb_email_uindex
        unique (email)
);

