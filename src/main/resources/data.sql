DELETE FROM LIKES;
DELETE FROM FILM_GENRE_LINE;
DELETE FROM FRIENDSHIP;
DELETE FROM USERS;
DELETE FROM FILMS;
ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1;
ALTER TABLE FILMS ALTER COLUMN fILM_ID RESTART WITH 1;

MERGE INTO GENRE KEY(genre_id)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

MERGE INTO RATING_MPA KEY(MPA_id)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');