package ru.yandex.practicum.filmorate.interfaces.dal;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaRatingStorage {
    Collection<MpaRating> getMpaRating();

    MpaRating getMpaRatingById(int mpaId);
}