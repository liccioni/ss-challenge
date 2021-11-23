delete
from courses;
TRUNCATE courses RESTART IDENTITY CASCADE;
insert into courses (pk, id, name)
values (default, '1', 'math');
insert into courses (pk, id, name)
values (default, '2', 'history');
insert into courses (pk, id, name)
values (default, '3', 'science');
insert into courses (pk, id, name)
values (default, '4', 'english');