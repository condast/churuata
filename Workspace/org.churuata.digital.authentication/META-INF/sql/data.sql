/* Initialise the database with default entries. don't break up the line..it causes errors */
INSERT INTO LOGIN ( id, username, password, email, createdate, updatedate) values (0, 'Admin', 'Churu@t@Rul3s@2022', 'info@condast.com', curdate(), curdate());
INSERT INTO ADMIN ( id, loginid, role, createdate, updatedate) values (0, 0, 1, curdate(), curdate());
