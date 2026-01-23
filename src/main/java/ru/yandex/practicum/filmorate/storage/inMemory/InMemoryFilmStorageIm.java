package ru.yandex.practicum.filmorate.storage.inMemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorageIm;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class InMemoryFilmStorageIm implements FilmStorageIm {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorageIm.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private int idGen = 1;

    @Autowired
    public InMemoryFilmStorageIm() {
    }

    @Override
    public Film getFilm(Integer filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Такого фильма не существует!, {}", filmId);
            throw new NotFoundException("Такого фильма не существует!");
        }
        return films.get(filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("Количество фильмов всего: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addNewFilm(Film film) {
        film.setId(idGen++);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм, {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilm(film.getId());
        films.put(film.getId(), film);
        log.info("Фильм обновлен - , {}", film);
        return film;
    }


}
