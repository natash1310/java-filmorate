package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.interfaces.dal.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Интеграционные тесты приложения Filmorate")
class FilmorateApplicationTests {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;
    private final FilmGenresStorage filmGenreLineStorage;
    private final FilmDbStorage filmStorage;
    private final FriendshipStorage friendShipStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likesStorage;
    private final MpaRatingStorage mpaStorage;

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM LIKES");
        jdbcTemplate.update("DELETE FROM FILM_GENRE_LINE");
        jdbcTemplate.update("DELETE FROM FRIENDSHIP");
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1");
    }

    @Test
    @DisplayName("Добавление пользователя")
    void addUserTest() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User newUser = userStorage.addUser(user);
        user.setId(1);
        assertThat(user, equalTo(newUser));
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateUserTest() {
        User user1 = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User oldUser = userStorage.addUser(user1);
        User user2 = User.builder()
                .id(oldUser.getId())
                .email("newUser2@yandex.ru")
                .login("newLoginUser2022")
                .name("NewUser")
                .birthday(LocalDate.of(1990, 1, 2))
                .friends(new HashSet<>())
                .build();
        User updateUser = userStorage.updateUser(user2);
        assertThat("Пользователь не обновлен", user2, equalTo(updateUser));
    }

    @Test
    @DisplayName("Ошибка при обновлении несуществующего пользователя")
    void updateUserFailTest() {
        User user = User.builder()
                .id(999)
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class, () -> userStorage.updateUser(user));
        assertThat("Пользователь с id 999 не найден", equalTo(e.getMessage()));
    }

    @Test
    @DisplayName("Получение пустого списка пользователей")
    void getUsersByEmptyTest() {
        Collection<User> users = userStorage.getAllUsers();
        assertThat("Список пользователей не пуст", users, empty());
    }

    @Test
    @DisplayName("Получение списка пользователей")
    void getUsersTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        assertThat("Список пользователей пуст", userStorage.getAllUsers(), hasSize(2));
        assertThat("User1 не найден", userStorage.getAllUsers(), hasItem(addUser1));
        assertThat("User2 не найден", userStorage.getAllUsers(), hasItem(addUser2));
    }

    @Test
    @DisplayName("Ошибка при запросе пользователя по несуществующему id")
    void getUserInvalidIdTest() {
        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class, () -> userStorage.getUser(1));
        assertThat("Пользователь с id 1 не найден", equalTo(e.getMessage()));
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void getUserById() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User addUser = userStorage.addUser(user1);
        assertThat(addUser, equalTo(userStorage.getUser(addUser.getId())));
    }

    @Test
    @DisplayName("Получение пустого списка друзей пользователя")
    void getFriendsByEmptyTest() {
        Collection<Integer> friends = friendShipStorage.getListOfFriends(1);
        assertThat("Список друзей не пуст", friends, hasSize(0));
    }

    @Test
    @DisplayName("Добавление пользователя в друзья другого пользователя")
    void addAsFriendTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        friendShipStorage.addAsFriend(addUser1.getId(), addUser2.getId());
        assertThat("User2 не добавлен в друзья User1",
                userStorage.getUser(addUser1.getId()).getFriends(), hasItem(addUser2.getId()));
        assertThat("Список друзей User2 не пуст",
                userStorage.getUser(addUser2.getId()).getFriends(), empty());
    }

    @Test
    @DisplayName("Удаление пользователя из друзей")
    void removeFromFriendsAsFriendTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        friendShipStorage.addAsFriend(addUser1.getId(), addUser2.getId());
        assertThat("Список друзей User1 пуст",
                userStorage.getUser(addUser1.getId()).getFriends(), hasItem(addUser2.getId()));
        friendShipStorage.removeFromFriends(addUser1.getId(), addUser2.getId());
        assertThat("Список друзей User1 не пуст",
                userStorage.getUser(addUser1.getId()).getFriends(), empty());
    }

    @Test
    @DisplayName("Получение списка друзей пользователя")
    void getListOfFriendsTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1992, 1, 1))
                .build();
        User user3 = User.builder()
                .email("user3@yandex.ru")
                .login("user3")
                .name("User3")
                .birthday(LocalDate.of(1993, 1, 1))
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        User addUser3 = userStorage.addUser(user3);
        friendShipStorage.addAsFriend(addUser1.getId(), addUser2.getId());
        friendShipStorage.addAsFriend(addUser1.getId(), addUser3.getId());
        assertThat("Список друзей User1 не содержит id User2 и User3",
                friendShipStorage.getListOfFriends(addUser1.getId()), contains(addUser2.getId(), addUser3.getId()));
    }

    @Test
    @DisplayName("Получение списка общих друзей двух пользователей")
    void getAListOfMutualFriendsTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1992, 1, 1))
                .build();
        User user3 = User.builder()
                .email("user3@yandex.ru")
                .login("user3")
                .name("User3")
                .birthday(LocalDate.of(1993, 1, 1))
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        User addUser3 = userStorage.addUser(user3);
        friendShipStorage.addAsFriend(addUser1.getId(), addUser3.getId());
        friendShipStorage.addAsFriend(addUser2.getId(), addUser3.getId());
        assertThat("Список друзей User1 не содержит id User2 и User3",
                friendShipStorage.getAListOfMutualFriends(addUser1.getId(), addUser2.getId()),
                contains(addUser3.getId()));
    }

    @Test
    @DisplayName("Добавление фильма")
    void addFilmTest() {
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();
        Film addFilm = filmStorage.addFilm(film);
        film.setId(1);
        assertThat(film, equalTo(addFilm));
    }

    @Test
    @DisplayName("Обновление фильма")
    void updateFilmTest() {
        Film film1 = Film.builder()
                .name("Тестовый фильм 1")
                .description("Описание тестового фильма 1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(100)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film oldFilm = filmStorage.addFilm((film1));
        Film film2 = Film.builder()
                .id(oldFilm.getId())
                .name("Тестовый фильм 2")
                .description("Описание тестового фильма 2")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(100)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film updateFilm = filmStorage.updateFilm(film2);
        assertThat("Фильм не обновлен", film2, equalTo(updateFilm));
    }

    @Test
    @DisplayName("Ошибка при обновлении несуществующего фильма")
    void updateFilmFailTest() {
        Film film = Film.builder()
                .id(999)
                .name("Тестовый фильм 1")
                .description("Описание тестового фильма 1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(100)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class, () -> filmStorage.updateFilm(film));
        assertThat("Фильм с id 999 не найден", equalTo(e.getMessage()));
    }

    @Test
    @DisplayName("Получение пустого списка фильмов")
    void getFilmsByEmptyTest() {
        Collection<Film> films = filmStorage.getAllFilms();
        assertThat("Список фильмов не пуст", films, empty());
    }

    @Test
    @DisplayName("Получение списка фильмов")
    void getFilmsTest() {
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film film2 = Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(1961, 1, 1))
                .duration(109)
                .rate(5)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();
        Film addFilm1 = filmStorage.addFilm(film1);
        Film addFilm2 = filmStorage.addFilm(film2);
        assertThat("Список пользователей пуст", filmStorage.getAllFilms(), hasSize(2));
        assertThat("Film1 не найден", filmStorage.getAllFilms(), hasItem(addFilm1));
        assertThat("Film2 не найден", filmStorage.getAllFilms(), hasItem(addFilm2));
    }

    @Test
    @DisplayName("Ошибка при запросе фильма по несуществующему id")
    void getFilmInvalidIdTest() {
        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class, () -> filmStorage.getFilm(1));
        assertThat("Фильм с id 1 не найден", equalTo(e.getMessage()));
    }

    @Test
    @DisplayName("Получение фильма по id")
    void getFilmById() {
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film addFilm = filmStorage.addFilm(film1);
        assertThat(addFilm, equalTo(filmStorage.getFilm(addFilm.getId())));
    }

    @Test
    @DisplayName("Получение пустого списка лайков фильма")
    void getLikesByEmptyTest() {
        Collection<Integer> likes = likesStorage.getListOfLikes(1);
        assertThat("Список лайков не пуст", likes, hasSize(0));
    }

    @Test
    @DisplayName("Добавление лайка фильму")
    void addLikeTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        User addUser1 = userStorage.addUser(user1);
        Film addFilm1 = filmStorage.addFilm(film1);
        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("%s не поставил лайк %s", addUser1.getName(), addFilm1.getName()),
                filmStorage.getFilm(addFilm1.getId()).getLikes(), hasItem(addUser1.getId()));
    }

    @Test
    @DisplayName("Удаление лайка у фильма")
    void unlikeTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        User addUser1 = userStorage.addUser(user1);
        Film addFilm1 = filmStorage.addFilm(film1);
        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("Список лайков %s пуст", addFilm1.getName()),
                filmStorage.getFilm(addFilm1.getId()).getLikes(), hasItem(addUser1.getId()));
        likesStorage.removeLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("Список лайков %s не пуст", addFilm1.getName()),
                filmStorage.getFilm(addFilm1.getId()).getLikes(), empty());
    }

    @Test
    @DisplayName("Получение списка лайков фильма")
    void getListOfLikes() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        User addUser1 = userStorage.addUser(user1);
        Film addFilm1 = filmStorage.addFilm(film1);
        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("Список лайков %s не содержит id %s = %s",
                        addFilm1.getName(), addUser1.getName(), addUser1.getId()),
                likesStorage.getListOfLikes(addFilm1.getId()), contains(addUser1.getId()));
    }

    @Test
    @DisplayName("Получение списка лучших фильмов по лайкам")
    void getTheBestFilmsTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1992, 1, 1))
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film film2 = Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(1961, 1, 1))
                .duration(109)
                .rate(5)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        Film addFilm1 = filmStorage.addFilm(film1);
        Film addFilm2 = filmStorage.addFilm(film2);
        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        likesStorage.addLike(addFilm1.getId(), addUser2.getId());
        assertThat("Список лучших фильмов отличается от [1, 2]",
                likesStorage.getTheBestFilms(5), contains(addFilm1.getId(), addFilm2.getId()));
        assertThat("Список лучших фильмов отличается от [1]",
                likesStorage.getTheBestFilms(1), hasItem(addFilm1.getId()));
    }

    @Test
    @DisplayName("Получение списка жанров")
    void getGenresTest() {
        Genre genre = Genre.builder()
                .id(6)
                .name("Боевик")
                .build();
        assertThat(genreStorage.getGenres(), hasSize(6));
        assertThat(genreStorage.getGenres(), hasItem(genre));
    }

    @Test
    @DisplayName("Получение жанра по id")
    void getGenreByIdTest() {
        Genre genre1 = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();
        Genre genre6 = Genre.builder()
                .id(6)
                .name("Боевик")
                .build();
        assertThat(genreStorage.getGenreById(1), equalTo(genre1));
        assertThat(genreStorage.getGenreById(6), equalTo(genre6));
    }

    @Test
    @DisplayName("Добавление жанра к фильму")
    void addGenreTest() {
        Genre genre1 = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film addFilm1 = filmStorage.addFilm(film1);
        filmGenreLineStorage.addGenres(Set.of(genre1), addFilm1.getId());
        assertThat(filmStorage.getFilm(addFilm1.getId()).getGenres(), hasItem(genre1));
    }

    @Test
    @DisplayName("Получение списка жанров фильма")
    void getListOfGenresTest() {
        Genre genre2 = Genre.builder()
                .id(2)
                .name("Драма")
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(genre2))
                .build();
        Film addFilm1 = filmStorage.addFilm(film1);
        assertThat(filmGenreLineStorage.getListOfGenres(addFilm1.getId()), hasItem(genre2.getId()));
    }

    @Test
    @DisplayName("Удаление жанров у фильма")
    void deleteGenreTest() {
        Genre genre2 = Genre.builder()
                .id(2)
                .name("Драма")
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(Set.of(genre2))
                .build();
        Film addFilm1 = filmStorage.addFilm(film1);
        filmGenreLineStorage.deleteGenres(addFilm1.getId());
        assertThat(filmGenreLineStorage.getListOfGenres(addFilm1.getId()), empty());
    }

    @Test
    @DisplayName("Получение списка рейтингов MPA")
    void getMpaRatingTest() {
        MpaRating mpa = MpaRating.builder()
                .id(1)
                .name("G")
                .build();
        assertThat(mpaStorage.getMpaRating(), hasSize(5));
        assertThat(mpaStorage.getMpaRating(), hasItem(mpa));
    }

    @Test
    @DisplayName("Получение рейтинга MPA по id")
    void getMpaRatingById() {
        MpaRating mpa1 = MpaRating.builder()
                .id(1)
                .name("G")
                .build();
        MpaRating mpa5 = MpaRating.builder()
                .id(5)
                .name("NC-17")
                .build();
        assertThat(mpaStorage.getMpaRatingById(1), equalTo(mpa1));
        assertThat(mpaStorage.getMpaRatingById(5), equalTo(mpa5));
    }

}