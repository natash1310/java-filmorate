package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaRatingStorage {
    Collection<MpaRating> getMpaRating();

    MpaRating getMpaRatingById(int mpaId);
}