delete
from students;
TRUNCATE students RESTART IDENTITY CASCADE;
insert into students (pk, id, student_id, name, last_name)
values (default, '1', 'S001', 'jerry', 'seinfeld');
insert into students (pk, id, student_id, name, last_name)
values (default, '2', 'S002', 'george', 'costanza');
insert into students (pk, id, student_id, name, last_name)
values (default, '3', 'S003', 'elaine', 'benes');
insert into students (pk, id, student_id, name, last_name)
values (default, '4', 'S004', 'cosmo', 'kramer');
insert into students (pk, id, student_id, name, last_name)
values (default, '5', 'S005', 'hello', 'newman');