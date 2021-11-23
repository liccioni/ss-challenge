TRUNCATE registrations RESTART IDENTITY CASCADE;
insert into registrations (pk, id, course_pk, student_pk)
values (default, '1', 3, 1);
insert into registrations (pk, id, course_pk, student_pk)
values (default, '2', 3, 3);
insert into registrations (pk, id, course_pk, student_pk)
values (default, '3', 3, 5);