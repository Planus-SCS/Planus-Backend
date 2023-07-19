INSERT INTO member (description, email, nickname, profile_image_url, role, social_type, status)
VALUES ('hello planus!', 'planus@planus','groupLeader', 'image_url1', 'USER', 'KAKAO', 'ACTIVE');

INSERT INTO member (description, email, nickname, profile_image_url, role, social_type, status)
VALUES ('hello testMember!', 'tester@planus','groupMember', 'image_url2', 'USER', 'KAKAO', 'ACTIVE');

INSERT INTO _group (group_image_url, introduction, limit_count, name, notice, scope, status)
VALUES ('group_image_url', 'planus test group', 5, 'planus group', 'notice', 'PUBLIC', 'ACTIVE');

INSERT INTO todo_category (dtype, color, name, status, member_id)
VALUES ('MC', 'RED', 'memberTodoCategory', 'ACTIVE', 1);

INSERT INTO todo_category (dtype, color, name, status, member_id, group_id)
VALUES ('GC', 'BLUE', 'groupTodoCategory', 'ACTIVE', 1, 1);

INSERT INTO group_member (leader, todo_authority, online_status, status, member_id, group_id)
VALUES (true, true, true, 'ACTIVE', 1, 1);

INSERT INTO group_member (leader, todo_authority, online_status, status, member_id, group_id)
VALUES (false, false, true, 'ACTIVE', 2, 1);