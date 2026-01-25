package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.List;

public interface LikeStorage {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    HashSet<Integer> getListOfLikes(int filmId);

    List<Film> getTheBestFilms(int count);

}
