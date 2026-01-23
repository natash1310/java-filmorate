package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.interfaces.dal.FilmGenresStorage;
import ru.yandex.practicum.filmorate.interfaces.dal.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;
    private final FilmGenresStorage filmGenresStorage;

    public Collection<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    public Set<Genre> getListOfGenres(int id) {

        return filmGenresStorage.getListOfGenres(id).stream()
                .map(genreStorage::getGenreById)
                .collect(Collectors.toSet());
    }
}
