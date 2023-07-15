insert into member (description, email, nickname, profile_image_url, role, social_type, status)
values('hello planus!', 'planus@planus','planus', 'image_url', 'USER', 'KAKAO', 'ACTIVE');

insert into _group (group_image_url, introduction, limit_count, name, notice, scope, status)
values('group_image_url', 'planus test group', 5, 'planus group', 'notice', 'PUBLIC', 'ACTIVE');

insert into todo_category (dtype, color, name, status, member_id)
values ('MC', 'RED', 'memberTodoCategory', 'ACTIVE', 1);

insert into todo_category (dtype, color, name, status, member_id, group_id)
values ('GC', 'BLUE', 'groupTodoCategory', 'ACTIVE', 1, 1);