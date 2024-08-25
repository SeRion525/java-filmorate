INSERT INTO users(login, email, name, birthday)
VALUES ('user1', 'email1@email.com', 'user1', '2000-09-20'),
       ('user2', 'email2@email.com', 'user2', '1999-09-09'),
       ('user3', 'email3@email.com', 'user3', '1988-08-08');

INSERT INTO friends(user_id, friend_id)
VALUES (1, 2), (2, 1);

INSERT INTO mpa(name)
VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO genres(name)
VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

INSERT INTO films(name, description, release_date, duration, mpa_id)
VALUES ('Test1', 'Test1 desc', '2001-01-01', 111, 3),
       ('Test2', 'Test2 desc', '2002-02-02', 222, 4);

INSERT INTO films_genres(film_id, genre_id)
VALUES (1, 1), (1, 3),
       (2, 2), (2, 6);

INSERT INTO likes(film_id, user_id)
VALUES (1, 1), (2, 1);
