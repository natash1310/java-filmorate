package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Genre {
    private int id;
    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;
}
