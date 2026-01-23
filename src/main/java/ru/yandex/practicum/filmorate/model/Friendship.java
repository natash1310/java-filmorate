package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Friendship {
    @NotNull
    @PositiveOrZero(message = "id пользователя не может быть отрицательным числом")
    private int userId;
    @NotNull
    @PositiveOrZero(message = "id пользователя не может быть отрицательным числом")
    private int friendId;
}

