package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.interfaces.dal.MpaRatingStorage;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingStorage mpaStorage;

    public Collection<MpaRating> getMpaRating() {
        return mpaStorage.getMpaRating();
    }

    public MpaRating getMpaById(int id) {
        return mpaStorage.getMpaRatingById(id);
    }
}
