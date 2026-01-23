package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmGenres {
    @NotNull
    @PositiveOrZero(message = "id фильма не может быть отрицательным числом")
    private int filmId;
    @NotNull
    @PositiveOrZero(message = "id жанра не может быть отрицательным числом")
    private int genreId;
}