package ru.yandex.practicum.filmorate.interfaces.dal;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface FilmGenresStorage {
    void addGenres(Set<Genre> genres, int filmId);

    void deleteGenres(int filmId);

    List<Integer> getListOfGenres(int id);

}
