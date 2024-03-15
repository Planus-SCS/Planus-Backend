drop table if exists group_todo_completion;
drop table if exists group_tag;
drop table if exists tag;
drop table if exists group_member;
drop table if exists group_join;
drop table if exists todo;
drop table if exists todo_category;
drop table if exists _group;
drop table if exists member;
drop table if exists exception_log;

create table member
(
    member_id         bigint auto_increment primary key,
    created_at        datetime(6)  null,
    updated_at        datetime(6)  null,
    birth             varchar(255) null,
    description       varchar(255) null,
    email             varchar(255) null,
    name              varchar(255) null,
    nickname          varchar(255) null,
    password          text         null,
    profile_image_url text         null,
    role              varchar(255) null,
    social_type       varchar(255) null,
    status            varchar(255) null
);

create table _group
(
    group_id        bigint auto_increment primary key,
    created_at      datetime(6)  null,
    updated_at      datetime(6)  null,
    group_image_url text         null,
    introduction    varchar(255) null,
    limit_count     int          not null,
    name            varchar(255) null,
    notice          text         null,
    scope           varchar(255) null,
    status          varchar(255) null
);

create table todo_category
(
    dtype            varchar(31)  not null,
    todo_category_id bigint auto_increment primary key,
    created_at       datetime(6)  null,
    updated_at       datetime(6)  null,
    color            varchar(255) null,
    name             varchar(255) null,
    status           varchar(255) null,
    group_id         bigint       null,
    member_id        bigint       null,
    foreign key (group_id) references _group (group_id),
    foreign key (member_id) references member (member_id)
);

create table todo
(
    dtype            varchar(31)  not null,
    todo_id          bigint auto_increment primary key,
    created_at       datetime(6)  null,
    updated_at       datetime(6)  null,
    description      varchar(255) null,
    end_date         date         null,
    is_group_todo    bit          not null,
    start_date       date         null,
    start_time       time         null,
    title            varchar(255) null,
    completion       bit          null,
    group_id         bigint       null,
    todo_category_id bigint       null,
    member_id        bigint       null,
    foreign key (group_id) references _group (group_id),
    foreign key (member_id) references member (member_id),
    foreign key (todo_category_id) references todo_category (todo_category_id)
);

create table group_join
(
    group_join_id bigint auto_increment primary key,
    created_at    datetime(6)  null,
    updated_at    datetime(6)  null,
    status        varchar(255) null,
    group_id      bigint       null,
    member_id     bigint       null,
    foreign key (group_id) references _group (group_id),
    foreign key (member_id) references member (member_id)
);

create table group_member
(
    group_member_id bigint auto_increment primary key,
    created_at      datetime(6)  null,
    updated_at      datetime(6)  null,
    leader          bit          not null,
    online_status   bit          not null,
    status          varchar(255) null,
    todo_authority  bit          not null,
    group_id        bigint       null,
    member_id       bigint       null,

    foreign key (group_id) references _group (group_id),
    foreign key (member_id) references member (member_id)
);


create table tag
(
    tag_id     bigint auto_increment primary key,
    created_at datetime(6)  null,
    updated_at datetime(6)  null,
    name       varchar(255) null
);

create table group_tag
(
    group_tag_id bigint auto_increment primary key,
    created_at   datetime(6) null,
    updated_at   datetime(6) null,
    group_id     bigint      null,
    tag_id       bigint      null,

    foreign key (tag_id) references tag (tag_id),
    foreign key (group_id) references _group (group_id)
);

create table group_todo_completion
(
    group_todo_completion_id bigint auto_increment primary key,
    completion               bit    not null,
    todo_id                  bigint null,
    member_id                bigint null,
    foreign key (member_id) references member (member_id),
    foreign key (todo_id) references todo (todo_id)
);

create table exception_log
(
    id             bigint auto_increment primary key,
    created_at     datetime(6)  null,
    updated_at     datetime(6)  null,
    exception_type varchar(255) null,
    message        varchar(255) null,
    class_name     varchar(255) null,
    method_name    varchar(255) null,
    line_number    int          null,
    requesturi     varchar(255) null,
    http_method    varchar(255) null,
    parameter      varchar(50000) null,
    email          varchar(255) null
);