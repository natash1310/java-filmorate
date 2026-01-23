package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.dal.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.dal.LikeStorage;
import ru.yandex.practicum.filmorate.interfaces.dal.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private int idGen = 1;

    public Collection<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(int filmId) {
        return filmStorage.getFilm(filmId);
    }

    public Film addFilm(Film film) {
        checkPostFilmValidation(film);
        film.setId(idGen);
        filmStorage.addFilm(film);
        idGen++;
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        Film beforeFilm = filmStorage.getFilm(film.getId());
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Данные фильма: {} Обновлены на: {}", beforeFilm, updatedFilm);
        return updatedFilm;
    }

    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getAllFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
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
        if (!currentFilm.getLikes().contains(userId)) {
            log.error("У фильма с id{} Нет лайка от пользователя с id:{}", filmId, userId);
            throw new NotFoundException("У фильма нет лайка от этого пользователя");
        }
        likeStorage.removeLike(filmId, userId);
        log.info("Пользователь с id:{} удалил свой лайк фильму с id:{}", userId, filmId);
    }

    private void checkPostFilmValidation(Film film) {
        if (filmStorage.getAllFilms().contains(film)) {
            log.error("Такой фильм уже есть!, {}", film);
            throw new ValidationException("Такой фильм уже есть!");
        } else if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза не должна быть раньше 28 декабря 1895!, {}", film);
            throw new ValidationException("Дата релиза не должна быть раньше 28 декабря 1895!");
        }
    }

    private int compare(Film f0, Film f1) {
        return Integer.compare(f1.getLikes().size(), f0.getLikes().size());
    }

}