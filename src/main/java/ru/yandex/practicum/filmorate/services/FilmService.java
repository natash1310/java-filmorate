package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorageIm;
import ru.yandex.practicum.filmorate.interfaces.UserStorageIm;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorageIm filmStorageIm;
    private final UserStorageIm userStorageIm;

    public Film addFilm(Film film) {
        checkPostFilmValidation(film);
        filmStorageIm.addNewFilm(film);
        log.info("Фильм успешно добавлен с id = {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        Film existingFilm = filmStorageIm.getFilm(film.getId());
        if (existingFilm == null) {
            log.error("Ошибка обновления: фильм с id = {} не найден", film.getId());
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }

        if (film.getName() != null) {
            log.debug("Обновление названия фильма с '{}' на '{}'", existingFilm.getName(), film.getName());
            existingFilm.setName(film.getName());
        }

        if (film.getDescription() != null) {
            existingFilm.setDescription(film.getDescription());
        }

        if (film.getDuration() != null) {
            existingFilm.setDuration(film.getDuration());
        }

        if (film.getReleaseDate() != null) {
            checkPostFilmValidation(film);
            existingFilm.setReleaseDate(film.getReleaseDate());
        }

        filmStorageIm.updateFilm(existingFilm);
        log.info("Фильм с id = {} успешно обновлён", film.getId());
        return existingFilm;
    }

    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorageIm.getAllFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film addLike(Integer id, Integer userId) {
        Film currentFilm = filmStorageIm.getFilm(id);
        userStorageIm.getUser(userId);
        currentFilm.getLikes().add(userId);
        log.info("Пользователь с id:{} поставил лайк фильму с id:{}", userId, id);
        return currentFilm;
    }

    public Film removeLike(Integer id, Integer userId) {
        Film currentFilm = filmStorageIm.getFilm(id);
        userStorageIm.getUser(userId);
        if (!currentFilm.getLikes().contains(userId)) {
            log.error("У фильма с id{} Нет лайка от пользователя с id:{}", id, userId);
            throw new NotFoundException("У фильма нет лайка от этого пользователя");
        }
        currentFilm.getLikes().remove(userId);
        log.info("Пользователь с id:{} удалил свой лайк фильму с id:{}", userId, id);
        return currentFilm;
    }

    private void checkPostFilmValidation(Film film) {
        if (filmStorageIm.getAllFilms().contains(film)) {
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