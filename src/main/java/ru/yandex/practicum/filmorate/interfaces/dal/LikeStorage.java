package ru.yandex.practicum.filmorate.interfaces.dal;

import java.util.HashSet;

public interface LikeStorage {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    HashSet<Integer> getListOfLikes(int filmId);

    HashSet<Integer> getTheBestFilms(int count);

}
