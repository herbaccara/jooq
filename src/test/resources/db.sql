create table "board"
(
    id    int primary key auto_increment,
    title varchar(255) default null,
    `like` int default rand() * 100
);

insert into "board" (title)
values ('게시물1'),
       ('게시물2'),
       ('게시물3'),
       ('게시물4'),
       ('게시물5'),
       ('게시물6'),
       ('게시물7'),
       ('게시물8'),
       ('게시물9'),
       ('게시물10'),
       ('게시물11'),
       ('게시물12'),
       ('게시물13'),
       ('게시물14'),
       ('게시물15'),
       ('게시물16'),
       ('게시물17'),
       ('게시물18'),
       ('게시물19'),
       ('게시물20'),
       ('게시물21'),
       ('게시물22');

insert into "board" (title, `like`)
values ('게시물23', 32),
       ('게시물24', 89);