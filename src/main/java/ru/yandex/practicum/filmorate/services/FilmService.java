package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.LikeStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(int filmId) {
        return filmStorage.getFilm(filmId);
    }

    public Film addFilm(Film film) {
        checkPostFilmValidation(film);
        Film newFilm = filmStorage.addFilm(film);
        log.info("Добавлен фильм: {}", film);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        Film beforeFilm = filmStorage.getFilm(film.getId());
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Данные фильма: {} Обновлены на: {}", beforeFilm, updatedFilm);
        return updatedFilm;
    }

    public List<Film> getMostPopularFilms(int count) {
        return likeStorage.getTheBestFilms(count);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.getFilm(filmId);
        userStorage.getUser(userId);
        likeStorage.addLike(filmId, userId);
        log.info("Пользователь с id:{} поставил лайк фильму с id:{}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film currentFilm = filmStorage.getFilm(filmId);
        userStorage.getUser(userId);
        if (!currentFilm.getLikes()
                .contains(userId)) {
            log.error("У фильма с id{} Нет лайка от пользователя с id:{}", filmId, userId);
            throw new NotFoundException("У фильма нет лайка от этого пользователя");
        }
        likeStorage.removeLike(filmId, userId);
        log.info("Пользователь с id:{} удалил свой лайк фильму с id:{}", userId, filmId);
    }

    private void checkPostFilmValidation(Film film) {
        if (filmStorage.getAllFilms()
                .contains(film)) {
            log.error("Такой фильм уже есть!, {}", film);
            throw new ValidationException("Такой фильм уже есть!");
        } else if (!film.getReleaseDate()
                .isAfter(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза не должна быть раньше 28 декабря 1895!, {}", film);
            throw new ValidationException("Дата релиза не должна быть раньше 28 декабря 1895!");
        }
    }
}