package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.interfaces.FilmGenresStorage;
import ru.yandex.practicum.filmorate.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;
    private final FilmGenresStorage filmGenresStorage;

    public Collection<Genre> getGenres() {
        return genreStorage.addGenresToFilm();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    public Set<Genre> getGenresByFilmId(int id) {
        return filmGenresStorage.getGenresByFilmId(id);
    }
}
