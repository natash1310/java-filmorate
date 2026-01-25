package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenresStorage {
    void addGenres(Set<Genre> genres, int filmId);

    void deleteGenres(int filmId);

    Set<Genre> getGenresByFilmId(int id);

}
