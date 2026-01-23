package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable("id") @Positive Integer id) {
        return ResponseEntity.ok(filmStorage.getFilm(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilms() {
        log.info("Получен запрос на получение всех фильмов. Количество фильмов: {}", filmStorage.getAllFilms().size());
        return ResponseEntity.ok(filmStorage.getAllFilms());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма: {}", film.getName());
        filmService.addFilm(film);
        return ResponseEntity.ok(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма с id = {}", film.getId());
        return ResponseEntity.ok(filmService.updateFilm(film));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getMostPopularFilms(@RequestParam(defaultValue = "10", required = false)
                                                          @Positive Integer count) {
        return ResponseEntity.ok(filmService.getMostPopularFilms(count));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable("id") @Positive Integer id,
                                        @PathVariable("userId") @Positive Integer userId) {
        return ResponseEntity.ok(filmService.addLike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> removeLike(@PathVariable("id") @Positive Integer id,
                                           @PathVariable("userId") @Positive Integer userId) {
        return ResponseEntity.ok(filmService.removeLike(id, userId));
    }
}