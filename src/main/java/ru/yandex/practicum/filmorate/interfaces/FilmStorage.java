package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film getFilm(Integer id);

    List<Film> getAllFilms();

    Film addNewFilm(Film film);

    Film updateFilm(Film film);

}
