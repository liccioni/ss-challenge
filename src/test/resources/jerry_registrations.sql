TRUNCATE registrations RESTART IDENTITY CASCADE;
insert into registrations (pk, id, course_pk, student_pk)
values (default, '1', 1, 1);
insert into registrations (pk, id, course_pk, student_pk)
values (default, '2', 2, 1);