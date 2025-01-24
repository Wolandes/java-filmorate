insert into public.users (email, login, name, birthday)
values ('test@yandex.ru', 'test', 'test name', '2000-01-01'),
('test2@yandex.ru', 'test2', 'test name2', '2001-01-01'),
('test3@yandex.ru', 'test3', 'test name3', '2002-01-01');

insert into public.friends (user_id, friend_id)
values (2, 3);

insert into public.films (name, description, release_date, duration, mpaa_id)
values ('test', 'test description', '2000-01-01', 100, 1),
('test2', 'test description2', '2001-01-01', 110, 2),
('test3', 'test description3', '2002-01-01', 120, 3);

insert into public.film_genre (film_id, genre_id)
values (1, 2),
(1, 3),
(2, 2),
(3, 1),
(3, 2),
(3, 3);

insert into public.likes (film_id, user_id)
values(1, 3);