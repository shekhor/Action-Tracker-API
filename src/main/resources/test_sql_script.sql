--test privilege groups

--Test Privileges
INSERT INTO PRIVILEGE_ACTR(privilege_name, description, status, PRIVILEGE_GROUP_ID, API_END_POINT, CREATED_BY) VALUES('CREATE_DEMO','CREATE DEMO TEST','ACTIVE', 1, '/api/test/demo/create', 'SYSTEM');
INSERT INTO PRIVILEGE_ACTR(privilege_name, description, status, PRIVILEGE_GROUP_ID, API_END_POINT, CREATED_BY) VALUES('UPDATE_DEMO','UPDATE DEMO TEST','ACTIVE', 1, '/api/test/demo/update', 'SYSTEM');
INSERT INTO PRIVILEGE_ACTR(privilege_name, description, status, PRIVILEGE_GROUP_ID, API_END_POINT, CREATED_BY) VALUES('DELETE_DEMO','DELETE DEMO TEST','ACTIVE', 1, '/api/test/demo/delete', 'SYSTEM');
INSERT INTO PRIVILEGE_ACTR(privilege_name, description, status, PRIVILEGE_GROUP_ID, API_END_POINT, CREATED_BY) VALUES('READ_DEMO','READ DEMO TEST','ACTIVE', 1, '/api/test/demo/read', 'SYSTEM');

INSERT INTO PRIVILEGE_ACTR(privilege_name, description, status, PRIVILEGE_GROUP_ID, API_END_POINT, CREATED_BY) VALUES('USER_DETAILS_BY_ID','USER DETAILS BY ID','ACTIVE', 1, '/rest/user/details/{id}', 'SYSTEM');
INSERT INTO PRIVILEGE_ACTR(privilege_name, description, status, PRIVILEGE_GROUP_ID, API_END_POINT, CREATED_BY) VALUES('USER_LIST','USER LIST','ACTIVE', 1, '/rest/user/list', 'SYSTEM');
INSERT INTO PRIVILEGE_ACTR(privilege_name, description, status, PRIVILEGE_GROUP_ID, API_END_POINT, CREATED_BY) VALUES('USER_CREATE','USER CREATE','ACTIVE', 1, '/rest/user/create', 'SYSTEM');
INSERT INTO PRIVILEGE_ACTR(privilege_name, description, status, PRIVILEGE_GROUP_ID, API_END_POINT, CREATED_BY) VALUES('USER_UPDATE','USER UPDATE','ACTIVE', 1, '/rest/user/update/{id}', 'SYSTEM');

--Test roles
--//has create update delete and read roles
INSERT INTO ROLES_ACTR(ROLE_NAME, STATUS, DESCRIPTION, CREATED_BY) VALUES('SUPER_ADMIN', 'ACTIVE', 'SUPER ADMIN ROLE', 'SYSTEM');
--//has create update and read roles
INSERT INTO ROLES_ACTR(ROLE_NAME, STATUS, DESCRIPTION, CREATED_BY) VALUES('PROJECT_OWNER', 'ACTIVE', 'PROJECT OWNER ROLE', 'SYSTEM');
--//has create and read roles
INSERT INTO ROLES_ACTR(ROLE_NAME, STATUS, DESCRIPTION, CREATED_BY) VALUES('PROJECT_MANAGER', 'ACTIVE', 'PROJECT MANAGER ROLE', 'SYSTEM');
--//has only read roles
INSERT INTO ROLES_ACTR(ROLE_NAME, STATUS, DESCRIPTION, CREATED_BY) VALUES('COMMON_USER', 'ACTIVE', 'COMMON USER  ROLE', 'SYSTEM');


--TEST PERMISSION
--SUPER_ADMIN HAS CREATE, UPDATE, DELETE AND READ PERMISSION ROLES
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (1,1);
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (1,2);
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (1,3);
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (1,4);

INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (1,5);
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (1,6);
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (1,7);
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (1,8);

--PROJECT_OWNER HAS CREATE, UPDATE AND READ PERMISSION ROLES
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (2,1);
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (2,2);
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (2,4);
--PROJECT_MANAGER  HAS CREATE AND READ PERMISSION
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (3,1);
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (3,4);
--COMMON_USER HAS ONLY READ PERMISSION
INSERT INTO  role_privilege_mapping_actr(ROLE_ID, privilege_id) VALUES (4,4);

--select * from PRIVILEGE;
--select * from ROLES_ACTR;
--SELECT * from role_privilege_mapping_actr;
--delete from role_privilege_mapping_actr;

INSERT INTO users_actr(
	id, first_name, last_name, username, email, encrypted_password, user_id, user_role_id, organization_id, domain_name, logged_out_at, status, user_type, user_category, created_by, create_time, edited_by, edit_time, internal_version)
	VALUES (1, 'Admin', 'Admin', 'admin@gmail.com', 'admin@gmail.com', '$2a$10$PjYTdj.e0eZu0hhSbVqxZeJM9o.d6NO0TxMAATFZggWVokJLuiMLm', 'admin', 1, 1, 'tigerit.com', null, 'ACTIVE','COMMON','SUPER_ADMIN', 'SYSTEM', null, null, null, 0);

--if needed to reset bigserial field
--SELECT SETVAL((SELECT pg_get_serial_sequence('table_name', 'field_name')), 1, false);
SELECT SETVAL((SELECT pg_get_serial_sequence('roles_actr', 'id')), 1, false);

