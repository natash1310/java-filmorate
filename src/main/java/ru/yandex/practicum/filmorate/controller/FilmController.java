package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int globalId = 0;

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilms() {
        log.info("Получен запрос на получение всех фильмов. Количество фильмов: {}", films.size());
        return ResponseEntity.ok(films.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма: {}", film.getName());

        checkPostFilmValidation(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм успешно добавлен с id = {}", film.getId());
        return ResponseEntity.ok(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма с id = {}", film.getId());

        Film existingFilm = films.get(film.getId());
        if (existingFilm == null) {
            log.error("Ошибка обновления: фильм с id = {} не найден", film.getId());
            throw new ConditionsNotMetException("Фильм с id = " + film.getId() + " не найден");
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

        films.put(existingFilm.getId(), existingFilm);
        log.info("Фильм с id = {} успешно обновлён", film.getId());
        return ResponseEntity.ok(existingFilm);
    }

    private void checkPostFilmValidation(Film film) {
        if (films.containsValue(film)) {
            log.error("Такой фильм уже есть!, {}", film);
            throw new ValidationException("Такой фильм уже есть!");
        } else if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза не должна быть раньше 28 декабря 1895!, {}", film);
            throw new ValidationException("Дата релиза не должна быть раньше 28 декабря 1895!");
        }
    }

    private int getNextId() {
        return ++globalId;
    }
}